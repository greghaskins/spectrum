package com.greghaskins.spectrum.runner;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public class Context<T, OuterType> {

    private final TestPlan<T> testPlan;
    private final Description description;
    private final ArrayList<Context<?, T>> innerContexts;
    private final InstanceFactory<T, OuterType> instanceFactory;
    private final ArrayList<Context<?, Void>> staticNestedContexts;

    public static <T> Context<T, Void> forClass(final Class<T> contextClass) {
        return new Context<T, Void>(contextClass, new DefaultConstructor<T>(contextClass));
    }

    private static <T, OuterType> Context<T, OuterType> forInnerClass(final Class<T> innerClass,
            final Class<OuterType> outerClass) {
        return new Context<T, OuterType>(innerClass, new NestedConstructor<T, OuterType>(innerClass, outerClass));
    }

    private Context(final Class<T> contextClass, final InstanceFactory<T, OuterType> factory) {
        instanceFactory = factory;
        description = ContextDescriber.makeDescription(contextClass);
        testPlan = TestPlanner.makeTestPlan(contextClass, description);

        innerContexts = new ArrayList<Context<?, T>>();
        staticNestedContexts = new ArrayList<Context<?, Void>>();

        for (final Class<?> nestedClass : contextClass.getDeclaredClasses()) {
            if (Modifier.isStatic(nestedClass.getModifiers())) {
                final Context<?, Void> context = Context.forClass(nestedClass);
                addChildContext(context, staticNestedContexts);
            } else {
                final Context<?, T> context = Context.forInnerClass(nestedClass, contextClass);
                addChildContext(context, innerContexts);
            }
        }

    }

    private <TOuter> void addChildContext(final Context<?, TOuter> context, final ArrayList<Context<?, TOuter>> childContexts) {
        childContexts.add(context);
        description.addChild(context.getDescription());
    }

    public void run(final RunNotifier notifier) {
        final T instance = instanceFactory.makeInstance(null);
        testPlan.runInContext(instance, notifier);
        for (final Context<?, T> context : innerContexts) {
            context.execute(instance, notifier);
        }
        for (final Context<?,Void> context : staticNestedContexts) {
            context.run(notifier);
        }
    }

    public void execute(final OuterType outerInstance, final RunNotifier notifier) {
        final T instance = instanceFactory.makeInstance(outerInstance);
        testPlan.runInContext(instance, notifier);
    }

    public Description getDescription() {
        return description;
    }

}

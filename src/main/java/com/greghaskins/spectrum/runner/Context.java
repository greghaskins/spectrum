package com.greghaskins.spectrum.runner;

import java.util.ArrayList;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public class Context<T, OuterType> {

    private final TestPlan<T> testPlan;
    private final Description description;
    private final Class<T> type;
    private final ArrayList<Context<?, T>> childContexts;
    private final InstanceFactory<T, OuterType> instanceFactory;

    public static <T> Context<T, Void> forClass(final Class<T> contextClass) {
        return new Context<T, Void>(contextClass, new DefaultConstructor<T>(contextClass));
    }

    private static <T, OuterType> Context<T, OuterType> forInnerClass(final Class<T> innerClass,
            final Class<OuterType> outerClass) {
        return new Context<T, OuterType>(innerClass, new NestedConstructor<T, OuterType>(innerClass, outerClass));
    }

    private Context(final Class<T> contextClass, final InstanceFactory<T, OuterType> factory) {
        type = contextClass;
        instanceFactory = factory;
        description = ContextDescriber.makeDescription(contextClass);
        testPlan = TestPlanner.makeTestPlan(contextClass, description);

        childContexts = new ArrayList<Context<?, T>>();
        for (final Class<?> nestedClass : contextClass.getDeclaredClasses()) {
            final Context<?, T> context = Context.forInnerClass(nestedClass, contextClass);
            childContexts.add(context);
        }

    }

    public void run(final RunNotifier notifier) {
        final T instance = instanceFactory.makeInstance(null);
        testPlan.runInContext(instance, notifier);
        for (final Context<?, T> context : childContexts) {
            context.execute(instance, notifier);
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

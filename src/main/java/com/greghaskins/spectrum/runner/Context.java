package com.greghaskins.spectrum.runner;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public class Context<T> {

    private final TestPlan<T> testPlan;
    private final Description description;
    private final Class<T> type;

    public static <T> Context<T> forClass(final Class<T> contextClass) {
        return new Context<T>(contextClass);
    }

    private Context(final Class<T> contextClass) {
        type = contextClass;
        description = ContextDescriber.makeDescription(contextClass);
        testPlan = TestPlanner.makeTestPlan(contextClass, description);
    }

    public void run(final RunNotifier notifier) {
        final T instance = ReflectiveConstructor.makeInstance(type);
        testPlan.runInContext(instance, notifier);
    }

    public Description getDescription() {
        return description;
    }

}

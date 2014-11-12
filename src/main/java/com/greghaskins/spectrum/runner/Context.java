package com.greghaskins.spectrum.runner;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public class Context<T> {

    private final TestPlan<T> tests;
    private final Description description;
    private final Class<T> contextClass;

    public static <T> Context<T> forClass(final Class<T> contextClass) {
        return new Context<T>(contextClass);
    }

    private Context(final Class<T> contextClass) {
        this.contextClass = contextClass;
        description = ContextDescriber.makeDescription(contextClass);
        tests = TestFinder.findTests(contextClass, description);
    }

    public void run(final RunNotifier notifier) {
        final T instance = ReflectiveConstructor.makeInstance(contextClass);
        tests.runInContext(instance, notifier);
    }

    public Description getDescription() {
        return description;
    }

}

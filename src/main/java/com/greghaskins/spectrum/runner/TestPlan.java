package com.greghaskins.spectrum.runner;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

class TestPlan<T> {

    private final Description description;
    private final List<Test<T>> tests;
    private InstanceMethod<T> setupMethod;

    public TestPlan(final Class<T> type, final Description description) {
        this.description = description;
        this.tests = new ArrayList<Test<T>>();
    }

    public void addTest(final Test<T> test) {
        tests.add(test);
        description.addChild(test.getDescription());
    }

    public void addSetup(final InstanceMethod<T> setupMethod) {
        this.setupMethod = setupMethod;
    }

    public void runInContext(final T instance, final RunNotifier notifier) {
        for (final Test<T> test : tests) {
            try {
                setupContext(instance);
                test.run(instance, notifier);
            } catch (final Throwable e) {
                notifier.fireTestFailure(new Failure(test.getDescription(), e));
            }
        }
    }

    private void setupContext(final T instance) throws Throwable {
        if (setupMethod != null) {
            setupMethod.invokeWithInstance(instance);
        }
    }

}

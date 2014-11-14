package com.greghaskins.spectrum.runner;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

class TestPlan<T> {

    private final Description description;
    private final List<Test<T>> tests;
    private final List<InstanceMethod<T>> setupMethods;

    public TestPlan(final Description description) {
        this.description = description;
        this.tests = new ArrayList<Test<T>>();
        this.setupMethods = new ArrayList<InstanceMethod<T>>();
    }

    public void addTest(final Test<T> test) {
        tests.add(test);
        description.addChild(test.getDescription());
    }

    public void addSetup(final InstanceMethod<T> setupMethod) {
        setupMethods.add(setupMethod);
    }

    public void runInContext(final T targetInstance, final RunNotifier notifier) {
        for (final Test<T> test : tests) {
            try {
                setup(targetInstance);
                test.run(targetInstance, notifier);
            } catch (final Throwable e) {
                notifier.fireTestFailure(new Failure(test.getDescription(), e));
            }
        }
    }

    private void setup(final T targetInstance) throws Throwable {
        for (final InstanceMethod<T> setupMethod : setupMethods) {
            setupMethod.invokeWithInstance(targetInstance);
        }
    }

}

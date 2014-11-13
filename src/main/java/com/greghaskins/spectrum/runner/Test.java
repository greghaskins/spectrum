package com.greghaskins.spectrum.runner;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

class Test<T> {

    private final Description description;
    private final InstanceMethod<T> method;

    Test(final String testName, final String contextName, final InstanceMethod<T> instanceMethod) {
        this.method = instanceMethod;
        this.description = Description.createTestDescription(contextName, testName);
    }

    public Description getDescription() {
        return description;
    }

    public void run(final T contextInstance, final RunNotifier notifier) {
        notifier.fireTestStarted(description);
        try {
            method.invokeWithInstance(contextInstance);
        } catch (final Throwable e) {
            notifier.fireTestFailure(new Failure(description, e));
        }
        notifier.fireTestFinished(description);
    }

}

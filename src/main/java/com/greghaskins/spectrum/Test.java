package com.greghaskins.spectrum;

import java.lang.reflect.Method;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import com.greghaskins.spectrum.Spectrum.Describe;
import com.greghaskins.spectrum.Spectrum.It;

class Test<T> {

    private final Description description;
    private final Method method;

    Test(final Class<T> contextClass, final Method method) {
        this.method = method;
        final String contextName = contextClass.getAnnotation(Describe.class).value();
        final String testName = method.getAnnotation(It.class).value();
        description = Description.createTestDescription(contextName , testName);
    }

    public Description getDescription() {
        return description;
    }

    void run(final T contextInstance, final RunNotifier notifier) {
        notifier.fireTestStarted(description);

        try {
            invokeMethodWithInstance(contextInstance);
        } catch (final Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            notifier.fireTestFailure(new Failure(description, null));
        }


        notifier.fireTestFinished(description);
    }

    private void invokeMethodWithInstance(final T contextInstance) throws Exception {
        method.setAccessible(true);
        method.invoke(contextInstance);
    }


}

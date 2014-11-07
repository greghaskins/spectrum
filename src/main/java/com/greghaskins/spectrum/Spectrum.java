package com.greghaskins.spectrum;

import java.lang.reflect.Method;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import com.greghaskins.spectrum.errors.MissingDescribeAnnotationError;

public class Spectrum extends Runner {

    private final Description suiteDescription;

    public Spectrum(final Class<?> testClass) throws InitializationError {
        suiteDescription = Description.createSuiteDescription(getSuiteName(testClass));
        addChildTests(testClass, suiteDescription);
    }

    private String getSuiteName(final Class<?> testClass) throws MissingDescribeAnnotationError {
        final Describe describe = testClass.getAnnotation(Describe.class);
        if (describe == null) {
            throw new MissingDescribeAnnotationError(testClass);
        }
        return describe.value();
    }

    private void addChildTests(final Class<?> contextClass, final Description contextDescription) {
        final Method[] methods = contextClass.getDeclaredMethods();
        for (final Method method : methods) {
            final It annotation = method.getAnnotation(It.class);
            final String testName = annotation.value();
            contextDescription.addChild(Description.createTestDescription(contextDescription.getClassName(), testName));
        }
    }

    @Override
    public Description getDescription() {
        return suiteDescription;
    }

    @Override
    public void run(final RunNotifier notifier) {
        notifier.fireTestStarted(suiteDescription);
        notifier.fireTestFinished(suiteDescription);
    }

}

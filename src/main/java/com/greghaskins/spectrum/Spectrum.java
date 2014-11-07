package com.greghaskins.spectrum;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import com.greghaskins.spectrum.errors.MissingDescribeAnnotationError;

public class Spectrum extends Runner {

    private final Description suiteDescription;

    public Spectrum(final Class<?> testClass) throws InitializationError {
        suiteDescription = Description.createSuiteDescription(getSuiteName(testClass));
    }

    private String getSuiteName(final Class<?> testClass) throws MissingDescribeAnnotationError {
        final Describe describe = testClass.getAnnotation(Describe.class);
        if (describe == null) {
            throw new MissingDescribeAnnotationError(testClass);
        }
        return describe.value();
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

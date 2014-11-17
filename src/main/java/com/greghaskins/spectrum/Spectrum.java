package com.greghaskins.spectrum;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public class Spectrum extends Runner {

    public Spectrum(final Class<?> testClass) {
    }

    @Override
    public Description getDescription() {
        return null;
    }

    @Override
    public void run(final RunNotifier notifier) {
    }

}

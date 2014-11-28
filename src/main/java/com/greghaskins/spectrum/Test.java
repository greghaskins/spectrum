package com.greghaskins.spectrum;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import com.greghaskins.spectrum.Spectrum.Block;

class Test implements Executable {
    private final Description description;
    private final Block block;

    public Test(final Description description, final Block block) {
        this.description = description;
        this.block = block;
    }

    @Override
    public void execute(final RunNotifier notifier) {
        notifier.fireTestStarted(description);
        try {
            block.run();
        } catch (final Throwable e) {
            notifier.fireTestFailure(new Failure(description, e));
        }
        notifier.fireTestFinished(description);
    }
}

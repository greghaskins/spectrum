package com.greghaskins.spectrum;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import com.greghaskins.spectrum.Spectrum.Block;

class BlockExecutable implements Executable {

    private final Block block;
    private final Description description;

    public BlockExecutable(final Description description, final Block block) {
        this.description = description;
        this.block = block;
    }

    @Override
    public void execute(final RunNotifier notifier) {
        try {
            block.run();
        } catch (final Throwable e) {
            notifier.fireTestFailure(new Failure(description, e));
        }
    }

}

package com.greghaskins.spectrum;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import com.greghaskins.spectrum.Spectrum.Block;

class Context {

    private static class Test {
        public Block block;
        public Description description;
    }

    private static class CompositeBlock implements Block {

        private final Iterable<Block> blocks;

        public CompositeBlock(final Iterable<Block> blocks) {
            this.blocks = blocks;
        }

        @Override
        public void run() throws Throwable {
            for (final Block block : blocks) {
                block.run();
            }
        }

    }

    private final Description description;
    private final List<Block> setupBlocks;
    private final List<Test> tests;
    private final List<Context> childContexts;

    public Context(final Description description) {
        this.description = description;
        setupBlocks = new ArrayList<Block>();
        tests = new ArrayList<Test>();
        childContexts = new ArrayList<Context>();
    }

    public void addSetup(final Block block) {
        setupBlocks.add(block);
    }

    public void addTest(final String behavior, final Block block) {
        final Description testDescription = Description.createTestDescription(description.getClassName(), behavior);
        final Test test = new Test();
        test.block = block;
        test.description = testDescription;
        tests.add(test);
        description.addChild(testDescription);
    }

    public void execute(final RunNotifier notifier) {
        for (final Test test : tests) {
            notifier.fireTestStarted(test.description);
            try {
                setup();
                test.block.run();
            } catch (final Throwable e) {
                notifier.fireTestFailure(new Failure(test.description, e));
            }
            notifier.fireTestFinished(test.description);
        }
        for (final Context context : childContexts) {
            context.execute(notifier);
        }
    }

    private void setup() throws Throwable {
        for (final Block setupBlock : setupBlocks) {
            setupBlock.run();
        }
    }

    public void addChild(final Context childContext) {
        description.addChild(childContext.description);
        childContext.addSetup(new CompositeBlock(setupBlocks));
        childContexts.add(childContext);
    }

}

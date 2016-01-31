package com.greghaskins.spectrum;

import static java.util.Arrays.asList;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import com.greghaskins.spectrum.Spectrum.Block;

class Context implements Executable {

    private final Description description;
    private final List<Block> setupBlocks;
    private final List<Block> teardownBlocks;
    private final List<Block> contextSetupBlocks;
    private final List<Block> contextTeardownBlocks;
    private final Deque<Executable> executables;

    public Context(final Description description) {
        this.description = description;
        this.setupBlocks = new ArrayList<Block>();
        this.teardownBlocks = new ArrayList<Block>();
        this.contextSetupBlocks = new ArrayList<Block>();
        this.contextTeardownBlocks = new ArrayList<Block>();

        this.executables = new ArrayDeque<Executable>();
    }

    @Override
    public void execute(final RunNotifier notifier) {
        if (descriptionHasAnyTests(this.description)) {
            addExecutablesForFixtureLevelSetupAndTeardown();
        } else {
            notifier.fireTestIgnored(this.description);
        }
        for (final Executable child : this.executables) {
            child.execute(notifier);
        }
    }

    private boolean descriptionHasAnyTests(final Description currentDescription) {
        for (final Description child : currentDescription.getChildren()) {
            if (isTest(child) || descriptionHasAnyTests(child)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTest(final Description child) {
        return child.getMethodName() != null;
    }

    private void addExecutablesForFixtureLevelSetupAndTeardown() {
        this.executables.addFirst(new BlockExecutable(this.description, new CompositeBlock(this.contextSetupBlocks)));
        this.executables.addLast(new BlockExecutable(this.description, new CompositeBlock(this.contextTeardownBlocks)));
    }

    public void addTestSetup(final Block block) {
        this.setupBlocks.add(block);
    }

    public void addTestTeardown(final Block block) {
        this.teardownBlocks.add(block);
    }

    public void addContextSetup(final Block block) {
        this.contextSetupBlocks.add(new RunOnceBlock(block));
    }

    public void addContextTeardown(final Block block) {
        this.contextTeardownBlocks.add(block);
    }

    public void addTest(final String behavior, final Block block) {
        final Description testDescription = Description.createTestDescription(this.description.getClassName(), behavior);
        final CompositeBlock testBlock = putTestBlockInContext(block);
        final Test test = new Test(testDescription, testBlock);
        this.description.addChild(testDescription);
        this.executables.add(test);
    }

    private CompositeBlock putTestBlockInContext(final Block testBlock) {
		return new CompositeBlock(asList(new CompositeBlock(this.contextSetupBlocks), new CompositeBlock(this.setupBlocks), testBlock, new CompositeBlock(this.teardownBlocks)));
    }

    public void addChild(final Context childContext) {
        this.description.addChild(childContext.description);
        childContext.addContextSetup(new CompositeBlock(this.contextSetupBlocks));
        childContext.addTestSetup(new CompositeBlock(this.setupBlocks));
        childContext.addTestTeardown(new CompositeBlock(this.teardownBlocks));
        this.executables.add(childContext);
    }


}

package com.greghaskins.spectrum;

import java.util.ArrayDeque;
import java.util.Deque;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import com.greghaskins.spectrum.Spectrum.Block;

class TestPlan {

    private final Deque<Context> contexts;

    public TestPlan(final Description rootDescription) {
        final Context rootContext = new Context(rootDescription);
        contexts = new ArrayDeque<Context>();
        contexts.push(rootContext);
    }

    public void addContext(final String context, final Block block) {
        final Context newContext = new Context(Description.createSuiteDescription(context));
        contexts.peek().addChild(newContext);

        contexts.push(newContext);
        try {
            block.run();
        } catch (final Throwable e) {
            addTest("encountered an error", new FailingBlock(e));
        }
        contexts.pop();
    }

    public void addTest(final String behavior, final Block block) {
        contexts.peek().addTest(behavior, block);
    }

    public void addSetup(final Block block) {
        contexts.peek().addSetup(block);
    }

    public void addTeardown(final Block block) {
        contexts.peek().addTeardown(block);
    }

    public void addFixtureSetup(final Block block) {
        contexts.peek().addFixtureSetup(block);
    }

    public void addFixtureTeardown(final Block block) {
        contexts.peek().addFixtureTeardown(block);
    }

    public void execute(final RunNotifier notifier) {
        contexts.peek().execute(notifier);
    }


}

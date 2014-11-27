package com.greghaskins.spectrum;

import java.util.ArrayDeque;
import java.util.Deque;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import com.greghaskins.spectrum.Spectrum.Block;

class TestPlan {

    private static class FailingBlock implements Block {
        private final Throwable exceptionToThrow;

        private FailingBlock(final Throwable exceptionToThrow) {
            this.exceptionToThrow = exceptionToThrow;
        }

        @Override
        public void run() throws Throwable {
            throw exceptionToThrow;
        }
    }

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

    public void execute(final RunNotifier notifier){
        contexts.peek().execute(notifier);
    }


}

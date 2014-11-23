package com.greghaskins.spectrum;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import com.greghaskins.spectrum.Spectrum.Block;

class TestPlan {

    private static class Test {
        public Block block;
        public Description description;
    }

    private static class Context {
        private final Description description;

        public Context(final Description description) {
            this.description = description;
        }

    }

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
    private final List<Test> tests;

    public TestPlan(final Description rootDescription) {
        final Context rootContext = new Context(rootDescription);
        contexts = new ArrayDeque<Context>();
        contexts.push(rootContext);

        tests = new ArrayList<Test>();
    }

    public void addContext(final String context, final Block block) {
        final Context newContext = new Context(Description.createSuiteDescription(context));
        contexts.peek().description.addChild(newContext.description);

        contexts.push(newContext);
        try {
            block.run();
        } catch (final Throwable e) {
            addTest("encountered an error", new FailingBlock(e));
        }
        contexts.pop();
    }

    public void addTest(final String behavior, final Block block) {
        final Test test = new Test();
        test.block = block;
        final Description currentDescription = contexts.peek().description;
        test.description = Description.createTestDescription(currentDescription.getClassName(), behavior);
        tests.add(test);
        currentDescription.addChild(test.description);
    }

    public void execute(final RunNotifier notifier) {
        for (final Test test : tests) {
            notifier.fireTestStarted(test.description);
            try {
                test.block.run();
            } catch (final Throwable e) {
                notifier.fireTestFailure(new Failure(test.description, e));
            }
            notifier.fireTestFinished(test.description);
        }
    }

}

package com.greghaskins.spectrum;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import com.greghaskins.spectrum.Spectrum.Block;

class TestPlan {

    static class Test {
        public Block block;
        public Description description;
    }

    private final List<Test> tests;
    private Description currentDescription;

    private final Description suiteDescription;

    public TestPlan(final Description rootDescription) {
        suiteDescription = rootDescription;
        tests = new ArrayList<Test>();
    }

    public void addContext(final String context, final Block block) {
        currentDescription = Description.createSuiteDescription(context);
        suiteDescription.addChild(currentDescription);
        try {
            block.run();
        } catch (final Throwable exceptionFromDescribeBlock) {
            final Test failingTest = new Test();
            failingTest.description = Description.createTestDescription(context, "encountered an error");
            failingTest.block = new Block() {

                @Override
                public void run() throws Throwable {
                    throw exceptionFromDescribeBlock;
                }

            };
            tests.add(failingTest);
            currentDescription.addChild(failingTest.description);
        }
    }

    public void addTest(final String behavior, final Block block) {
        final Test test = new Test();
        test.block = block;
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

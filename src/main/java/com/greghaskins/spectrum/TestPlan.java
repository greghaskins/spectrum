package com.greghaskins.spectrum;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;

import com.greghaskins.spectrum.Spectrum.Block;
import com.greghaskins.spectrum.Spectrum.Test;

class TestPlan {

    private final List<Test> currentTests;
    private Description currentDescription;

    private final Description suiteDescription;

    public TestPlan(final Description rootDescription) {
        suiteDescription = rootDescription;
        currentTests = new ArrayList<Spectrum.Test>();
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
            currentTests.add(failingTest);
            currentDescription.addChild(failingTest.description);
        }
    }



    public void addTest(final String behavior, final Block block) {
        final Test test = new Test();
        test.block = block;
        test.description = Description.createTestDescription(currentDescription.getClassName(), behavior);
        currentTests.add(test);
        currentDescription.addChild(test.description);
    }

    public List<Test> getTests() {
        return currentTests;
    }


}

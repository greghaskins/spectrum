package com.greghaskins.spectrum;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class Spectrum extends Runner {

    public static interface Block {
        void run() throws Throwable;
    }

    private static Description currentDescription;

    public static void describe(final String context, final Block block) {
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
        }
    }

    public static void it(final String behavior, final Block block) {
        final Test test = new Test();
        test.block = block;
        test.description = Description.createTestDescription(currentDescription.getClassName(), behavior);
        currentTests.add(test);
        currentDescription.addChild(test.description);
    }

    private static List<Test> currentTests;
    private static Description suiteDescription;

    private final List<Test> tests;

    private static class Test {
        public Block block;
        public Description description;
    }

    public Spectrum(final Class<?> testClass) {
        suiteDescription = Description.createSuiteDescription(testClass);
        tests = prepareSpec(testClass);
    }

    @Override
    public Description getDescription() {
        return suiteDescription;
    }

    @Override
    public void run(final RunNotifier notifier) {
        for (final Test test : tests) {
            notifier.fireTestStarted(null);
            try {
                test.block.run();
            } catch (final Throwable e) {
                notifier.fireTestFailure(new Failure(test.description, e));
            }
            notifier.fireTestFinished(null);
        }
    }

    private List<Test> prepareSpec(final Class<?> specClass) {
        currentTests = new ArrayList<Test>();
        final List<Test> testList;
        try {
            final Constructor<?> constructor = specClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        } catch (final InvocationTargetException e) {
            throw new SpecInitializationError(e.getTargetException());
        } catch (final Exception e) {
            throw new SpecInitializationError(e);
        } finally {
            testList = new ArrayList<Test>(currentTests);
            currentTests = null;
        }
        return testList;
    }

}

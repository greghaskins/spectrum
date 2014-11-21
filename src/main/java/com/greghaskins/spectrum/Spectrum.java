package com.greghaskins.spectrum;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
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
        } catch (final Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void it(final String behavior, final Block block) {
        currentTests.add(block);
        final Description description = Description.createTestDescription(currentDescription.getClassName(), behavior);
        currentDescription.addChild(description);
    }

    private static List<Block> currentTests;
    private static Description suiteDescription;

    private final List<Block> tests;

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
        for (final Block block : tests) {
            notifier.fireTestStarted(null);
            notifier.fireTestFinished(null);
        }
    }

    private List<Block> prepareSpec(final Class<?> specClass) {
        currentTests = new ArrayList<Block>();
        final List<Block> testList;
        try {
            final Constructor<?> constructor = specClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        } catch (final InvocationTargetException e) {
            throw new SpecInitializationError(e.getTargetException());
        } catch (final Exception e) {
            throw new SpecInitializationError(e);
        } finally {
            testList = new ArrayList<Block>(currentTests);
            currentTests = null;
        }
        return testList;
    }

}

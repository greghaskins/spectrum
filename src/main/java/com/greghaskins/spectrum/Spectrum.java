package com.greghaskins.spectrum;

import java.lang.reflect.Constructor;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public class Spectrum extends Runner {

    public static interface Block {
        void run() throws Throwable;
    }

    public static void describe(final String context, final Block block) {
        try {
            suiteName = context;
            suiteDescription = Description.createSuiteDescription(suiteName);
            block.run();
        } catch (final Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void it(final String behavior, final Block block) {
        currentTest = block;
        testName = behavior;
        final Description description = Description.createTestDescription(suiteName, testName);
        suiteDescription.addChild(description);
    }

    private static Block currentTest;
    private static String suiteName;
    private static Description suiteDescription;
    private static String testName;

    private final Block test;

    public Spectrum(final Class<?> testClass) {
        test = prepareSpec(testClass);
    }

    @Override
    public Description getDescription() {
        return suiteDescription;
    }

    @Override
    public void run(final RunNotifier notifier) {
        if(test != null){
            notifier.fireTestStarted(null);
            notifier.fireTestFinished(null);
        }
    }

    private Block prepareSpec(final Class<?> specClass) {
        Block test;
        currentTest = null;
        try {
            final Constructor<?> constructor = specClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            test = currentTest;
            currentTest = null;
        }
        return test;
    }

}

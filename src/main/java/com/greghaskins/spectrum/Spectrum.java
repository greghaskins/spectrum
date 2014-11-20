package com.greghaskins.spectrum;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public class Spectrum extends Runner {

    public static interface Block {
        void run() throws Throwable;
    }

    private static Description currentDescription;

    public static void describe(final String context, final Block block) {
        try {
            currentDescription = Description.createSuiteDescription(context);
            suiteDescription.addChild(currentDescription);
            block.run();
        } catch (final Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void it(final String behavior, final Block block) {
        currentTest = block;
        testName = behavior;
        final Description description = Description.createTestDescription(currentDescription.getClassName(), testName);
        currentDescription.addChild(description);
    }

    private static Block currentTest;
    private static Description suiteDescription;
    private static String testName;

    private final Block test;

    public Spectrum(final Class<?> testClass) {
        suiteDescription = Description.createSuiteDescription(testClass);
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
        } catch (final InvocationTargetException e) {
            throw new SpecInitializationError(e.getTargetException());
        } catch (final Exception e) {
            throw new SpecInitializationError(e);
        } finally {
            test = currentTest;
            currentTest = null;
        }
        return test;
    }

}

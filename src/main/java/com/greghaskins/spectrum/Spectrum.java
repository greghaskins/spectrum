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
            block.run();
        } catch (final Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void it(final String behavior, final Block block) {
        currentTest = block;
    }

    private static Block currentTest;
    private final Class<?> testClass;

    public Spectrum(final Class<?> testClass) {
        this.testClass = testClass;
    }

    @Override
    public Description getDescription() {
        return Description.createSuiteDescription("blah");
    }

    @Override
    public void run(final RunNotifier notifier) {

        try {
            final Constructor<?> constructor = testClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        if(currentTest != null){
            notifier.fireTestStarted(null);
            notifier.fireTestFinished(null);
        }
        currentTest = null;
    }

}

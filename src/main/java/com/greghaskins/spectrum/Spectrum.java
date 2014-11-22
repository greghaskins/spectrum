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

    public static void describe(final String context, final Block block) {
        currentTestPlan.addContext(context, block);
    }

    public static void it(final String behavior, final Block block) {
        currentTestPlan.addTest(behavior, block);
    }

    private final List<Test> tests;
    private final Description description;

    private static TestPlan currentTestPlan;

    static class Test {
        public Block block;
        public Description description;
    }

    public Spectrum(final Class<?> testClass) {
        description = Description.createSuiteDescription(testClass);
        tests = prepareSpec(testClass, description);
    }

    @Override
    public Description getDescription() {
        return description;
    }

    @Override
    public void run(final RunNotifier notifier) {
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

    private List<Test> prepareSpec(final Class<?> specClass, final Description rootDescription) {
        currentTestPlan = new TestPlan(rootDescription);
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
            testList = new ArrayList<Test>(currentTestPlan.getTests());
            currentTestPlan = null;
        }
        return testList;
    }

}

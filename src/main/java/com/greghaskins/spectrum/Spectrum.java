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

    public static void describe(final String context, final Block block) {
        currentTestPlan.addContext(context, block);
    }

    public static void it(final String behavior, final Block block) {
        currentTestPlan.addTest(behavior, block);
    }

    public static void beforeEach(final Block block){
        currentTestPlan.addSetup(block);
    }

    private final Description description;
    private final TestPlan plan;

    private static TestPlan currentTestPlan;

    public Spectrum(final Class<?> testClass) {
        description = Description.createSuiteDescription(testClass);
        plan = prepareSpec(testClass, description);
    }


    private TestPlan prepareSpec(final Class<?> specClass, final Description rootDescription) {
        currentTestPlan = new TestPlan(rootDescription);
        final TestPlan testPlan;
        try {
            final Constructor<?> constructor = specClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        } catch (final InvocationTargetException e) {
            throw new SpecInitializationError(e.getTargetException());
        } catch (final Exception e) {
            throw new SpecInitializationError(e);
        } finally {
            testPlan = currentTestPlan;
            currentTestPlan = null;
        }
        return testPlan;
    }


    @Override
    public Description getDescription() {
        return description;
    }

    @Override
    public void run(final RunNotifier notifier) {
        plan.execute(notifier);
    }

}

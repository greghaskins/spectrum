package com.greghaskins.spectrum.runner;

import static com.greghaskins.spectrum.runner.AnnotationLogic.ifMethodHasAnnotation;

import java.lang.reflect.Method;

import org.junit.runner.Description;

import com.greghaskins.spectrum.Spectrum.BeforeEach;
import com.greghaskins.spectrum.Spectrum.It;
import com.greghaskins.spectrum.runner.AnnotationLogic.Then;

class TestPlanner {

    static <T> TestPlan<T> makeTestPlan(final Class<T> type, final Description parentDescription) {
        final TestPlan<T> testPlan = new TestPlan<T>(type, parentDescription);
        final String contextName = parentDescription.getDisplayName();

        for (final Method method : type.getDeclaredMethods()) {
            ifMethodHasAnnotation(method, It.class, thenAddTestToPlan(method, testPlan, contextName));
            ifMethodHasAnnotation(method, BeforeEach.class, thenAddSetupToPlan(method, testPlan));
        }
        return testPlan;
    }

    private static <T> Then<It> thenAddTestToPlan(final Method method, final TestPlan<T> testPlan, final String contextName) {
        return new Then<It>() {

            @Override
            public void then(final It foundAnnotation) {
                final String testName = foundAnnotation.value();
                testPlan.addTest(new Test<T>(testName, contextName, new InstanceMethod<T>(method)));

            }
        };
    }

    private static <T> Then<BeforeEach> thenAddSetupToPlan(final Method method, final TestPlan<T> testPlan){
        return new Then<BeforeEach>() {

            @Override
            public void then(final BeforeEach foundAnnotation) {
                testPlan.addSetup(new InstanceMethod<T>(method));
            }
        };
    }
}

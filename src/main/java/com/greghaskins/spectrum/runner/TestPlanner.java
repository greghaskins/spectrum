package com.greghaskins.spectrum.runner;

import static com.greghaskins.spectrum.runner.AnnotationLogic.ifElement;

import java.lang.reflect.Method;

import org.junit.runner.Description;

import com.greghaskins.spectrum.Spectrum.BeforeEach;
import com.greghaskins.spectrum.Spectrum.It;
import com.greghaskins.spectrum.runner.AnnotationLogic.ThenClause;

class TestPlanner {

    static <T> TestPlan<T> makeTestPlan(final Class<T> type, final Description parentDescription) {
        final TestPlan<T> testPlan = new TestPlan<T>(parentDescription);
        final String contextName = parentDescription.getDisplayName();

        for (final Method method : type.getDeclaredMethods()) {
            ifElement(method).hasAnnotation(It.class).then(addTestToPlan(method, testPlan, contextName));
            ifElement(method).hasAnnotation(BeforeEach.class).then(addSetupToPlan(method, testPlan));
        }
        return testPlan;
    }

    private static <T> ThenClause<It> addTestToPlan(final Method method, final TestPlan<T> testPlan, final String contextName) {
        return new ThenClause<It>() {

            @Override
            public void then(final It foundAnnotation) {
                final String testName = foundAnnotation.value();
                testPlan.addTest(new Test<T>(testName, contextName, new InstanceMethod<T>(method)));

            }
        };
    }

    private static <T> ThenClause<BeforeEach> addSetupToPlan(final Method method, final TestPlan<T> testPlan){
        return new ThenClause<BeforeEach>() {

            @Override
            public void then(final BeforeEach foundAnnotation) {
                testPlan.addSetup(new InstanceMethod<T>(method));
            }
        };
    }
}

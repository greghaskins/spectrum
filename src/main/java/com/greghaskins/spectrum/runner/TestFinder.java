package com.greghaskins.spectrum.runner;

import java.lang.reflect.Method;

import org.junit.runner.Description;

import com.greghaskins.spectrum.Spectrum.BeforeEach;
import com.greghaskins.spectrum.Spectrum.It;

class TestFinder {

    static <T> TestPlan<T> findTests(final Class<T> type, final Description parentDescription) {
        final TestPlan<T> testPlan = new TestPlan<T>(type, parentDescription);
        for (final Method method : type.getDeclaredMethods()) {

            final It itAnnotation = method.getAnnotation(It.class);
            if (itAnnotation != null) {
                testPlan.addTest(new Test<T>(itAnnotation.value(), parentDescription.getDisplayName(), new InstanceMethod<T>(method)));
            }

            final BeforeEach beforeEachAnnotation = method.getAnnotation(BeforeEach.class);
            if (beforeEachAnnotation != null) {
                testPlan.addSetup(new InstanceMethod<T>(method));
            }
        }
        return testPlan;
    }
}

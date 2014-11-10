package com.greghaskins.spectrum.runner;

import java.lang.reflect.Method;

import org.junit.runner.Description;

import com.greghaskins.spectrum.Spectrum.It;

class TestFinder {

    static <T> TestList<T> findTests(final Class<T> type, final Description parentDescription) {
        final TestList<T> foundTests = new TestList<T>();
        for (final Method method : type.getDeclaredMethods()) {
            final It annotation = method.getAnnotation(It.class);
            if (annotation != null) {
                addTest(type, method, foundTests, parentDescription);
            }
        }
        return foundTests;
    }

    private static <T> void addTest(final Class<T> type, final Method method, final TestList<T> foundTests,
            final Description parentDescription) {
        final Test<T> test = new Test<T>(type, method);
        foundTests.add(test);
        parentDescription.addChild(test.getDescription());
    }
}

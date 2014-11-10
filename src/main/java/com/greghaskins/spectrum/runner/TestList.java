package com.greghaskins.spectrum.runner;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.notification.RunNotifier;

public class TestList<T> {

    private final List<Test<T>> tests = new ArrayList<Test<T>>();

    public void add(final Test<T> test) {
        tests.add(test);
    }

    public void runInContext(final T instance, final RunNotifier notifier) {
        for (final Test<T> test : tests) {
            test.run(instance, notifier);
        }
    }

}

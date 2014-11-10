package com.greghaskins.spectrum;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import com.greghaskins.spectrum.Spectrum.Describe;
import com.greghaskins.spectrum.Spectrum.It;

class Context<T> {

    private final List<Test<T>> tests;
    private final Description description;
    private final Class<T> contextClass;

    Context(final Class<T> contextClass) {
        this.contextClass = contextClass;
        description = makeDescription();
        tests = new ArrayList<Test<T>>();
        addChildTests();
    }

    private T makeInstance(final Class<T> type) throws UnableToInstantiateContextError {
        final T newInstance;
        try {
            final Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            newInstance = constructor.newInstance();
        } catch (final Exception e) {
            throw new UnableToInstantiateContextError(type, e);
        }
        return newInstance;
    }

    private Description makeDescription() throws MissingDescribeAnnotationError {
        return Description.createSuiteDescription(getContextName(), contextClass.getAnnotations());
    }

    private String getContextName() throws MissingDescribeAnnotationError {
        final Describe describe = contextClass.getAnnotation(Describe.class);
        if (describe == null) {
            throw new MissingDescribeAnnotationError(contextClass);
        }
        return describe.value();
    }

    private void addChildTests() {
        final Method[] methods = contextClass.getDeclaredMethods();
        for (final Method method : methods) {
            final It annotation = method.getAnnotation(It.class);
            if (annotation != null) {
                final Test<T> test = new Test<T>(contextClass, method);
                tests.add(test);
                description.addChild(test.getDescription());
            }
        }
    }

    public void run(final RunNotifier notifier) {
        final T instance = makeInstance(contextClass);
        for (final Test<T> test : tests) {
            test.run(instance, notifier);
        }
    }

    public Description getDescription() {
        return description;
    }

}
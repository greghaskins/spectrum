package com.greghaskins.spectrum.runner;

import java.lang.reflect.Method;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import com.greghaskins.spectrum.Spectrum.BeforeEach;

public class Context<T> {

    private final TestList<T> tests;
    private final Description description;
    private final Class<T> contextClass;
    private Method beforeEach;

    public static <T> Context<T> forClass(final Class<T> contextClass) {
        return new Context<T>(contextClass);
    }

    private Context(final Class<T> contextClass) {
        this.contextClass = contextClass;
        description = ContextDescriber.makeDescription(contextClass);
        tests = TestFinder.findTests(contextClass, description);

        for (final Method method : contextClass.getDeclaredMethods()) {
            final BeforeEach annotation = method.getAnnotation(BeforeEach.class);
            if (annotation != null) {
                beforeEach = method;
            }
        }

    }

    public void run(final RunNotifier notifier) {
        final T instance = ReflectiveConstructor.makeInstance(contextClass);
        if (beforeEach != null) {
            try {
                beforeEach.setAccessible(true);
                beforeEach.invoke(instance);
            } catch (final Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        tests.runInContext(instance, notifier);
    }

    public Description getDescription() {
        return description;
    }

}

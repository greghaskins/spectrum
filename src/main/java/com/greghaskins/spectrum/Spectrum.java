package com.greghaskins.spectrum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

public class Spectrum extends Runner {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public static @interface Describe {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    public static @interface It {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    public @interface BeforeEach {

    }

    private final Context<?> context;

    public <T> Spectrum(final Class<T> testClass) throws InitializationError {
        context = new Context<T>(testClass);
    }

    @Override
    public Description getDescription() {
        return context.getDescription();
    }

    @Override
    public void run(final RunNotifier notifier) {
        context.run(notifier);
    }

}

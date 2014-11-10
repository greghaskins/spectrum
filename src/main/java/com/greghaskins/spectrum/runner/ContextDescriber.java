package com.greghaskins.spectrum.runner;

import org.junit.runner.Description;

import com.greghaskins.spectrum.Spectrum.Describe;

class ContextDescriber {

    public static <T> Description makeDescription(final Class<T> contextType) throws MissingDescribeAnnotationError {
        return Description.createSuiteDescription(getContextName(contextType), contextType.getAnnotations());
    }

    private static <T> String getContextName(final Class<T> contextType) throws MissingDescribeAnnotationError {
        final Describe describe = contextType.getAnnotation(Describe.class);
        if (describe == null) {
            throw new MissingDescribeAnnotationError(contextType);
        }
        return describe.value();
    }

}

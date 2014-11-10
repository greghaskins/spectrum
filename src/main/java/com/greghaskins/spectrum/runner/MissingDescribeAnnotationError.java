package com.greghaskins.spectrum.runner;

public class MissingDescribeAnnotationError extends RuntimeException {

    private static final long serialVersionUID = 1L;

    MissingDescribeAnnotationError(final Class<?> contextClass) {
        super(contextClass.getSimpleName() + " does not have a @Describe annotation");
    }

}

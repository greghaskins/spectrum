package com.greghaskins.spectrum.errors;

import org.junit.runners.model.InitializationError;

public class MissingDescribeAnnotationError extends InitializationError {

    private static final long serialVersionUID = 1L;

    public MissingDescribeAnnotationError(final Class<?> contextClass) {
        super(contextClass.getSimpleName() + " does not have a @Describe annotation");
    }

}

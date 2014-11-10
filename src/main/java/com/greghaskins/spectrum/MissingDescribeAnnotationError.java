package com.greghaskins.spectrum;


public class MissingDescribeAnnotationError extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MissingDescribeAnnotationError(final Class<?> contextClass) {
        super(contextClass.getSimpleName() + " does not have a @Describe annotation");
    }

}

package com.greghaskins.spectrum;


public class UnableToInstantiateContextError extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnableToInstantiateContextError(final Class<?> contextClass, final Throwable cause) {
        super("Error creating instance of " + contextClass.getName() + ". Does it have a zero-argument constructor?", cause);
    }

}

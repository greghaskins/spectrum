package com.greghaskins.spectrum;

public class UnableToConstructSpecException extends RuntimeException {

    public UnableToConstructSpecException(final Class<?> klass, final Throwable cause) {
        super(klass.getName(), cause);
    }

    private static final long serialVersionUID = 1L;

}

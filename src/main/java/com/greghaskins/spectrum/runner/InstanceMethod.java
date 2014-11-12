package com.greghaskins.spectrum.runner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class InstanceMethod<T> {

    private final Method method;

    public InstanceMethod(final Method method) {
        this.method = method;
    }

    public void invokeWithInstance(final T instance) throws Throwable {
        method.setAccessible(true);
        try {
            method.invoke(instance);
        } catch (final InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

}

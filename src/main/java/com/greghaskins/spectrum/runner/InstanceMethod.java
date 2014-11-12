package com.greghaskins.spectrum.runner;

import java.lang.reflect.Method;

class InstanceMethod<T> {

    private final Method method;

    public InstanceMethod(final Method method) {
        this.method = method;
    }

    public void invokeWithInstance(final T instance) throws Throwable {
        method.setAccessible(true);
        method.invoke(instance);
    }

}

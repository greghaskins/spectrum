package com.greghaskins.spectrum.runner;

import java.lang.reflect.Constructor;

public class NestedConstructor<T, OuterType> implements InstanceFactory<T, OuterType> {

    private final Class<T> innerClass;
    private final Class<OuterType> outerClass;

    public NestedConstructor(final Class<T> innerClass, final Class<OuterType> outerClass) {
        this.innerClass = innerClass;
        this.outerClass = outerClass;
    }

    @Override
    public T makeInstance(final OuterType outerInstance) {
        try {
            final Constructor<T> constructor = innerClass.getDeclaredConstructor(outerClass);
            constructor.setAccessible(true);
            return constructor.newInstance(outerInstance);
        } catch (final Exception e) {
            throw new UnableToInstantiateContextError(innerClass, e);
        }
    }

}

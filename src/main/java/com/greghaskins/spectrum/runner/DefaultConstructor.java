package com.greghaskins.spectrum.runner;

import java.lang.reflect.Constructor;

class DefaultConstructor<T> implements InstanceFactory<T, Void> {

    private final Class<T> type;

    public DefaultConstructor(final Class<T> type) {
        this.type = type;
    }

    @Override
    public T makeInstance(final Void outerInstanceNotNeeded) {
        final T newInstance;
        try {
            final Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            newInstance = constructor.newInstance();
        } catch (final Exception e) {
            throw new UnableToInstantiateContextError(type, e);
        }
        return newInstance;
    }

}

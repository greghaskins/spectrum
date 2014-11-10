package com.greghaskins.spectrum.runner;

import java.lang.reflect.Constructor;

class ReflectiveConstructor {

    public static <T> T makeInstance(final Class<T> type) {
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

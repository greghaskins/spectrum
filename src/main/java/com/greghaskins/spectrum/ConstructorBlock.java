package com.greghaskins.spectrum;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.greghaskins.spectrum.Spectrum.Block;

class ConstructorBlock implements Block {

    private final Class<?> klass;

    public ConstructorBlock(final Class<?> klass) {
        this.klass = klass;
    }

    @Override
    public void run() throws Throwable {
        try {
            final Constructor<?> constructor = klass.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        } catch (final InvocationTargetException e) {
            throw e.getTargetException();
        } catch (final Exception e) {
            throw new UnableToConstructSpecException(klass, e);
        }
    }

}

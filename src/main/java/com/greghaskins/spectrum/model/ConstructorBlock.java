package com.greghaskins.spectrum.model;

import com.greghaskins.spectrum.Block;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public final class ConstructorBlock implements Block, Supplier<Object> {

  private final Class<?> klass;
  private Object testObject;

  public ConstructorBlock(final Class<?> klass) {
    this.klass = klass;
  }

  @Override
  public void run() throws Throwable {
    try {
      final Constructor<?> constructor = this.klass.getDeclaredConstructor();
      constructor.setAccessible(true);
      testObject = constructor.newInstance();
    } catch (final InvocationTargetException invocationTargetException) {
      throw invocationTargetException.getTargetException();
    } catch (final Exception error) {
      throw new UnableToConstructSpecException(this.klass, error);
    }
  }

  @Override
  public Object get() {
    return testObject;
  }

  private class UnableToConstructSpecException extends RuntimeException {

    private UnableToConstructSpecException(final Class<?> klass, final Throwable cause) {
      super(klass.getName(), cause);
    }

    private static final long serialVersionUID = 1L;

  }

}

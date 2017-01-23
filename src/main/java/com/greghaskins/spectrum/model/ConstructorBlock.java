package com.greghaskins.spectrum.model;

import com.greghaskins.spectrum.Block;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public final class ConstructorBlock<T> implements Block, Supplier<Object> {

  private final Class<T> klass;
  private T testObject;

  public ConstructorBlock(final Class<T> klass) {
    this.klass = klass;
  }

  @Override
  public void run() throws Throwable {
    try {
      final Constructor<T> constructor = this.klass.getDeclaredConstructor();
      constructor.setAccessible(true);
      testObject = constructor.newInstance();
    } catch (final InvocationTargetException invocationTargetException) {
      throw invocationTargetException.getTargetException();
    } catch (final Exception error) {
      throw new UnableToConstructSpecException(this.klass, error);
    }
  }

  @Override
  public T get() {
    return testObject;
  }

  private static class UnableToConstructSpecException extends RuntimeException {

    private UnableToConstructSpecException(final Class<?> klass, final Throwable cause) {
      super(klass.getName(), cause);
    }

    private static final long serialVersionUID = 1L;

  }

}

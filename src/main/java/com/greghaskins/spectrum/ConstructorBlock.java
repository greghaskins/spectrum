package com.greghaskins.spectrum;

import com.greghaskins.spectrum.Spectrum.Block;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class ConstructorBlock implements Block {

  private final Class<?> klass;

  public ConstructorBlock(final Class<?> klass) {
    this.klass = klass;
  }

  @Override
  public void run() throws Throwable {
    try {
      final Constructor<?> constructor = this.klass.getDeclaredConstructor();
      constructor.setAccessible(true);
      constructor.newInstance();
    } catch (final InvocationTargetException invocationTargetException) {
      throw invocationTargetException.getTargetException();
    } catch (final Exception error) {
      throw new UnableToConstructSpecException(this.klass, error);
    }
  }

}

package com.greghaskins.spectrum;

import java.util.function.Supplier;

/**
 * This is a convenience class to make working with Java closures easier. Variables from outer
 * scopes must be {@code final} to be referenced inside lambda functions. Wrapping objects in a
 * {@link #Variable} instance allows you to get/set values from anywhere as long as the Variable
 * itself is {@code final}.
 */
public final class Variable<T> implements Supplier<T> {

  private T value;

  /**
   * Create a Variable with a {@code null} initial value.
   */
  public Variable() {}

  /**
   * Create a Variable with the given initial value.
   *
   * @param value starting value
   */
  public Variable(final T value) {
    set(value);
  }

  /**
   * Get the current value of this Variable.
   *
   * @return current value
   */
  @Override
  public T get() {
    return this.value;
  }

  /**
   * Change the value of this Variable.
   *
   * @param value new value
   */
  public void set(final T value) {
    this.value = value;
  }
}

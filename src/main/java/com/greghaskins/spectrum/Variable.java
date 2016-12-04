package com.greghaskins.spectrum;

import java.util.function.Supplier;

/**
 * This is a convenience class to make working with Java closures easier. Variables from outer
 * scopes must be {@code final} to be referenced inside lambda functions. Wrapping objects in a
 * {@link #Variable} instance allows you to get/set values from anywhere as long as the Variable
 * itself is {@code final}.
 */
public class Variable<T> implements Supplier<T> {

  private ThreadLocal<T> value = new ThreadLocal<>();

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
   * Create a variable to have a supplied initial value.
   * @param initialValue supplier of that value
   */
  public Variable(Supplier<T> initialValue) {
    value = ThreadLocal.withInitial(initialValue);
  }

  /**
   * Get the current value of this Variable.
   *
   * @return current value
   */
  @Override
  public T get() {
    return this.value.get();
  }

  /**
   * Change the value of this Variable.
   *
   * @param value new value
   */
  public void set(final T value) {
    this.value.set(value);
  }
}

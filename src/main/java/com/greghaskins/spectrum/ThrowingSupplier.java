package com.greghaskins.spectrum;

import java.util.function.Supplier;

/**
 * Supplier of results similar to {@link Supplier}, but may optionally throw checked exceptions.
 * Using {@link ThrowingSupplier} is more convenient for lambda functions since it
 * requires less exception handling.
 *
 * @see Supplier
 *
 * @param <T> The type of result that will be supplied
 */
@FunctionalInterface
public interface ThrowingSupplier<T> extends Supplier<T> {

  /**
   * Get a result.
   *
   * @return a result
   * @throws Throwable any uncaught Error or Exception
   */
  T getOrThrow() throws Throwable;

  @Override
  default T get() {
    try {
      return getOrThrow();
    } catch (final RuntimeException | Error unchecked) {
      throw unchecked;
    } catch (final Throwable checked) {
      throw new RuntimeException(checked);
    }
  }
}

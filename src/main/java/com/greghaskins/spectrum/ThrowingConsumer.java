package com.greghaskins.spectrum;

import java.util.function.Consumer;

/**
 * An operation that accepts one input and returns no result, similar to {@link Consumer}, but may
 * optionally throw checked exceptions. Using {@link ThrowingConsumer} is more convenient for lambda
 * functions since it requires less exception handling.
 *
 * @param <T> the type of the input to the operation
 */
public interface ThrowingConsumer<T> {

  /**
   * Performs this operation on the given argument, or throws a throwable.
   *
   * @param object an input argument
   * @throws Throwable when something goes wrong
   */
  void accept(T object) throws Throwable;
}

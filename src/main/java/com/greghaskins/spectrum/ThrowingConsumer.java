package com.greghaskins.spectrum;

import java.util.function.Consumer;

/**
 * An operation that accepts one input and returns no result, similar to {@link Consumer}, but may
 * optionally throw checked exceptions. Using {@link ThrowingConsumer} is more convenient for lambda
 * functions since it requires less exception handling.
 *
 * @param <T> the type of the input to the operation
 */
public interface ThrowingConsumer<T> extends Consumer<T> {

  /**
   * Performs this operation on the given argument, or throws a throwable.
   *
   * @param object an input argument
   * @throws Throwable when something goes wrong
   */
  void acceptOrThrow(T object) throws Throwable;

  @Override
  default void accept(T object) {
    try {
      acceptOrThrow(object);
    } catch (RuntimeException | Error unchecked) {
      throw unchecked;
    } catch (Throwable exception) {
      throw new RuntimeException(exception);
    }

  }
}

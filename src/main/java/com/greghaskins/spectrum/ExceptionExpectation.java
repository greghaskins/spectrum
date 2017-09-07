package com.greghaskins.spectrum;

import java.util.function.Predicate;

/**
 * Allows expectations for exceptions to be specified by a spec.
 */
public interface ExceptionExpectation {
  /**
   * Require that the exception type match the given type.
   * @param typeOfException the thrown exception must be of this exact type.
   */
  void expect(final Class<?> typeOfException);

  /**
   * General purpose expectation. "and"ed to all other expectations called on the object.
   * @param description description of the expectation for error reporting
   * @param exceptionIsExpected defines a single check on the exception that must be valid
   */
  void expect(final String description, final Predicate<Throwable> exceptionIsExpected);

  /**
   * Expect the whole of the message of the exception is as provided.
   * @param message expected message
   */
  void expectMessage(final CharSequence message);

  /**
   * Expect the message of the exception to contain.
   * @param substring expected substring
   */
  void expectMessageContains(final CharSequence substring);

  /**
   * When a throwable is thrown by the test check that it is valid.
   * @param thrown exception thrown
   * @throws Throwable the given exception is rethrown when no exception is expected
   */
  void validateThrowable(final Throwable thrown) throws Throwable;

  /**
   * When the test finishes with nothing thrown, check that this was expected.
   */
  void verifyNoExceptionExpected();
}

package com.greghaskins.spectrum.internal.exception;

import com.greghaskins.spectrum.ExceptionExpectation;

import org.junit.Assert;

import java.util.function.Predicate;

/**
 * Object that can be configured to expect exceptions.
 */
public class ExceptionExpecter implements ExceptionExpectation {
  private Predicate<Throwable> toExpect;
  private StringBuilder expectationDescription = new StringBuilder();

  @Override
  public void expect(final Class<?> typeOfException) {
    expect("type: " + typeOfException.getCanonicalName(),
        throwable -> throwable.getClass().equals(typeOfException));
  }

  @Override
  public void expect(final String description, final Predicate<Throwable> exceptionIsExpected) {
    if (expectationDescription.length() == 0) {
      expectationDescription.append("Expected ");
    } else {
      expectationDescription.append(", ");
    }

    expectationDescription.append(description);

    // combine the expectations into a chain
    toExpect = toExpect == null ? exceptionIsExpected : toExpect.and(exceptionIsExpected);
  }

  @Override
  public void expectMessage(final CharSequence message) {
    expect("message: " + message, throwable -> throwable.getMessage().equals(message));
  }

  @Override
  public void expectMessageContains(final CharSequence substring) {
    expect("message contains: " + substring, throwable -> throwable.getMessage().contains(substring));
  }

  @Override
  public void validateThrowable(Throwable thrown) throws Throwable {
    if (toExpect == null) {
      throw thrown;
    }
    if (!toExpect.test(thrown)) {
      Assert.fail(expectationDescription.toString() + " but got " + thrown.toString());
    }
  }

  @Override
  public void verifyNoExceptionExpected() {
    if (toExpect != null) {
      Assert.fail(expectationDescription.toString() + " but ended with no exception");
    }
  }
}

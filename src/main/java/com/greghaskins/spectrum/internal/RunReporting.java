package com.greghaskins.spectrum.internal;

/**
 * Abstraction of reporting that's done on a test run. This
 * decouples Spectrum from JUnit and also allows Spectrum to
 * report its own way with an adapter for each test framework
 * providing the right updates according to the target framework's
 * needs/expectations.
 */
public interface RunReporting<T, F> {
  /**
   * Marks the test as ignored.
   * @param description description of test
   */
  void fireTestIgnored(final T description);

  /**
   * Markes the test as having started - call this before any test-specific results.
   * @param description description of test
   */
  void fireTestStarted(final T description);

  /**
   * Marks the test as finished - call this after any test-specific results, whether
   * passed or failed.
   * @param description description of test
   */
  void fireTestFinished(final T description);

  /**
   * Marks a test as having failed.
   * @param failure failure information
   */
  void fireTestFailure(final F failure);

  /**
   * Marks a test as having an assumption failure.
   * @param failure failure information
   */
  void fireTestAssumptionFailed(final F failure);
}

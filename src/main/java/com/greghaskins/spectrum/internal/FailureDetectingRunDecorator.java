package com.greghaskins.spectrum.internal;

/**
 * A listener to detect test failure.
 */
public class FailureDetectingRunDecorator<T, F> implements RunReporting<T, F> {
  private boolean hasFailedYet = false;

  private RunReporting<T, F> decoratee;

  public FailureDetectingRunDecorator(RunReporting<T, F> decoratee) {
    this.decoratee = decoratee;
  }

  /**
   * Has the run failed since we've been listening.
   * @return whether any previous failures have been reported
   */
  public boolean hasFailedYet() {
    return hasFailedYet;
  }

  @Override
  public void fireTestFailure(F failure) {
    decoratee.fireTestFailure(failure);
    hasFailedYet = true;
  }

  @Override
  public void fireTestIgnored(T description) {
    decoratee.fireTestIgnored(description);
  }

  @Override
  public void fireTestStarted(T description) {
    decoratee.fireTestStarted(description);
  }

  @Override
  public void fireTestFinished(T description) {
    decoratee.fireTestFinished(description);
  }

  @Override
  public void fireTestAssumptionFailed(F failure) {
    decoratee.fireTestAssumptionFailed(failure);
    hasFailedYet = true;
  }
}

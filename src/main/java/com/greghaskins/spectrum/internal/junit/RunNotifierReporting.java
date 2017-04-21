package com.greghaskins.spectrum.internal.junit;

import com.greghaskins.spectrum.internal.RunReporting;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Wraps the JUnit RunNotifier with the Spectrum run reporting interface.
 */
public class RunNotifierReporting implements RunReporting<Description, Failure> {
  private RunNotifier notifier;
  private Set<FailureWrapper> reportedForFailure = new HashSet<>();

  static class FailureWrapper {
    private Failure failure;

    public FailureWrapper(Failure failure) {
      this.failure = failure;
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (other == null || getClass() != other.getClass()) {
        return false;
      }
      FailureWrapper that = (FailureWrapper) other;

      return Objects.equals(failure.getDescription(), that.failure.getDescription())
          && Objects.equals(failure.getException(), that.failure.getException());
    }

    @Override
    public int hashCode() {
      return Objects.hash(failure.getDescription(), failure.getException());
    }
  }

  public RunNotifierReporting(RunNotifier notifier) {
    this.notifier = notifier;
  }

  @Override
  public void fireTestIgnored(Description description) {
    notifier.fireTestIgnored(description);
  }

  @Override
  public void fireTestStarted(Description description) {
    notifier.fireTestStarted(description);
  }

  @Override
  public void fireTestFinished(Description description) {
    notifier.fireTestFinished(description);
  }

  @Override
  public void fireTestAssumptionFailed(Failure failure) {
    notifier.fireTestAssumptionFailed(failure);
  }

  @Override
  public void fireTestFailure(Failure failure) {
    FailureWrapper wrapper = new FailureWrapper(failure);
    if (!reportedForFailure.contains(wrapper)) {
      notifier.fireTestFailure(failure);
      reportedForFailure.add(wrapper);
    }
  }
}

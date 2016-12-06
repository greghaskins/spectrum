package com.greghaskins.spectrum;

import com.greghaskins.spectrum.internal.Child;
import com.greghaskins.spectrum.internal.FailureDetectingRunListener;
import com.greghaskins.spectrum.internal.Parent;
import com.greghaskins.spectrum.model.TaggingFilterCriteria;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

/**
 * Subclass of {@link Suite} that represent the fact that some tests are composed
 * of interrelated steps which add up to a single test.
 */
final class CompositeTest extends Suite {
  /**
   * Constructs a Composite Test, which is a suite run as an atomic test.
   * @param description of the test
   * @param parent parent suite
   * @param tagging tagging state to inherit from parent
   */
  CompositeTest(final Description description, final Parent parent,
      final TaggingFilterCriteria tagging) {
    super(description, parent, CompositeTest::abortOnFailureChildRunner, tagging);
  }

  @Override
  public boolean isAtomic() {
    return true;
  }

  private static void abortOnFailureChildRunner(final Suite suite, final RunNotifier runNotifier) {
    FailureDetectingRunListener listener = new FailureDetectingRunListener();
    runNotifier.addListener(listener);
    try {
      for (Child child : suite.children) {
        if (listener.hasFailedYet()) {
          child.ignore();
        }
        suite.runChild(child, runNotifier);
      }
    } finally {
      runNotifier.removeListener(listener);
    }
  }
}

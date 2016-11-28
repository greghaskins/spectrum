package com.greghaskins.spectrum;

import org.junit.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

@FunctionalInterface
interface NotifyingBlock {

  void run(final Description description, final RunNotifier notifier);

  static NotifyingBlock wrap(final Block block) {
    return (description, notifier) -> {
      try {
        block.run();
      } catch (final AssumptionViolatedException assumptionViolation) {
        notifier.fireTestAssumptionFailed(new Failure(description, assumptionViolation));
      } catch (final Throwable exception) {
        notifier.fireTestFailure(new Failure(description, exception));
      }
    };

  }

}

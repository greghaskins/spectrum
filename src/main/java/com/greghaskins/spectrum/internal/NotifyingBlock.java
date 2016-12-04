package com.greghaskins.spectrum.internal;

import com.greghaskins.spectrum.Block;

import org.junit.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * An interface which can be run with a description and notifier.
 */
@FunctionalInterface
public interface NotifyingBlock {
  void run(final Description description, final RunNotifier notifier);

  /**
   * Execute the block notifying the notifier of any errors.
   * @param description description to cite to the notifier
   * @param notifier notifier to be informed of assumption or test failure
   * @param block block to un
   */
  static void run(final Description description, final RunNotifier notifier, final Block block) {
    wrap(block).run(description, notifier);
  }

  /**
   * Convert a {@link Block} into a {@link NotifyingBlock}
   * @param block to convert
   * @return {@link NotifyingBlock} which can be run with {@link Description}
   *         and {@link RunNotifier} so that exceptions are reported
   *         rather than rethrown.
   */
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

package com.greghaskins.spectrum.internal.blocks;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.internal.RunReporting;

import org.junit.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * An interface which can be run with a description and notifier.
 */
@FunctionalInterface
public interface NotifyingBlock {
  void run(final Description description, final RunReporting<Description, Failure> reporting);

  /**
   * Execute the block notifying the notifier of any errors.
   * @param description description to cite to the notifier
   * @param reporting object to be informed of assumption or test failure
   * @param block block to un
   */
  static void run(final Description description, final RunReporting<Description, Failure> reporting,
      final Block block) {
    wrap(block).run(description, reporting);
  }

  /**
   * Convert a {@link Block} into a {@link NotifyingBlock}
   * @param block to convert
   * @return {@link NotifyingBlock} which can be run with {@link Description}
   *         and {@link RunNotifier} so that exceptions are reported
   *         rather than rethrown.
   */
  static NotifyingBlock wrap(final Block block) {
    return (description, reporting) -> {
      try {
        executeAndReport(description, reporting, block);
      } catch (final Throwable exception) {
        // the exception has already been reported and should not be rethrown
      }
    };
  }

  /**
   * Add the reporting capability to a block. The block runs as normal, but
   * any exceptions are ALSO reported on the way to outer catch blocks.
   * @param description which test called the block
   * @param reporting object to inform of failure
   * @param block the block to execute
   * @return a block which has notification built in
   */
  static Block wrapWithReporting(final Description description,
      final RunReporting<Description, Failure> reporting,
      final Block block) {
    return () -> executeAndReport(description, reporting, block);
  }

  /**
   * Add notification of exceptions to a throwing block.
   * @param description which test called the block
   * @param reporting object to inform of failure
   * @param block the block to execute
   * @throws Throwable the error which was reported to the {@link RunNotifier}
   */
  static void executeAndReport(final Description description,
      final RunReporting<Description, Failure> reporting,
      final Block block) throws Throwable {
    try {
      block.run();
    } catch (final AssumptionViolatedException assumptionViolation) {
      reporting.fireTestAssumptionFailed(new Failure(description, assumptionViolation));
      throw assumptionViolation;
    } catch (final Throwable throwable) {
      reporting.fireTestFailure(new Failure(description, throwable));
      throw throwable;
    }
  }
}

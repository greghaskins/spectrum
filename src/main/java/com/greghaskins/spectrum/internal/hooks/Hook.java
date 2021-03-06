package com.greghaskins.spectrum.internal.hooks;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.ThrowingConsumer;
import com.greghaskins.spectrum.internal.RunReporting;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

/**
 * A hook allows you to inject functionality before and/or after a {@link Block}.
 * Just implement the {@link #accept(Description, RunReporting, Block)} method and
 * call {@link Block#run()} within your implementation.
 * If your hook is going to provide an object to the running test, then implement
 * {@link SupplyingHook} or subclass {@link AbstractSupplyingHook}.
 */
@FunctionalInterface
public interface Hook {
  /**
   * Accept the block and execute it, hooking in any behaviour around it.
   * @param description description of where we are in the test
   * @param reporting the object to notify for failures
   * @param block the block to execute
   * @throws Throwable on error
   */
  void accept(final Description description, final RunReporting<Description, Failure> reporting,
      final Block block) throws Throwable;

  /**
   * Override to return true if the inner block cannot report its own errors for some reason.
   * @return true to suppress wrapping inner block in self-reporting
   */
  default boolean requiresUnreportedInnerBlock() {
    return false;
  }

  /**
   * Create a hook from a {@link ThrowingConsumer}.
   * @param consumer to turn into a hook
   * @return the hook
   */
  static Hook from(ThrowingConsumer<Block> consumer) {
    return (description, notifier, block) -> consumer.accept(block);
  }
}

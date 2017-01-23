package com.greghaskins.spectrum;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

/**
 * A hook allows you to inject functionality before and/or after a {@link Block}.
 * Just implement the {@link #accept(Description, RunNotifier, Block)} method and
 * call {@link Block#run()} within your implementation.
 * If your hook is going to provide an object to the running test, then implement
 * {@link SupplyingHook} or subclass {@link AbstractSupplyingHook}.
 */
public interface Hook {
  /**
   * Accept the block and execute it, hooking in any behaviour around it.
   * @param description description of where we are in the test
   * @param notifier the notifier for failures
   * @param block the block to execute
   * @throws Throwable on error
   */
  void accept(final Description description, final RunNotifier notifier,
      final Block block) throws Throwable;

  /**
   * Create a hook from a {@link ThrowingConsumer}.
   * @param consumer to turn into a hook
   * @return the hook
   */
  static Hook from(ThrowingConsumer<Block> consumer) {
    return (description, notifier, block) -> consumer.accept(block);
  }
}

package com.greghaskins.spectrum.internal.hooks;

import static com.greghaskins.spectrum.Spectrum.assertSpectrumInTestMode;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.Variable;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

/**
 * A base class for {@link SupplyingHook hooks that supply a value}.
 *
 * <p>Override {@link #before} or {@link #after}. Return the singleton value from the before method.
 * You can use this to write any plugin which needs to make a value visible to the specs.
 * This is not the only way to achieve that - you can also build from {@link SupplyingHook}
 * but this captures the template for a complex hook.
 */
public abstract class AbstractSupplyingHook<T> extends Variable<T> implements SupplyingHook<T> {
  /**
   * Override this to supply behaviour for before the block is run.
   * @return the value that the singleton will store to supply
   */
  protected abstract T before();

  /**
   * Override this to supply behaviour for after the block is run.
   */
  protected void after() {}

  /**
   * Template method for a hook which supplies.
   * @param description description - unused here
   * @param runNotifier runNotifier - unused here
   * @param block the inner block that will be run
   * @throws Throwable on error
   */
  @Override
  public void accept(final Description description, final RunNotifier runNotifier,
      final Block block) throws Throwable {
    try {
      set(before());
      block.run();
    } finally {
      try {
        after();
      } finally {
        clear();
      }
    }
  }

  @Override
  public T get() {
    assertSpectrumInTestMode();

    return super.get();
  }

  private void clear() {
    set(null);
  }
}

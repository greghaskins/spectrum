package com.greghaskins.spectrum;

import static com.greghaskins.spectrum.Spectrum.assertSpectrumInTestMode;

import com.greghaskins.spectrum.model.Singleton;

/**
 * A base class for supplying hooks to use. Override before or after. Return the singleton
 * value from the before method.
 * You can use this to write any plugin which needs to make a value visible to the specs.
 * This is not the only way to achieve that - you can also build from {@link SupplyingHook}
 * but this captures the template for a complex hook.
 */
public class AbstractSupplyingHook<T> extends Singleton<T> implements SupplyingHook<T> {
  /**
   * Override this to supply behaviour for before the block is run.
   * @return the value that the singleton will store to supply
   */
  protected T before() {
    return null;
  }

  /**
   * Override this to supply behaviour for after the block is run.
   */
  protected void after() {}

  /**
   * Template method for a hook which supplies.
   * @param block the inner block that will be run
   * @throws Throwable on error
   */
  @Override
  public void acceptOrThrow(Block block) throws Throwable {
    set(before());
    block.run();
    after();
    clear();
  }

  @Override
  public T get() {
    assertSpectrumInTestMode();

    return super.get();
  }
}

package com.greghaskins.spectrum.internal;

import com.greghaskins.spectrum.Block;

/**
 * A hook that will run before something.
 * @see AfterHook
 */
public interface BeforeHook {
  /**
   * Insert the block before the inner.
   * @param block the inner block
   * @return new {@link Hook} which runs the provided block then the inner
   */
  static Hook before(final Block block) {
    return (description, notifier, inner) -> {
      block.run();
      inner.run();
    };
  }
}

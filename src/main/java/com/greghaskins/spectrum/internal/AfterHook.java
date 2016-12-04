package com.greghaskins.spectrum.internal;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.Hook;

/**
 * A hook that will run after something.
 * @see BeforeHook
 */
public interface AfterHook {
  /**
   * Insert the block after the inner.
   * @param block the inner block
   * @return new {@link Hook} which runs the inner then the provided block
   */
  static Hook after(final Block block) {
    return inner -> {
      inner.run();
      block.run();
    };
  }
}

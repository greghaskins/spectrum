package com.greghaskins.spectrum.internal;

import com.greghaskins.spectrum.Block;

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
    return (description, notifier, inner) -> {
      try {
        inner.run();
      } finally {
        block.run();
      }
    };
  }
}

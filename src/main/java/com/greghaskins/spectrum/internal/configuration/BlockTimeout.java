package com.greghaskins.spectrum.internal.configuration;

import static com.greghaskins.spectrum.internal.junit.TimeoutWrapper.timeoutHook;

import com.greghaskins.spectrum.internal.Child;
import com.greghaskins.spectrum.internal.LeafChild;
import com.greghaskins.spectrum.internal.hooks.HookContext;

import java.time.Duration;

/**
 * Applies timeout metadata to the block. A timeout will cause a leaf child to
 * run within a sentinel which fails the test if it takes too long.
 */
public class BlockTimeout implements BlockConfigurable<BlockTimeout> {
  private Duration timeout;

  /**
   * Create a timeout.
   * @param timeout duration of the timeout
   */
  public BlockTimeout(Duration timeout) {
    this.timeout = timeout;
  }

  @Override
  public boolean inheritedByChild() {
    return true;
  }

  @Override
  public void applyTo(Child child, TaggingFilterCriteria state) {
    if (child instanceof LeafChild) {
      ((LeafChild) child).addLeafHook(timeoutHook(timeout), HookContext.Precedence.ROOT);
    }
  }

  @Override
  public BlockConfigurable<BlockTimeout> merge(BlockConfigurable<?> other) {
    // my timeout supersedes any inherited timeout

    return this;
  }
}

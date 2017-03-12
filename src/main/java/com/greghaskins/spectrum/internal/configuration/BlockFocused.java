package com.greghaskins.spectrum.internal.configuration;

import com.greghaskins.spectrum.internal.Child;

/**
 * Created by friezea on 02/03/2017.
 */
public class BlockFocused implements BlockConfigurable<BlockFocused> {
  @Override
  public boolean inheritedByChild() {
    // focus is not inherited

    return false;
  }

  @Override
  public void applyTo(Child child, TaggingFilterCriteria state) {
    child.focus();
  }

  @Override
  public BlockConfigurable<BlockFocused> merge(BlockConfigurable<?> other) {
    // any focusing means future focusing
    // so this will always add up to focused, regardless of what other
    // may contain

    return new BlockFocused();
  }
}

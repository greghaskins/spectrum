package com.greghaskins.spectrum.internal;

/**
 * A configurable that does ignoring.
 */
public class BlockIgnore implements BlockConfigurable<BlockIgnore> {
  private String reason;

  public BlockIgnore() {}

  public BlockIgnore(String reason) {
    this.reason = reason;
  }

  @Override
  public boolean inheritedByChild() {
    return true;
  }

  @Override
  public void applyTo(Child child, TaggingFilterCriteria state) {
    child.ignore();
  }

  @Override
  public BlockConfigurable<BlockIgnore> merge(BlockConfigurable<?> other) {
    // any ignoring means future ignoring
    // so this will always add up to ignore, regardless of what other
    // may contain

    return new BlockIgnore();
  }
}

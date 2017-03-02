package com.greghaskins.spectrum.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The tags of a given block.
 */
public class BlockTagging implements BlockConfigurable<BlockTagging> {
  private Set<String> hasTags = new HashSet<>();

  public BlockTagging(String... tags) {
    Arrays.stream(tags).forEach(hasTags::add);
  }

  private BlockTagging(Set<String> tags) {
    hasTags.addAll(tags);
  }

  @Override
  public boolean inheritedByChild() {
    return true;
  }

  @Override
  public void applyTo(Child child, TaggingFilterCriteria state) {
    if (!state.isAllowedToRun(hasTags)) {
      child.ignore();
    }
  }

  @Override
  public BlockConfigurable<BlockTagging> merge(BlockConfigurable<?> other) {
    BlockTagging merged = new BlockTagging(hasTags);
    if (other != null) {
      // the downcast is allowed because this is only called with an object
      // of the same type as the parent merge routine is working type
      // by type
      merged.hasTags.addAll(((BlockTagging) other).hasTags);
    }

    return merged;
  }
}

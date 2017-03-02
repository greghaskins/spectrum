package com.greghaskins.spectrum.internal;

/**
 * Defines a configurable thing on a block.
 */
public interface BlockConfigurable<T> {
  /**
   * Return if this sort of configurable inherited by a child from a suite.
   * @return true when the configurable is inheritable.
   */
  boolean inheritedByChild();

  /**
   * Modify the child according to this configurable.
   * @param child to modify.
   * @param state any known tagging filter criteria (used by tagging configurable).
   */
  void applyTo(final Child child, final TaggingFilterCriteria state);

  /**
   * Provide a merged configurable, based on the combination of this configurable
   * and the input.
   * @param other the configurable to merge with this. Must be of same type. Can be null.
   * @return a new BlockConfigurable of the right type with the contents of both.
   */
  BlockConfigurable<T> merge(BlockConfigurable<?> other);
}

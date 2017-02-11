package com.greghaskins.spectrum.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Configurations that apply to a {@link ConfiguredBlock}.
 */
public class BlockConfiguration {


  private boolean isIgnored = false;
  private boolean isFocused = false;
  private final Set<String> hasTags = new HashSet<>();

  /**
   * Combine provided precondition objects together.
   * 
   * @param conditions to combine
   * @return a combination of all preconditions as a new object
   */
  static BlockConfiguration merge(BlockConfiguration... conditions) {
    BlockConfiguration merged = new BlockConfiguration();
    Arrays.stream(conditions).forEach((condition) -> {
      merged.hasTags.addAll(condition.hasTags);
      merged.isIgnored |= condition.isIgnored;
      merged.isFocused |= condition.isFocused;
    });

    return merged;
  }

  private BlockConfiguration() {}

  /**
   * Children should inherit tags and ignore status, but not focus.
   *
   * @return a new BlockConfiguration that would apply for a Child
   */
  BlockConfiguration forChild() {
    BlockConfiguration conditions = new BlockConfiguration();
    conditions.hasTags.addAll(this.hasTags);
    conditions.isIgnored = this.isIgnored;

    return conditions;
  }

  /**
   * Fluent setter of the ignored status.
   *
   * @return this for fluent use
   */
  public BlockConfiguration ignore() {
    this.isIgnored = true;

    return this;
  }

  /**
   * Fluent setter of the focused status.
   *
   * @return this for fluent use
   */
  public BlockConfiguration focus() {
    this.isFocused = true;

    return this;
  }

  /**
   * Add tags to the block - this will control execution in selective running. The tags may lead to
   * the block being ignored.
   *
   * @param tags the tags of the block
   * @return this for fluent use
   */
  public BlockConfiguration tags(String... tags) {
    Arrays.stream(tags).forEach(this.hasTags::add);

    return this;
  }

  /**
   * Visitor pattern - when necessary, the child gets the preconditions to apply to it.
   *
   * @param child to be pre-processed according to the preconditions.
   * @param state the tagging state within which the child is operating
   */
  void applyTo(final Child child, final TaggingFilterCriteria state) {
    // the order of precedence = tags, focus, ignored
    // the assumption being that tags are a general purpose override
    // and focus is only ever added as an override
    if (!state.isAllowedToRun(this.hasTags)) {
      child.ignore();
    } else if (this.isFocused) {
      child.focus();
    } else if (this.isIgnored) {
      child.ignore();
    }
  }

  public static BlockConfiguration defaultConfiguration() {
    return new BlockConfiguration();
  }
}

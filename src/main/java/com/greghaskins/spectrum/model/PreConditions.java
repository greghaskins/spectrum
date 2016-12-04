package com.greghaskins.spectrum.model;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.internal.Child;
import com.greghaskins.spectrum.internal.ConfiguredBlock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Pre conditions and configurations that apply to a {@link ConfiguredBlock}.
 */
public class PreConditions {
  /**
   * Contains factory methods for creating {@link PreConditions} objects so you can include them in
   * {@link ConfiguredBlock#with(PreConditions, Block)}. The {@link PreConditions} object has
   * fluent setters so you can add more properties. This is an interface since it has nothing but
   * static methods.
   */
  public interface Factory {
    /**
     * Ignore the suite or spec.
     *
     * @return a precondition that will ignore the block within a
     *         {@link ConfiguredBlock#with(PreConditions, Block)}
     */
    static PreConditions ignore() {
      return new PreConditions().ignore();
    }

    /**
     * Ignore the suite or spec.
     *
     * @param why reason for ignoring
     * @return a precondition that will ignore the block within a
     *         {@link ConfiguredBlock#with(PreConditions, Block)}
     */
    static PreConditions ignore(final String why) {
      return new PreConditions().ignore(why);
    }

    /**
     * Tags the suite or spec that is being built using
     * {@link ConfiguredBlock#with(PreConditions, Block)}. Dependent on the current selection of
     * tags, this may lead to the item being ignored during this execution.
     *
     * @param tags tags that relate to the suite or spec
     * @return a precondition that has these tags set
     *         {@link ConfiguredBlock#with(PreConditions, Block)}
     */
    static PreConditions tags(final String... tags) {
      return new PreConditions().tags(tags);
    }

    /**
     * Tags the suite or spec to be focused.
     *
     * @return a precondition that will focus the suite or spec around the
     *         {@link ConfiguredBlock#with(PreConditions, Block)}
     */
    static PreConditions focus() {
      return new PreConditions().focus();
    }

    static PreConditions defaultPreConditions() {
      return new PreConditions();
    }
  }

  private boolean isIgnored = false;
  private boolean isFocused = false;
  private final Set<String> hasTags = new HashSet<>();

  /**
   * Combine provided precondition objects together.
   * @param conditions to combine
   * @return a combination of all preconditions as a new object
   */
  public static PreConditions merge(PreConditions... conditions) {
    PreConditions merged = new PreConditions();
    Stream.of(conditions).forEach((condition) -> {
      merged.hasTags.addAll(condition.hasTags);
      merged.isIgnored |= condition.isIgnored;
      merged.isFocused |= condition.isFocused;
    });

    return merged;
  }

  /**
   * Hidden default constructor. Build using {@link Factory}
   */
  private PreConditions() {

  }

  /**
   * Children should inherit tags and ignore status, but not focus.
   *
   * @return a new PreConditions that would apply for a Child
   */
  public PreConditions forChild() {
    PreConditions conditions = new PreConditions();
    conditions.hasTags.addAll(this.hasTags);
    conditions.isIgnored = this.isIgnored;

    return conditions;
  }

  /**
   * Fluent setter of the ignored status.
   *
   * @return this for fluent use
   */
  public PreConditions ignore() {
    this.isIgnored = true;

    return this;
  }

  /**
   * Fluent setter of the ignored status.
   *
   * @param why the reason for ignoring
   * @return this for fluent use
   */
  public PreConditions ignore(final String why) {
    return ignore();
  }

  /**
   * Fluent setter of the focused status.
   *
   * @return this for fluent use
   */
  public PreConditions focus() {
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
  public PreConditions tags(String... tags) {
    Arrays.stream(tags).forEach(this.hasTags::add);

    return this;
  }

  /**
   * Visitor pattern - when necessary, the child gets the preconditions to apply to it.
   *
   * @param child to be pre-processed according to the preconditions.
   * @param state the tagging state within which the child is operating
   */
  public void applyTo(final Child child, final TaggingFilterCriteria state) {
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
}

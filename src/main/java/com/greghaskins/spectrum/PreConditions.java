package com.greghaskins.spectrum;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Pre conditions that apply to a {@link PreConditionBlock}.
 */
public class PreConditions {
  /**
   * Contains factory methods for creating {@link PreConditions} objects
   * so you can include them in {@link PreConditionBlock#with(PreConditions, Block)}.
   * The {@link PreConditions} object has fluent setters so you can add more properties.
   * This is an interface since it has nothing but static methods.
   */
  public interface Factory {
    /**
     * Ignore the suite or spec.
     * @return a precondition that will ignore the block within a
     *         {@link PreConditionBlock#with(PreConditions, Block)}
     */
    static PreConditions ignore() {
      return new PreConditions().ignore();
    }

    /**
     * Ignore the suite or spec.
     * @param why reason for ignoring
     * @return a precondition that will ignore the block within a
     *         {@link PreConditionBlock#with(PreConditions, Block)}
     */
    static PreConditions ignore(final String why) {
      return new PreConditions().ignore(why);
    }

    /**
     * Tags the suite or spec that is being built using
     * {@link PreConditionBlock#with(PreConditions, Block)}.
     * Dependent on the current selection of tags, this may lead to the item being ignored during
     * this execution.
     * @param tags tags that relate to the suite or spec
     * @return a precondition that has these tags set
     *         {@link PreConditionBlock#with(PreConditions, Block)}
     */
    static PreConditions tags(final String... tags) {
      return new PreConditions().tags(tags);
    }

    /**
     * Tags the suite or spec to be focused.
     * @return a precondition that will focus the suite or spec around the
     *         {@link PreConditionBlock#with(PreConditions, Block)}
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
  private Set<String> hasTags = new HashSet<>();

  /**
   * Hidden default constructor. Build using {@link Factory}
   */
  private PreConditions() {

  }

  /**
   * Fluent setter of the ignored status.
   * @return this for fluent use
   */
  public PreConditions ignore() {
    isIgnored = true;

    return this;
  }

  /**
   * Fluent setter of the ignored status.
   * @param why the reason for ignoring
   * @return this for fluent use
   */
  public PreConditions ignore(final String why) {
    return ignore();
  }

  /**
   * Fluent setter of the focused status.
   * @return this for fluent use
   */
  public PreConditions focus() {
    isFocused = true;

    return this;
  }

  /**
   * Add tags to the block - this will control execution in selective running.
   * The tags may lead to the block being ignored.
   * @param tags the tags of the block
   * @return this for fluent use
   */
  public PreConditions tags(String... tags) {
    Arrays.stream(tags).forEach(hasTags::add);

    return this;
  }

  /**
   * Visitor pattern - when necessary, the child gets the preconditions to apply to it.
   * @param child to be pre-processed according to the preconditions.
   */
  void applyTo(Child child, TaggingState state) {
    // the order of precedence = tags, focus, ignored
    // the assumption being that tags are a general purpose override
    // and focus is only ever added as an override
    if (!isAllowed(child, state)) {
      child.ignore();
    } else if (isFocused) {
      child.focus();
    } else if (isIgnored) {
      child.ignore();
    }
  }

  private boolean isAllowed(Child child, TaggingState state) {
    boolean isAllowed;
    if (child instanceof Parent) {
      isAllowed = state.isSuiteAllowedToRun(hasTags);
    } else {
      isAllowed = state.isSpecAllowedToRun(hasTags);
    }

    return isAllowed;
  }
}

package com.greghaskins.spectrum;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Pre conditions that apply to a {@link PreConditionBlock}.
 */
public class PreConditions {
  /**
   * Contains factory methods for creating PreConditions objects.
   * So you can include them in {@link PreConditionBlock#with(PreConditions, Block)}.
   */
  public interface PreConditionsFactory {
    static PreConditions ignore() {
      return new PreConditions().ignore();
    }

    static PreConditions ignore(final String why) {
      return new PreConditions().ignore(why);
    }

    static PreConditions tags(final String... tags) {
      return new PreConditions().tags(tags);
    }

    static PreConditions focus() {
      return new PreConditions().focus();
    }
  }

  private boolean isIgnored = false;
  private boolean isFocused = false;
  private Set<String> hasTags = new HashSet<>();

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
    boolean isAllowed;
    if (child instanceof Parent) {
      isAllowed = state.isSuiteAllowedToRun(hasTags);
    } else {
      isAllowed = state.isSpecAllowedToRun(hasTags);
    }
    if (!isAllowed) {
      child.ignore();
    } else if (isFocused) {
      child.focus();
    } else if (isIgnored) {
      child.ignore();
    }
  }
}

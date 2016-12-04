package com.greghaskins.spectrum.internal;

import static com.greghaskins.spectrum.model.PreConditions.Factory.defaultPreConditions;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.model.PreConditions;
import com.greghaskins.spectrum.model.TaggingState;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public interface Child {

  Description getDescription();

  void run(RunNotifier notifier);

  int testCount();

  void focus();

  void ignore();

  /**
   * Gets the object to be filtered appropriately with its preconditions.
   * @param block the block that will be executed by the child - this may be of
   *              type {@link PreConditionBlock} if declared with
   *              {@link PreConditionBlock#with(PreConditions, Block)}
   * @param taggingState the tagging state in the parent of this suite or spec.
   *                     This is used to determine what filters apply to the block
   * @return the child itself for fluent calling
   */
  default Child applyPreConditions(final Block block, final TaggingState taggingState) {
    findApplicablePreconditions(block).applyTo(this, taggingState);

    return this;
  }

  /**
   * Provide the precondition object for this child's block, which are
   * either in the block itself, or provided as a default.
   * @param block the block which may have preconditions
   * @return a non null preconditions object to use
   */
  static PreConditions findApplicablePreconditions(final Block block) {
    if (block instanceof PreConditionBlock) {
      return ((PreConditionBlock) block).getPreconditions();
    } else {
      return defaultPreConditions();
    }
  }
}

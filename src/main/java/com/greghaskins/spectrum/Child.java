package com.greghaskins.spectrum;

import static com.greghaskins.spectrum.PreConditions.Factory.defaultPreConditions;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

interface Child {

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

  static PreConditions findApplicablePreconditions(final Block block) {
    if (block instanceof PreConditionBlock) {
      return ((PreConditionBlock) block).getPreconditions();
    } else {
      return defaultPreConditions();
    }
  }
}

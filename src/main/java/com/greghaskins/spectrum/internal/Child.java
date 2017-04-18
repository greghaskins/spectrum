package com.greghaskins.spectrum.internal;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.internal.configuration.ConfiguredBlock;
import com.greghaskins.spectrum.internal.configuration.TaggingFilterCriteria;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

public interface Child {

  Description getDescription();

  void run(RunReporting<Description, Failure> reporting);

  int testCount();

  void focus();

  void ignore();

  /**
   * Is either this child ignored or are all of its children ignored.
   * @return true if nothing will run from here
   */
  boolean isEffectivelyIgnored();

  /**
   * Is this child something which runs as a test.
   * @return if the child is atomic
   */
  default boolean isAtomic() {
    return false;
  }

  /**
   * Does this child appear as an individual test within the test runner.
   * @return true if it's an individual test item in the runner
   */
  default boolean isLeaf() {
    return false;
  }

  /**
   * Gets the object to be filtered appropriately with its preconditions.
   * @param block the block that will be executed by the child - this may be of
   *              type {@link ConfiguredBlock}.
   * @param taggingFilterCriteria the tagging state in the parent of this suite or spec.
   *                     This is used to determine what filters apply to the block
   * @return the child itself for fluent calling
   */
  default Child applyPreconditions(final Block block,
      final TaggingFilterCriteria taggingFilterCriteria) {
    ConfiguredBlock.findApplicablePreconditions(block).applyTo(this, taggingFilterCriteria);

    return this;
  }
}

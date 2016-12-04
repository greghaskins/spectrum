package com.greghaskins.spectrum.internal;

import static com.greghaskins.spectrum.model.BlockConfiguration.Factory.defaultPreConditions;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.model.BlockConfiguration;
import com.greghaskins.spectrum.model.TaggingFilterCriteria;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public interface Child {

  Description getDescription();

  void run(RunNotifier notifier);

  int testCount();

  void focus();

  void ignore();

  /**
   * Is this child something which runs as a test.
   * @return if the child is atomic
   */
  default boolean isAtomic() {
    return false;
  }

  /**
   * Gets the object to be filtered appropriately with its preconditions.
   * @param block the block that will be executed by the child - this may be of
   *              type {@link ConfiguredBlock} if declared with
   *              {@link ConfiguredBlock#with(BlockConfiguration, Block)}
   * @param taggingFilterCriteria the tagging state in the parent of this suite or spec.
   *                     This is used to determine what filters apply to the block
   * @return the child itself for fluent calling
   */
  default Child applyPreConditions(final Block block,
      final TaggingFilterCriteria taggingFilterCriteria) {
    findApplicablePreconditions(block).applyTo(this, taggingFilterCriteria);

    return this;
  }

  /**
   * Provide the precondition object for this child's block, which are
   * either in the block itself, or provided as a default.
   * @param block the block which may have preconditions
   * @return a non null preconditions object to use
   */
  static BlockConfiguration findApplicablePreconditions(final Block block) {
    if (block instanceof ConfiguredBlock) {
      return ((ConfiguredBlock) block).getPreconditions();
    } else {
      return defaultPreConditions();
    }
  }
}

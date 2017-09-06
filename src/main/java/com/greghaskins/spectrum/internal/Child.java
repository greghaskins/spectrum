package com.greghaskins.spectrum.internal;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.internal.configuration.ConfiguredBlock;
import com.greghaskins.spectrum.internal.configuration.TaggingFilterCriteria;
import com.greghaskins.spectrum.internal.hooks.Hook;

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
}

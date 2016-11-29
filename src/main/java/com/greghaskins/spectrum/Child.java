package com.greghaskins.spectrum;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

interface Child {

  Description getDescription();

  void run(RunNotifier notifier);

  int testCount();

  void focus();

  void ignore();

  default Child applyPreConditions(Block block, TaggingState taggingState) {
    if (block instanceof PreConditionBlock) {
      ((PreConditionBlock) block).getPreconditions().applyTo(this, taggingState);
    } else {
      new PreConditions().applyTo(this, taggingState);
    }

    return this;
  }
}

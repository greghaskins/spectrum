package com.greghaskins.spectrum;

import com.greghaskins.spectrum.Spectrum.Block;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

class Spec implements Child {

  private final Block block;
  private final Description description;
  private final Parent parent;
  private boolean ignored = false;

  public Spec(final Description description, final Block block, final Parent parent) {
    this.description = description;
    this.block = block;
    this.parent = parent;
    this.ignored = parent.isIgnored();
  }

  @Override
  public Description getDescription() {
    return this.description;
  }

  @Override
  public void run(final RunNotifier notifier) {
    if (this.ignored) {
      notifier.fireTestIgnored(this.description);
      return;
    }

    notifier.fireTestStarted(this.description);
    try {
      this.block.run();
    } catch (final Throwable error) {
      notifier.fireTestFailure(new Failure(this.description, error));
    }
    notifier.fireTestFinished(this.description);
  }

  @Override
  public int testCount() {
    return 1;
  }

  @Override
  public void focus() {
    if (this.ignored) {
      return;
    }

    this.parent.focus(this);
  }

  @Override
  public void ignore() {
    ignored = true;
  }
}

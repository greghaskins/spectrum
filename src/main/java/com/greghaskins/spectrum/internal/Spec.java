package com.greghaskins.spectrum.internal;


import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.internal.hooks.Hook;
import com.greghaskins.spectrum.internal.hooks.HookContext;
import com.greghaskins.spectrum.internal.hooks.Hooks;
import com.greghaskins.spectrum.internal.hooks.NonReportingHook;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

final class Spec implements LeafChild {

  private final Block block;
  private final Description description;
  private final Parent parent;
  private boolean ignored = false;
  private Hooks leafHooks = new Hooks();

  Spec(final Description description, final Block block, final Parent parent) {
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
  public void run(final RunReporting<Description, Failure> notifier) {
    if (this.ignored) {
      notifier.fireTestIgnored(this.description);
      return;
    }

    // apply leaf hooks around the inner block
    leafHooks.sorted().runAround(this.description, notifier, block);
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
    this.ignored = true;
  }

  @Override
  public boolean isAtomic() {
    return true;
  }

  @Override
  public boolean isLeaf() {
    return true;
  }

  @Override
  public boolean isEffectivelyIgnored() {
    return ignored;
  }

  @Override
  public void addLeafHook(NonReportingHook leafHook, HookContext.Precedence precedence) {
    // hooks at this level are always at the same point in the hierarchy and applying to each child
    leafHooks.add(new HookContext(leafHook, 0, HookContext.AppliesTo.EACH_CHILD, precedence));
  }
}

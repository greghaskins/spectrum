package com.greghaskins.spectrum.model;

import com.greghaskins.spectrum.Hook;

/**
 * Container for a hook. Determines whether the hook runs just within the suite, or whether it
 * propagates down to test level.
 */
public class HookContext {
  private final Hook hook;
  private final boolean atomicOnly;
  private final boolean once;
  private final boolean eachChild;

  /**
   * Construct a hook context.
   * @param hook the hook being wrapped
   * @param atomicOnly is this run for atomics only?
   * @param once is this a one time hook?
   * @param eachChild is this for each individual child of the parent?
   */
  public HookContext(final Hook hook, final boolean atomicOnly,
      final boolean once, final boolean eachChild) {
    this.hook = hook;
    this.atomicOnly = atomicOnly;
    this.once = once;
    this.eachChild = eachChild;
  }

  /**
   * Provides the hook within the context.
   * @return the hook
   */
  public Hook getHook() {
    return hook;
  }

  /**
   * Does this hook apply only to atomic items. Atomic hooks will propagate
   * down to the most atomic level.
   * @return if this is for atomic items only
   */
  public boolean isAtomicOnly() {
    return atomicOnly;
  }

  /**
   * Is this hook to be run once only. Such hooks are run at the level they
   * are declared and never again.
   * @return is this a one time hook?
   */
  public boolean isOnce() {
    return once;
  }

  /**
   * Does this hook apply to all immediate children of the declared location.
   * @return if this is a hook to apply before each direct child
   */
  public boolean isEachChild() {
    return eachChild;
  }
}

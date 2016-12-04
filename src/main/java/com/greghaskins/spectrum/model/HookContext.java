package com.greghaskins.spectrum.model;

import static com.greghaskins.spectrum.model.HookContext.AppliesTo.ATOMIC_ONLY;
import static com.greghaskins.spectrum.model.HookContext.AppliesTo.EACH_CHILD;
import static com.greghaskins.spectrum.model.HookContext.AppliesTo.ONCE;

import java.util.concurrent.atomic.AtomicInteger;

import com.greghaskins.spectrum.Hook;

/**
 * Container for a hook. Determines whether the hook runs just within the suite, or whether it
 * propagates down to test level.
 */
public class HookContext {
  private final Hook hook;
  private final AppliesTo appliesTo;
  private final int sequenceNumber;

  private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger();

  public enum AppliesTo {
    ATOMIC_ONLY,
    ONCE,
    EACH_CHILD
  }

  /**
   * Construct a hook context.
   * @param hook the hook being wrapped
   * @param appliesTo where in the lifecycle is this hook applied?
   */
  public HookContext(final Hook hook, final AppliesTo appliesTo) {
    this.hook = hook;
    this.appliesTo = appliesTo;
    this.sequenceNumber = SEQUENCE_GENERATOR.incrementAndGet();
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
    return appliesTo.equals(ATOMIC_ONLY);
  }

  /**
   * Is this hook to be run once only. Such hooks are run at the level they
   * are declared and never again.
   * @return is this a one time hook?
   */
  public boolean isOnce() {
    return appliesTo.equals(ONCE);
  }

  /**
   * Does this hook apply to all immediate children of the declared location.
   * @return if this is a hook to apply before each direct child
   */
  public boolean isEachChild() {
    return appliesTo.equals(EACH_CHILD);
  }
}

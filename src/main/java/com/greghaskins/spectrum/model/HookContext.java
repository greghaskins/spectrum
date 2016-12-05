package com.greghaskins.spectrum.model;

import static com.greghaskins.spectrum.model.HookContext.AppliesTo.ATOMIC_ONLY;
import static com.greghaskins.spectrum.model.HookContext.AppliesTo.EACH_CHILD;
import static com.greghaskins.spectrum.model.HookContext.AppliesTo.ONCE;

import com.greghaskins.spectrum.Hook;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Container for a hook. Determines whether the hook runs just within the suite, or whether it
 * propagates down to test level.
 */
public class HookContext implements Comparable<HookContext> {
  private final Hook hook;
  private final AppliesTo appliesTo;
  private final int sequenceNumber;
  private final Precedence precedence;
  private final int hierarchyDepth;

  private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger();

  /**
   * Where in the lifecycle the hook is applied.
   */
  public enum AppliesTo {
    /**
     * Run around an atomic item.
     */
    ATOMIC_ONLY,

    /**
     * Run once within the parent which declares it.
     */
    ONCE,

    /**
     * Run for each immediate child of the parent which declares it.
     */
    EACH_CHILD
  }


  /**
   * A precedence object to allow hooks to be used in the right order.
   * Note the internal integers help enforce the precedence but are not
   * for user consumption.
   */
  public enum Precedence {
    /**
     * Guaranteed the first to be run.
     */
    ROOT(0),

    /**
     * Aside from the root, this happens before anything else.
     */
    OUTER(1),

    /**
     * Guaranteed tidy up code should be allowed to run no matter
     * what, once the test has started.
     */
    GUARANTEED_CLEAN_UP(2),

    /**
     * Set up code should run before the local context.
     */
    SET_UP(3),

    /**
     * Local context - the order depends on declaration.
     */
    LOCAL(4);

    private int ordering;

    /**
     * How the precedence is sequenced against others.
     * @return the order for sorting.
     */
    int getOrdering() {
      return ordering;
    }

    Precedence(final int ordering) {
      this.ordering = ordering;
    }
  }

  /**
   * Construct a hook context.
   * @param hook the hook being wrapped
   * @param hierarchyDepth where in the hierarchy this was created
   * @param appliesTo where in the lifecycle is this hook applied?
   * @param precedence the importance of this within the lifecycle
   */
  public HookContext(final Hook hook, final int hierarchyDepth,
      final AppliesTo appliesTo, final Precedence precedence) {
    this.hook = hook;
    this.appliesTo = appliesTo;
    this.sequenceNumber = SEQUENCE_GENERATOR.incrementAndGet();
    this.precedence = precedence;
    this.hierarchyDepth = hierarchyDepth;
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

  /**
   * In hook context terms, if this returns a positive number it means this item is
   * higher priority than other.
   * @param other to compare with
   * @return positive if this one is higher priority, negative if this one is
   *         lower this can NEVER return 0 as no two contexts can be equal owing
   *         to the auto incremented sequence number
   */
  @Override
  public int compareTo(HookContext other) {
    // for us, lower means MORE IMPORTANT, so negate
    // the answer from a method which looks less surprising

    return -comparisonWith(other);
  }

  private int comparisonWith(HookContext other) {
    if (precedence != other.precedence) {
      return Integer.compare(precedence.getOrdering(), other.precedence.getOrdering());
    }

    if (hierarchyDepth != other.hierarchyDepth) {
      return Integer.compare(hierarchyDepth, other.hierarchyDepth);
    }

    return Integer.compare(sequenceNumber, other.sequenceNumber);
  }
}

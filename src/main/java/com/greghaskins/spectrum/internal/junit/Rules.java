package com.greghaskins.spectrum.internal.junit;

import com.greghaskins.spectrum.internal.Hook;
import com.greghaskins.spectrum.internal.HookContext;

import java.util.function.Supplier;

/**
 * How to hook JUnit rules into Spectrum.
 */
public interface Rules {

  @FunctionalInterface
  static interface Target {
    void addHook(Hook hook, HookContext.AppliesTo appliesTo, HookContext.Precedence precedence);
  }

  /**
   * Apply JUnit rules by adding a hook to hook in the rules class.
   * This runs the rules for each atomic.
   * @param rulesClass type of object to create
   * @param target the insertion point to add JUnit rules as hooks
   * @param <T> type of object that will be built
   * @return a supplier that provides access to the test object created
   */
  static <T> Supplier<T> applyRules(Class<T> rulesClass, Target target) {
    RuleContext<T> context = new RuleContext<>(rulesClass);
    target.addHook(context.classHook(), HookContext.AppliesTo.ONCE, HookContext.Precedence.LOCAL);
    target.addHook(context.methodHook(), HookContext.AppliesTo.ATOMIC_ONLY,
        HookContext.Precedence.LOCAL);

    return context;
  }

  /**
   * Add a test object junit.rule set.
   * @param object the JUnit test object - this will never be recreated
   * @param target the insertion point to add JUnit rules as hooks
   * @param <T> type of the test object
   */
  static <T> void applyRules(T object, Target target) {
    RuleContext<T> context = new RuleContext<>(object);
    if (context.hasAnyJUnitAnnotations()) {
      target.addHook(context.classHook(), HookContext.AppliesTo.ONCE,
          HookContext.Precedence.LOCAL);
      target.addHook(context.methodHook(), HookContext.AppliesTo.ATOMIC_ONLY,
          HookContext.Precedence.LOCAL);
    }
  }

}

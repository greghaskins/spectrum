package com.greghaskins.spectrum.internal.junit;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.model.HookContext;

import java.util.function.Supplier;

/**
 * How to hook JUnit rules into Spectrum.
 */
public interface Rules {
  /**
   * Apply JUnit rules by adding a hook to hook in the rules class.
   * This runs the rules for each atomic.
   * @param rulesClass type of object to create
   * @param <T> type of object that will be build
   * @return a supplier that provides access to the test object created
   */
  static <T> Supplier<T> applyRules(Class<T> rulesClass) {
    RuleContext<T> context = new RuleContext<>(rulesClass);
    Spectrum.addHook(context.classHook(), HookContext.AppliesTo.ONCE, HookContext.Precedence.LOCAL);
    Spectrum.addHook(context.methodHook(), HookContext.AppliesTo.ATOMIC_ONLY,
        HookContext.Precedence.LOCAL);

    return context;
  }

  /**
   * Add a test object junit.rule set.
   * @param object the JUnit test object - this will never be recreated
   */
  static void applyRules(Object object) {
    RuleContext context = new RuleContext<>(object);
    if (context.hasAnyJUnitAnnotations()) {
      Spectrum.addHook(context.classHook(), HookContext.AppliesTo.ONCE,
          HookContext.Precedence.LOCAL);
      Spectrum.addHook(context.methodHook(), HookContext.AppliesTo.ATOMIC_ONLY,
          HookContext.Precedence.LOCAL);
    }
  }

  /**
   * Apply JUnit rules by adding a hook to hook in the rules class. This
   * will run the junit.rule the level of the suite only.
   * @param rulesClass type of object to create
   * @param <T> type of object that will be build
   * @return a supplier that provides access to the test object created
   */
  static <T> Supplier<T> applyRulesHere(Class<T> rulesClass) {
    RuleContext<T> context = new RuleContext<>(rulesClass);
    Spectrum.addHook(context.classHook(), HookContext.AppliesTo.ONCE, HookContext.Precedence.LOCAL);
    Spectrum.addHook(context.methodHook(), HookContext.AppliesTo.EACH_CHILD,
        HookContext.Precedence.LOCAL);

    return context;
  }
}

package com.greghaskins.spectrum.internal.hooks;

import static com.greghaskins.spectrum.internal.blocks.NotifyingBlock.wrapWithReporting;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.Variable;
import com.greghaskins.spectrum.internal.RunReporting;
import com.greghaskins.spectrum.internal.blocks.NotifyingBlock;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * Collection of hooks. It is a linked list, but provides some helpers for
 * passing hooks down a generation.
 */
public class Hooks extends ArrayList<HookContext> {
  private static final long serialVersionUID = 1L;

  public Hooks once() {
    return filtered(HookContext::isOnce);
  }

  public Hooks forNonAtomic() {
    return filtered(context -> !context.isOnce() && !context.isAtomicOnly());
  }

  public Hooks forAtomic() {
    return filtered(HookContext::isAtomicOnly);
  }

  public Hooks forThisLevel() {
    return filtered(HookContext::isEachChild);
  }

  /**
   * Run the hooks on the right in the correct order AFTER these ones.
   * @param other to add to this
   * @return this for fluent use
   */
  public Hooks plus(Hooks other) {
    addAll(other);

    return this;
  }

  /**
   * Return a hooks object where the hooks from this have been sorted into execution order.
   * @return new hooks sorted into the order for execution
   */
  public Hooks sorted() {
    Hooks result = new Hooks();
    result.addAll(this);
    result.sort(HookContext::compareTo);

    return result;
  }

  /**
   * Convert the hooks into a chain of responsibility and execute as
   * a consumer of the given block.
   * @param description test node being run
   * @param reporting test result notifier
   * @param block to execute
   */
  public void runAround(final Description description, final RunReporting<Description, Failure> reporting,
      final Block block) {
    NotifyingBlock.run(description, reporting,
        () -> runAroundInternal(description, reporting, block));
  }

  private void runAroundInternal(final Description description,
      final RunReporting<Description, Failure> reporting,
      final Block block) throws Throwable {
    Variable<Boolean> hooksRememberedToRunTheInner = new Variable<>(false);

    Hook chainOfResponsibility = createChainOfResponsibility(hooksRememberedToRunTheInner);
    executeChain(description, reporting, block, chainOfResponsibility);

    if (!hooksRememberedToRunTheInner.get()) {
      throw new RuntimeException("At least one of the test hooks did not run the test block.");
    }
  }

  private Hook createChainOfResponsibility(Variable<Boolean> hooksRememberedToRunTheInner) {
    Hook chainOfResponsibility = innerHook(hooksRememberedToRunTheInner);

    for (HookContext context : this) {
      chainOfResponsibility = wrap(chainOfResponsibility, context);
    }

    return chainOfResponsibility;
  }

  private void executeChain(Description description, RunReporting<Description, Failure> reporting,
      Block block, Hook chainOfResponsibility)
      throws Throwable {
    chainOfResponsibility.accept(description, reporting, block);
  }

  private Hook innerHook(Variable<Boolean> hooksRememberedToRunTheInner) {
    return (description, notifier, innerBlock) -> {
      hooksRememberedToRunTheInner.set(true);
      innerBlock.run();
    };
  }

  private Hook wrap(final Hook inner, final HookContext outer) {
    return (description, reporting, block) -> outer.getHook().accept(description, reporting,
        wrapWithReporting(description, reporting,
            () -> inner.accept(description, reporting, block)));
  }

  private Hooks filtered(Predicate<HookContext> predicate) {
    Hooks filtered = new Hooks();
    stream().filter(predicate).forEach(filtered::add);

    return filtered;
  }

}

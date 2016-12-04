package com.greghaskins.spectrum.model;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.ThrowingConsumer;
import com.greghaskins.spectrum.Variable;
import com.greghaskins.spectrum.internal.NotifyingBlock;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Collection of hooks. It is a linked list, but provides some helpers for
 * passing hooks down a generation.
 */
public class Hooks extends LinkedList<HookContext> {
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
    List<HookContext> list = other.stream().collect(Collectors.toList());
    Collections.reverse(list);
    list.forEach(this::addFirst);

    return this;
  }

  /**
   * Convert the hooks into a chain of responsibility and execute as
   * a consumer of the given block.
   * @param description test node being run
   * @param notifier test result notifier
   * @param block to execute
   */
  public void runAround(final Description description, final RunNotifier notifier,
      final Block block) {
    NotifyingBlock.run(description, notifier, () -> runAroundInternal(block));
  }

  private void runAroundInternal(Block block) {
    Variable<Boolean> hooksRememberedToRunTheInner = new Variable<>(false);
    ThrowingConsumer<Block> consumer = innerBlock -> {
      hooksRememberedToRunTheInner.set(true);
      innerBlock.run();
    };

    for (HookContext context : this) {
      consumer = wrap(consumer, context);
    }
    consumer.accept(block);

    if (!hooksRememberedToRunTheInner.get()) {
      throw new RuntimeException("At least one of the test hooks did not run the test block.");
    }
  }

  private ThrowingConsumer<Block> wrap(ThrowingConsumer<Block> inner, HookContext outer) {
    return block -> outer.getHook().acceptOrThrow(() -> inner.acceptOrThrow(block));
  }


  private Hooks filtered(Predicate<HookContext> predicate) {
    Hooks filtered = new Hooks();
    stream().filter(predicate).forEach(filtered::add);

    return filtered;
  }

}

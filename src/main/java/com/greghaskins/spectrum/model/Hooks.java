package com.greghaskins.spectrum.model;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.ThrowingConsumer;

import java.util.LinkedList;
import java.util.function.Predicate;

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
    other.forEach(this::addFirst);

    return this;
  }

  /**
   * Convert the hooks into a chain of responsibility and execute as
   * a consumer of the given block.
   * @param block to execute
   */
  public void runAround(Block block) {
    ThrowingConsumer<Block> consumer = Block::run;
    for (HookContext context : this) {
      consumer = wrap(consumer, context);
    }
    consumer.accept(block);
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

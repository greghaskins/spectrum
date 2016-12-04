package com.greghaskins.spectrum;

/**
 * A hook allows you to inject functionality before and/or after a {@link Block}.
 * Just implement the {@link ThrowingConsumer#acceptOrThrow(Object)} method and
 * call {@link Block#run()} within your implementation.
 * If your hook is going to provide an object to the running test, then implement
 * {@link SupplyingHook} or subclass {@link AbstractSupplyingHook}.
 */
public interface Hook extends ThrowingConsumer<Block> {
}

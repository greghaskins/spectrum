package com.greghaskins.spectrum.internal;

import java.util.function.Supplier;

/**
 * A hook that supplies a value within a running test. The hook will consume
 * a block, which is to be run within the hook's setup/teardown. That block will access
 * the hook object as a supplier, which will provide an object of T. The T object should also
 * be destroyed by the hook's teardown.
 * @param <T> the type of object the hook will supply within its execution scope.
 */
public interface SupplyingHook<T> extends Hook, Supplier<T> {
}

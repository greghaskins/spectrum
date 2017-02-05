package com.greghaskins.spectrum.internal;

import com.greghaskins.spectrum.ThrowingSupplier;

/**
 * Implementation of let as a supplying hook.
 */
public class LetHook<T> extends AbstractSupplyingHook<T> {
  private final ThrowingSupplier<T> supplier;

  public LetHook(final ThrowingSupplier<T> supplier) {
    this.supplier = supplier;
  }

  protected T before() {
    return supplier.get();
  }
}

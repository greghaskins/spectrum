package com.greghaskins.spectrum.internal.hooks;

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

  protected String getExceptionMessageIfUsedAtDeclarationTime() {
    return "Cannot use the value from let() in a suite declaration. "
        + "It may only be used in the context of a running spec.";
  }
}

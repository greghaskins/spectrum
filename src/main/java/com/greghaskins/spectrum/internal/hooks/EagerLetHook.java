package com.greghaskins.spectrum.internal.hooks;

import com.greghaskins.spectrum.ThrowingSupplier;

/**
 * Implementation of an eager version of {@code let}.
 *
 * <p>Sematics are the same as with {@link LetHook}, except that all values are calculated at the
 * start of the test, rather than on an as-needed basis.
 */
public class EagerLetHook<T> extends AbstractSupplyingHook<T> {
  private final ThrowingSupplier<T> supplier;

  public EagerLetHook(final ThrowingSupplier<T> supplier) {
    this.supplier = supplier;
  }

  protected T before() {
    return supplier.get();
  }

  protected String getExceptionMessageIfUsedAtDeclarationTime() {
    return "Cannot use the value from eagerLet() in a suite declaration. "
        + "It may only be used in the context of a running spec.";
  }
}

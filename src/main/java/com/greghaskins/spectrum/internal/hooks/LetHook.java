package com.greghaskins.spectrum.internal.hooks;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.ThrowingSupplier;
import com.greghaskins.spectrum.Variable;
import com.greghaskins.spectrum.internal.DeclarationState;
import com.greghaskins.spectrum.internal.RunReporting;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

/**
 * Implementation of {@code let} as a supplying hook.
 *
 * <p>Using {@code let} allows you to define shared values that can be used by multiple tests,
 * without having to worry about cleaning up the values between tests to prevent shared state in
 * one test from affecting the results of another.
 *
 * <p>Values are lazily initialized and then cached, so a value is not calculated until the first
 * time it is needed in a given test. Subsequent fetches of the value within the same test will
 * return the cached value.
 */
public class LetHook<T> implements SupplyingHook<T> {
  private final ThrowingSupplier<T> supplier;
  private final Variable<T> cachedValue = new Variable<>();
  private boolean isCached;

  public LetHook(final ThrowingSupplier<T> supplier) {
    this.supplier = supplier;
    this.isCached = false;
  }

  @Override
  public void accept(final Description description,
      final RunReporting<Description, Failure> reporting, final Block block)
      throws Throwable {
    try {
      block.run();
    } finally {
      clear();
    }
  }

  @Override
  public T get() {
    assertSpectrumIsRunningTestsNotDeclaringThem();

    if (!this.isCached) {
      this.cachedValue.set(supplier.get());

      this.isCached = true;
    }

    return this.cachedValue.get();
  }

  protected String getExceptionMessageIfUsedAtDeclarationTime() {
    return "Cannot use the value from let() in a suite declaration. "
        + "It may only be used in the context of a running spec.";
  }

  private void clear() {
    this.isCached = false;
    this.cachedValue.set(null);
  }

  /**
   * Will throw an exception if this method happens to be called while Spectrum is still defining
   * tests, rather than executing them. Useful to see if a hook is being accidentally used during
   * definition.
   */
  private void assertSpectrumIsRunningTestsNotDeclaringThem() {
    if (DeclarationState.instance().getCurrentSuiteBeingDeclared() != null) {
      throw new IllegalStateException(getExceptionMessageIfUsedAtDeclarationTime());
    }
  }
}

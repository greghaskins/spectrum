package com.greghaskins.spectrum.internal.hooks;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.Variable;
import com.greghaskins.spectrum.internal.DeclarationState;
import com.greghaskins.spectrum.internal.RunReporting;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

/**
 * A base class for {@link SupplyingHook hooks that supply a value}.
 *
 * <p>Override {@link #before} or {@link #after}. Return the singleton value from the before method.
 * You can use this to write any plugin which needs to make a value visible to the specs.
 * This is not the only way to achieve that - you can also build from {@link SupplyingHook}
 * but this captures the template for a complex hook.
 */
abstract class AbstractSupplyingHook<T> implements SupplyingHook<T> {

  private final Variable<T> value = new Variable<>();

  /**
   * Override this to supply behaviour for before the block is run.
   *
   * @return the value that the singleton will store to supply
   */
  protected abstract T before();

  /**
   * Override this to give a message for when the value from this hook gets used any time other than
   * while running a test.
   *
   * @return the IllegalStateException message to use
   */
  protected abstract String getExceptionMessageIfUsedAtDeclarationTime();

  /**
   * Override this to supply behaviour for after the block is run.
   */
  protected void after() {}

  /**
   * Template method for a hook which supplies.
   *
   * @param description description - unused here
   * @param reporting reporting - unused here
   * @param block       the inner block that will be run
   * @throws Throwable on error
   */
  @Override
  public void accept(final Description description, final RunReporting<Description, Failure> reporting,
      final Block block) throws Throwable {
    try {
      this.value.set(before());
      block.run();
    } finally {
      try {
        after();
      } finally {
        clear();
      }
    }
  }

  @Override
  public T get() {
    assertSpectrumIsRunningTestsNotDeclaringThem();

    return this.value.get();
  }

  private void clear() {
    this.value.set(null);
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

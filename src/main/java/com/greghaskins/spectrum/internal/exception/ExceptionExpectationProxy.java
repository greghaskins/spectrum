package com.greghaskins.spectrum.internal.exception;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.ExceptionExpectation;
import com.greghaskins.spectrum.internal.configuration.BlockConfiguration;
import com.greghaskins.spectrum.internal.hooks.Hook;
import com.greghaskins.spectrum.internal.hooks.NonReportingHook;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Proxy to the exception expectation supplier.
 */
public class ExceptionExpectationProxy implements ExceptionExpectation {
  private Supplier<ExceptionExpectation> supplier;

  public ExceptionExpectationProxy(final Supplier<ExceptionExpectation> supplier) {
    this.supplier = supplier;
  }

  @Override
  public void expect(final Class<?> typeOfException) {
    proxyObject().expect(typeOfException);
  }

  @Override
  public void expect(final String description, final Predicate<Throwable> exceptionIsExpected) {
    proxyObject().expect(description, exceptionIsExpected);
  }

  @Override
  public void validateThrowable(final Throwable thrown) throws Throwable {
    proxyObject().validateThrowable(thrown);
  }

  @Override
  public void verifyNoExceptionExpected() {
    proxyObject().verifyNoExceptionExpected();
  }

  @Override
  public void expectMessage(final CharSequence message) {
    proxyObject().expectMessage(message);
  }

  @Override
  public void expectMessageContains(final CharSequence substring) {
    proxyObject().expectMessageContains(substring);
  }

  public BlockConfiguration asConfiguration() {
    return BlockConfiguration.of(new ExceptionConfiguration(this));
  }

  NonReportingHook asHook() {
    return NonReportingHook.nonReportingHookFrom(Hook.from(this::verifyExceptionBehaviour));
  }

  private ExceptionExpectation proxyObject() {
    return supplier.get();
  }

  private void verifyExceptionBehaviour(final Block block) throws Throwable {
    boolean hasCaught = false;
    try {
      block.run();
    } catch (Throwable thrown) {
      hasCaught = true;
      validateThrowable(thrown);
    } finally {
      if (!hasCaught) {
        verifyNoExceptionExpected();
      }
    }
  }
}

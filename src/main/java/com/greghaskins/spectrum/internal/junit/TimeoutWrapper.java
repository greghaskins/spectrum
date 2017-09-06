package com.greghaskins.spectrum.internal.junit;

import static com.greghaskins.spectrum.internal.hooks.NonReportingHook.nonReportingHookFrom;
import static com.greghaskins.spectrum.internal.junit.RuleContext.statementOf;

import com.greghaskins.spectrum.internal.hooks.NonReportingHook;

import org.junit.internal.runners.statements.FailOnTimeout;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Wrap JUnit's timeout mechanism as a {@link NonReportingHook}.
 */
public interface TimeoutWrapper {
  /**
   * Convert the timeout into a {@link NonReportingHook} which executes
   * the inner inside a daemon thread, failing if it takes too long.
   * @param timeout duration of the timeout
   * @return hook which implements the timeout
   */
  static NonReportingHook timeoutHook(Duration timeout) {
    return nonReportingHookFrom(
        (description, reporting, block) -> withAppliedTimeout(FailOnTimeout.builder(), timeout)
            .build(statementOf(block))
            .evaluate());
  }

  /**
   * Apply a timeout expressed as a duration to a builder of a {@link FailOnTimeout} object.
   * @param builder to modify
   * @param timeout duration of the timeout
   * @return the builder input - for fluent use.
   */
  static FailOnTimeout.Builder withAppliedTimeout(FailOnTimeout.Builder builder, Duration timeout) {
    builder.withTimeout(timeout.toNanos(), TimeUnit.NANOSECONDS);

    return builder;
  }
}

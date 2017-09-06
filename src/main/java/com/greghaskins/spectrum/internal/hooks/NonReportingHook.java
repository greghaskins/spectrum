package com.greghaskins.spectrum.internal.hooks;

/**
 * A hook which requires that the block inside it is not self-reporting.
 */
public interface NonReportingHook extends Hook {
  /**
   * Factory method to create a hook which doesn't want its inner to be allowed to report on itself.
   * @param hook a hook object that can consume description, reporting and block to run for us to decorate
   * @return a non reportable hook
   */
  static NonReportingHook nonReportingHookFrom(final Hook hook) {
    return hook::accept;
  }

  @Override
  default boolean requiresUnreportedInnerBlock() {
    return true;
  }
}

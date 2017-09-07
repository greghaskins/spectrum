package com.greghaskins.spectrum.internal.exception;

import com.greghaskins.spectrum.internal.Child;
import com.greghaskins.spectrum.internal.LeafChild;
import com.greghaskins.spectrum.internal.configuration.BlockConfigurable;
import com.greghaskins.spectrum.internal.configuration.TaggingFilterCriteria;
import com.greghaskins.spectrum.internal.hooks.HookContext;

/**
 * Provide exception handling as a configuration.
 */
public class ExceptionConfiguration implements BlockConfigurable<ExceptionConfiguration> {
  private ExceptionExpectationProxy expectationProxy;

  public ExceptionConfiguration(ExceptionExpectationProxy expectationProxy) {
    this.expectationProxy = expectationProxy;
  }

  @Override
  public boolean inheritedByChild() {
    return true;
  }

  @Override
  public void applyTo(Child child, TaggingFilterCriteria state) {
    if (child instanceof LeafChild) {
      ((LeafChild) child).addLeafHook(expectationProxy.asHook(), HookContext.Precedence.ROOT);
    }
  }

  @Override
  public BlockConfigurable<ExceptionConfiguration> merge(BlockConfigurable<?> other) {
    // always supersedes higher level definition

    return this;
  }
}

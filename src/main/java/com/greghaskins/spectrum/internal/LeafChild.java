package com.greghaskins.spectrum.internal;

import com.greghaskins.spectrum.internal.hooks.HookContext;
import com.greghaskins.spectrum.internal.hooks.NonReportingHook;

/**
 * Interface for a child that is also a leaf node in the test hierarchy.
 */
public interface LeafChild extends Child {
  /**
   * Add an additional hook directly to a leaf of the hierarchy.
   * @param leafHook hook to add
   * @param precedence precedence, for sorting hooks into order
   */
  void addLeafHook(NonReportingHook leafHook, HookContext.Precedence precedence);
}

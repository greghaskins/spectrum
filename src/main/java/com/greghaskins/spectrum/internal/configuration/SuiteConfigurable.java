package com.greghaskins.spectrum.internal.configuration;

import com.greghaskins.spectrum.internal.Suite;

public interface SuiteConfigurable {
  void applyTo(Suite suite);
}

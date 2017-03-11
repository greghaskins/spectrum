package com.greghaskins.spectrum;

import com.greghaskins.spectrum.internal.Suite;
import com.greghaskins.spectrum.internal.configuration.SuiteConfigurable;

import java.util.ArrayList;
import java.util.List;

public class FilterConfigurationChain {

  private final List<SuiteConfigurable> configurables = new ArrayList<>();

  FilterConfigurationChain(SuiteConfigurable configurable) {
    this.configurables.add(configurable);
  }

  public FilterConfigurationChain and(FilterConfigurationChain chain) {
    this.configurables.addAll(chain.configurables);

    return this;
  }

  void applyTo(Suite suite) {
    this.configurables.forEach(configurable -> configurable.applyTo(suite));
  }
}

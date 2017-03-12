package com.greghaskins.spectrum.internal.configuration;


import com.greghaskins.spectrum.internal.Suite;

public class ExcludeTags implements SuiteConfigurable {

  private final String[] tags;

  public ExcludeTags(String[] tags) {
    this.tags = tags;
  }

  @Override
  public void applyTo(Suite suite) {
    suite.excludeTags(this.tags);
  }
}

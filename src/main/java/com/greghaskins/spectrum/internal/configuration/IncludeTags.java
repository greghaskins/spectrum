package com.greghaskins.spectrum.internal.configuration;


import com.greghaskins.spectrum.internal.Suite;

public class IncludeTags implements SuiteConfigurable {

  private final String[] tags;

  public IncludeTags(String[] tags) {
    this.tags = tags;
  }

  @Override
  public void applyTo(Suite suite) {
    suite.includeTags(this.tags);
  }
}

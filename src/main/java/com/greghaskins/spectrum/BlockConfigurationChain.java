package com.greghaskins.spectrum;

import com.greghaskins.spectrum.internal.BlockConfiguration;

public class BlockConfigurationChain {

  private BlockConfiguration blockConfiguration = BlockConfiguration.defaultConfiguration();

  public BlockConfigurationChain ignore() {
    this.blockConfiguration.ignore();

    return this;
  }

  public BlockConfigurationChain ignore(@SuppressWarnings("unused") String reason) {
    this.blockConfiguration.ignore();

    return this;
  }

  public BlockConfigurationChain focus() {
    this.blockConfiguration.focus();

    return this;
  }

  public BlockConfigurationChain tags(String... tags) {
    this.blockConfiguration.tags(tags);

    return this;
  }

  BlockConfiguration getBlockConfiguration() {
    return this.blockConfiguration;
  }

}

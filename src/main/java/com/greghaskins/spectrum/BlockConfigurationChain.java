package com.greghaskins.spectrum;

import com.greghaskins.spectrum.internal.BlockConfigurable;
import com.greghaskins.spectrum.internal.BlockConfiguration;

import java.util.stream.Stream;

/**
 * Chainable configuration of a {@link com.greghaskins.spectrum.internal.ConfiguredBlock}.
 * Use the factory methods in {@link Spectrum} like {@link Spectrum#ignore()},
 * {@link Spectrum#focus()} or {@link Spectrum#tags(String...)} to add configuration
 * to a block. The result will be a {@link BlockConfigurationChain}. To add configurations together
 * you use {@link BlockConfigurationChain#and(BlockConfigurationChain)}. This is fluent
 * so ands can be chained together.<br><br>
 * e.g.<pre>with(ignore().and(tags("a","b","c")).and(tags("d","e","f"), () -&gt; {...})</pre><br>
 * See also: {@link Spectrum#with(BlockConfigurationChain, Block)}
 */
public class BlockConfigurationChain {

  private BlockConfiguration blockConfiguration = BlockConfiguration.defaultConfiguration();

  /**
   * Fluent call to add a configurable to the configuration.
   * @param configurable to add.
   * @return this for fluent calling - users will use {@link #and(BlockConfigurationChain)}
   */
  BlockConfigurationChain with(BlockConfigurable<?> configurable) {
    blockConfiguration.add(configurable);

    return this;
  }

  /**
   * Add another configuration to the chain.
   * @param chain the configurable to add.
   * @return this for fluent calls
   */
  public BlockConfigurationChain and(BlockConfigurationChain chain) {
    chain.getConfigurables().forEach(this::with);

    return this;
  }

  BlockConfiguration getBlockConfiguration() {
    return this.blockConfiguration;
  }

  private Stream<BlockConfigurable<?>> getConfigurables() {
    return this.blockConfiguration.getConfigurables();
  }

}

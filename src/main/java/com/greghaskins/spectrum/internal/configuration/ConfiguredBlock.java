package com.greghaskins.spectrum.internal.configuration;

import com.greghaskins.spectrum.Block;

/**
 * A block with configuration data applied to it.
 */
public class ConfiguredBlock implements Block {
  private final BlockConfiguration blockConfiguration;
  private final Block innerBlock;

  /**
   * Surround a {@link Block} with the {@code with} statement to add preconditions and metadata to it.
   * E.g. <code>with(tags("foo"), () -&gt; {})</code>
   * @param blockConfiguration the precondition object - see the factory methods in
   *        {@link BlockConfiguration}
   * @param block the enclosed block
   * @return a PreconditionBlock to use
   */
  public static ConfiguredBlock with(final BlockConfiguration blockConfiguration,
      final Block block) {

    BlockConfiguration existingBlockConfiguration = configurationFromBlock(block);
    BlockConfiguration mergedBlockConfiguration =
        BlockConfiguration.merge(existingBlockConfiguration, blockConfiguration);

    return new ConfiguredBlock(mergedBlockConfiguration, block);
  }

  /**
   * Construct a ConfiguredBlock to wrap block execution with configuration data.
   * @param innerBlock the block to wrap
   */
  private ConfiguredBlock(final BlockConfiguration blockConfiguration, final Block innerBlock) {
    this.blockConfiguration = blockConfiguration;
    this.innerBlock = innerBlock;
  }

  /**
   * Get the configuration that applies to the block.
   * @return the configuration on the block
   */
  BlockConfiguration getConfiguration() {
    return this.blockConfiguration;
  }

  @Override
  public void run() throws Throwable {
    this.innerBlock.run();
  }

  /**
   * Provide any configuration data for this child's block or the default.
   * @param block the block which may have configuration data
   * @return a non null {@link BlockConfiguration} object to use
   */
  public static BlockConfiguration configurationFromBlock(final Block block) {
    if (block instanceof ConfiguredBlock) {
      return ((ConfiguredBlock) block).getConfiguration();
    } else {
      return BlockConfiguration.defaultConfiguration();
    }
  }
}

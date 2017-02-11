package com.greghaskins.spectrum.internal;

import com.greghaskins.spectrum.Block;

/**
 * A block with preconditions set on it.
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

    BlockConfiguration existingBlockConfiguration = findApplicablePreconditions(block);
    BlockConfiguration mergedBlockConfiguration =
        BlockConfiguration.merge(existingBlockConfiguration, blockConfiguration);

    return new ConfiguredBlock(mergedBlockConfiguration, block);
  }

  /**
   * Construct a new precondition block to wrap a block.
   * @param innerBlock the block to wrap
   */
  private ConfiguredBlock(final BlockConfiguration blockConfiguration, final Block innerBlock) {
    this.blockConfiguration = blockConfiguration;
    this.innerBlock = innerBlock;
  }

  /**
   * Get the preconditions that apply to the block.
   * @return the preconditions on the block
   */
  BlockConfiguration getPreconditions() {
    return this.blockConfiguration;
  }

  @Override
  public void run() throws Throwable {
    this.innerBlock.run();
  }

  /**
   * Provide the precondition object for this child's block, which are
   * either in the block itself, or provided as a default.
   * @param block the block which may have preconditions
   * @return a non null preconditions object to use
   */
  public static BlockConfiguration findApplicablePreconditions(final Block block) {
    if (block instanceof ConfiguredBlock) {
      return ((ConfiguredBlock) block).getPreconditions();
    } else {
      return BlockConfiguration.defaultConfiguration();
    }
  }
}

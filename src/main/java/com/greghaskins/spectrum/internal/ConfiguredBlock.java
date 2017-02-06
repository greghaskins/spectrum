package com.greghaskins.spectrum.internal;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.model.BlockConfiguration;

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

    BlockConfiguration existingBlockConfiguration = Child.findApplicablePreconditions(block);
    BlockConfiguration mergedBlockConfiguration =
        BlockConfiguration.merge(existingBlockConfiguration, blockConfiguration);

    return new ConfiguredBlock(mergedBlockConfiguration, block);
  }

  /**
   * Mark a block as ignored by surrounding it with the ignore method.
   * @param block the block to ignore
   * @return a PreconditionBlock - preignored
   */
  public static ConfiguredBlock ignore(final Block block) {
    return with(BlockConfiguration.Factory.ignore(), block);
  }

  /**
   * Mark a block as ignored by surrounding it with the ignore method.
   * @param why why is this block being ignored
   * @param block the block to ignore
   * @return a PreconditionBlock - preignored
   */
  public static ConfiguredBlock ignore(final String why, final Block block) {
    return with(BlockConfiguration.Factory.ignore(why), block);
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
    return blockConfiguration;
  }

  @Override
  public void run() throws Throwable {
    innerBlock.run();
  }
}

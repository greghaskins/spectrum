package com.greghaskins.spectrum;

/**
 * A block with pre conditions set on it.
 */
public class PreConditionBlock implements Block {
  private final PreConditions preConditions;
  private final Block innerBlock;

  /**
   * Surround a {@link Block} with the {@link #with(PreConditions, Block)} statement
   * to add preconditions and metadata to it.
   * E.g. <code>with(tags("foo"), () -&gt; {})</code>
   * @param preConditions the precondition object - see the factory methods in {@link PreConditions}
   * @param block the enclosed block
   * @return a PreconditionBlock to use
   */
  public static PreConditionBlock with(final PreConditions preConditions, final Block block) {
    return new PreConditionBlock(preConditions, block);
  }

  /**
   * Construct a new precondition block to wrap a block.
   * @param innerBlock the block to wrap
   */
  private PreConditionBlock(final PreConditions preConditions, final Block innerBlock) {
    this.preConditions = preConditions;
    this.innerBlock = innerBlock;
  }

  /**
   * Get the pre conditions that apply to the block.
   * @return the preconditons on the block
   */
  PreConditions getPreconditions() {
    return preConditions;
  }

  @Override
  public void run() throws Throwable {
    innerBlock.run();
  }
}

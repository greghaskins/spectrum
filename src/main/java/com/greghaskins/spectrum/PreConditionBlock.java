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
   * Mark a block as ignored by surrounding it with the ignore method.
   * @param block the block to ignore
   * @return a PreconditionBlock - preignored
   */
  public static PreConditionBlock ignore(final Block block) {
    return with(PreConditions.Factory.ignore(), block);
  }

  /**
   * Mark a block as ignored by surrounding it with the ignore method.
   * @param why why is this block being ignored
   * @param block the block to ignore
   * @return a PreconditionBlock - preignored
   */
  public static PreConditionBlock ignore(final String why, final Block block) {
    return with(PreConditions.Factory.ignore(why), block);
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

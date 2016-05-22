package com.greghaskins.spectrum;

import com.greghaskins.spectrum.Spectrum.Block;

class IdempotentBlock implements Block {

  private final Block block;
  private Block result;

  public IdempotentBlock(final Block block) {
    this.block = block;
  }

  @Override
  public void run() throws Throwable {
    if (this.result == null) {
      this.result = runBlockOnce(this.block);
    }
    this.result.run();
  }

  private static Block runBlockOnce(final Block block) {
    try {
      block.run();

      return new EmptyBlock();
    } catch (final Throwable error) {
      return new FailingBlock(error);
    }
  }

  private static class EmptyBlock implements Block {
    @Override
    public void run() {}
  }

}

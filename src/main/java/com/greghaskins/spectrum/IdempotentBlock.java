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

      return alwaysPass();
    } catch (final Throwable error) {
      return alwaysFail(error);
    }
  }

  private static Block alwaysPass() {
    return () -> {
    };
  }

  private static Block alwaysFail(final Throwable error) {
    return () -> {
      throw error;
    };
  }

}

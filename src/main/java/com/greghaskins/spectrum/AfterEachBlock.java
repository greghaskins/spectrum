package com.greghaskins.spectrum;

import com.greghaskins.spectrum.Spectrum.Block;

import java.util.ArrayList;
import java.util.List;

class AfterEachBlock implements Block {

  private final List<Block> blocks;

  public AfterEachBlock() {
    this.blocks = new ArrayList<>();
  }

  @Override
  public void run() throws Throwable {
    runRemainingBlocksInReverseOrder(this.blocks.size() - 1);
  }

  private void runRemainingBlocksInReverseOrder(final int currentIndex) throws Throwable {
    if (currentIndex < 0) {
      return;
    }
    try {
      this.blocks.get(currentIndex).run();
    } finally {
      runRemainingBlocksInReverseOrder(currentIndex - 1);
    }
  }

  public void addBlock(final Block block) {
    this.blocks.add(block);
  }

}

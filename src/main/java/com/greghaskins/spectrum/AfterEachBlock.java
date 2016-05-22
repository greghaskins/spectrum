package com.greghaskins.spectrum;

import com.greghaskins.spectrum.Spectrum.Block;

import java.util.ArrayList;
import java.util.List;

class AfterEachBlock implements Block {

  private final List<Block> blocks;

  public AfterEachBlock() {
    this.blocks = new ArrayList<Block>();
  }

  @Override
  public void run() throws Throwable {
    runAllBlocksInReverseOrder(this.blocks.size() - 1);
  }

  private void runAllBlocksInReverseOrder(final int index) throws Throwable {
    if (index < 0) {
      return;
    }
    try {
      this.blocks.get(index).run();
    } finally {
      runAllBlocksInReverseOrder(index - 1);
    }
  }

  public void addBlock(final Block block) {
    this.blocks.add(block);
  }

}

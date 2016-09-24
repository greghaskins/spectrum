package com.greghaskins.spectrum;

import com.greghaskins.spectrum.Spectrum.Block;

import java.util.Deque;
import java.util.LinkedList;

class CompositeBlock implements Block {

  private final Deque<Block> blocks;

  CompositeBlock() {
    this.blocks = new LinkedList<>();
  }

  @Override
  public void run() throws Throwable {
    for (final Block block : this.blocks) {
      block.run();
    }
  }

  void addBlock(final Block block) {
    this.blocks.add(block);
  }

}

package com.greghaskins.spectrum;

import com.greghaskins.spectrum.Spectrum.Block;

import java.util.ArrayList;
import java.util.List;

class CompositeBlock implements Block {

  private final List<Block> blocks;

  public CompositeBlock(final List<Block> blocks) {
    this.blocks = blocks;
  }

  public CompositeBlock() {
    this(new ArrayList<>());
  }

  @Override
  public void run() throws Throwable {
    for (final Block block : this.blocks) {
      block.run();
    }
  }

  public void addBlock(final Block block) {
    this.blocks.add(block);
  }

}

package com.greghaskins.spectrum.model;

import com.greghaskins.spectrum.Block;

import java.util.Deque;
import java.util.LinkedList;

public final class SetupBlock implements Block {

  private final Deque<Block> blocks;

  public SetupBlock() {
    this.blocks = new LinkedList<>();
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

package com.greghaskins.spectrum;

import com.greghaskins.spectrum.Spectrum.Block;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.util.ArrayList;
import java.util.List;

class AfterEachBlock implements NotifyingBlock {

  private final List<NotifyingBlock> blocks;

  public AfterEachBlock() {
    this.blocks = new ArrayList<>();
  }

  @Override
  public void run(final Description description, final RunNotifier notifier) {
    for (int index = this.blocks.size() - 1; index >= 0; index--) {
      this.blocks.get(index).run(description, notifier);
    }
  }

  public void addBlock(final Block block) {
    addBlock(NotifyingBlock.wrap(block));
  }

  void addBlock(final NotifyingBlock notifyingBlock) {
    this.blocks.add(notifyingBlock);
  }

}

package com.greghaskins.spectrum.internal.configuration;

import com.greghaskins.spectrum.internal.Child;
import com.greghaskins.spectrum.internal.ExecutionSequenceApplier;
import com.greghaskins.spectrum.internal.ExecutionSequencer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Configuration block that randomises the order of tests in their parent.
 */
public class BlockRandomOrder implements BlockConfigurable<BlockTimeout>, ExecutionSequencer {
  private Random random;

  public BlockRandomOrder() {
    this(System.currentTimeMillis());
  }

  public BlockRandomOrder(long seed) {
    random = new Random(seed);
    System.out.println("Random execution order set using seed: " + seed);
  }

  @Override
  public boolean inheritedByChild() {
    return true;
  }

  @Override
  public void applyTo(Child child, TaggingFilterCriteria state) {
    if (child instanceof ExecutionSequenceApplier) {
      ((ExecutionSequenceApplier) child).setSequencer(this);
    }
  }

  @Override
  public BlockConfigurable<BlockTimeout> merge(BlockConfigurable<?> other) {
    // my value supersedes any inherited value

    return this;
  }

  @Override
  public List<Child> sequence(List<Child> originalOrder) {
    List<Child> result = new ArrayList<>(originalOrder);
    Collections.shuffle(result, random);

    return result;
  }
}

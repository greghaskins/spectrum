package com.greghaskins.spectrum.internal;

import java.util.List;

/**
 * Put the children into an execution order.
 */
@FunctionalInterface
public interface ExecutionSequencer {
  ExecutionSequencer DEFAULT = list -> list;

  /**
   * Apply the ordering to the list of children.
   * @param originalOrder order they are stored
   * @return re-ordered
   */
  List<Child> sequence(List<Child> originalOrder);
}

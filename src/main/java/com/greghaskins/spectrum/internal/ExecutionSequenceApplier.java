package com.greghaskins.spectrum.internal;

/**
 * Tagging interface for a {@link Parent} that sequences its children.
 */
public interface ExecutionSequenceApplier {
  /**
   * Attache the sequencing strategy.
   * @param sequencer execution sequencer that orders the children.
   */
  void setSequencer(ExecutionSequencer sequencer);
}

package com.greghaskins.spectrum;

/**
 * A generic code block with a {@link #run()} method to perform any action. Usually defined by a
 * lambda function.
 */
@FunctionalInterface
public interface Block {
  void run() throws Throwable;
}

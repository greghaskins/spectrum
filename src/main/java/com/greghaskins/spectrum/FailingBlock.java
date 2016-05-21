package com.greghaskins.spectrum;

import com.greghaskins.spectrum.Spectrum.Block;

class FailingBlock implements Block {
  private final Throwable exceptionToThrow;

  FailingBlock(final Throwable exceptionToThrow) {
    this.exceptionToThrow = exceptionToThrow;
  }

  @Override
  public void run() throws Throwable {
    throw exceptionToThrow;
  }
}

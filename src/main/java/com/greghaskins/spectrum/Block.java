package com.greghaskins.spectrum;

@FunctionalInterface
interface Block {
  void run() throws Throwable;
}

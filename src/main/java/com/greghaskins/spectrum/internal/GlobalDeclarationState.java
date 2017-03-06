package com.greghaskins.spectrum.internal;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Variable;

import java.util.ArrayDeque;
import java.util.Deque;

public final class GlobalDeclarationState {

  public static final Variable<Deque<Suite>> suiteStack = new Variable<>(ArrayDeque::new);

  private GlobalDeclarationState() {}

  private static Deque<Suite> getSuiteStack() {
    return GlobalDeclarationState.suiteStack.get();
  }

  public static Suite getCurrentSuiteBeingDeclared() {
    return getSuiteStack().peek();
  }

  public static int getCurrentDepth() {
    return getSuiteStack().size();
  }

  public static void beginDefinition(final Suite suite, final Block definitionBlock) {
    getSuiteStack().push(suite);

    try {
      definitionBlock.run();
    } catch (final Throwable error) {
      suite.removeAllChildren();
      Spectrum.it("encountered an error", () -> {
        throw error;
      });
    }
    getSuiteStack().pop();
  }

}

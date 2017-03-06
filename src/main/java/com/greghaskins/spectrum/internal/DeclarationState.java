package com.greghaskins.spectrum.internal;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.Spectrum;

import java.util.ArrayDeque;
import java.util.Deque;

public final class DeclarationState {

  private static final ThreadLocal<DeclarationState> instance =
      ThreadLocal.withInitial(DeclarationState::new);

  public static DeclarationState instance() {
    return instance.get();
  }

  private final Deque<Suite> suiteStack = new ArrayDeque<>();

  private DeclarationState() {}

  public Suite getCurrentSuiteBeingDeclared() {
    return suiteStack.peek();
  }

  public int getCurrentDepth() {
    return suiteStack.size();
  }

  public void beginDeclaration(final Suite suite, final Block definitionBlock) {
    suiteStack.push(suite);

    try {
      definitionBlock.run();
    } catch (final Throwable error) {
      suite.removeAllChildren();
      Spectrum.it("encountered an error", () -> {
        throw error;
      });
    }
    suiteStack.pop();
  }

}

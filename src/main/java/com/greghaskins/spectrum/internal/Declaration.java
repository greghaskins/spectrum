package com.greghaskins.spectrum.internal;

import com.greghaskins.spectrum.Block;

import java.util.function.Function;

/**
 * Helper functions used by {@link com.greghaskins.spectrum.dsl.specification.Specification} and similar
 * DSL-specific classes to build specs.
 */
public interface Declaration {

  /**
   * Declare a general purpose composite test - this is a suite that runs as an atomic test in
   * itself. This is the internal implementation for
   * {@link com.greghaskins.spectrum.dsl.gherkin.Gherkin#scenario(String, Block)}.
   *
   * @param context Description of the context for this composite
   * @param block  {@link Block} with one or more calls to
   *                {@link com.greghaskins.spectrum.dsl.specification.Specification#it(String, Block) it}
   *                or its equivalent, that define each expected behavior
   */
  static void addComposite(final String context, final Block block) {
    addSuiteInternal(parent -> parent.addCompositeSuite(context), block);
  }

  /**
   * Common implementation of adding a suite to the current suite under declaration.
   *
   * @param suiteCreator specialisation for adding the right sort of suite
   * @param block {@link Block} with one or more calls to
   *                {@link com.greghaskins.spectrum.dsl.specification.Specification#it(String, Block) it}
   *                or its equivalent, that define each expected behavior
   */
  static void addSuiteInternal(final Function<Suite, Suite> suiteCreator, final Block block) {
    final Suite suite = suiteCreator.apply(DeclarationState.instance()
        .getCurrentSuiteBeingDeclared());
    suite.applyPreconditions(block);
    DeclarationState.instance().beginDeclaration(suite, block);
  }
}

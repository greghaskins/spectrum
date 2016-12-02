package com.greghaskins.spectrum;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.fdescribe;
import static com.greghaskins.spectrum.Spectrum.xdescribe;

/**
 * A translation from {@link Spectrum#describe(String, Block)} to the Ginkgo syntax.
 * Ginkgo has a <code>Context</code> keyword that can be mapped to <code>describe</code>
 * in Spectrum terms. Ginkgo is here - https://onsi.github.io/ginkgo/
 */
public class GinkgoSyntax {
  /**
   * Define a test context.
   * @param context the description of the context
   * @param block the block to execute
   */
  public static void context(final String context, final Block block) {
    describe(context, block);
  }

  /**
   * Define a focused test context - see {@link Spectrum#fdescribe(String, Block)}.
   * @param context the description of the context
   * @param block the block to execute
   */
  public static void fcontext(final String context, final Block block) {
    fdescribe(context, block);
  }

  /**
   * Define an ignored test context - see {@link Spectrum#xdescribe(String, Block)}.
   * @param context the description of the context
   * @param block the block to execute
   */
  public static void xcontext(final String context, final Block block) {
    xdescribe(context, block);
  }

}

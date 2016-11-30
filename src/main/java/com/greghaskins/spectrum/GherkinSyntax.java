package com.greghaskins.spectrum;

import static com.greghaskins.spectrum.Spectrum.compositeSpec;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;

/**
 * A translation from Spectrum describe/it to Gherkin-like Feature/Scenario/Given/When/Then syntax
 * Note - any beforeEach and afterEach within a Scenario will still be executed between
 * given/when/then steps which may not make sense in many situations.
 */
public interface GherkinSyntax {
  /**
   * Describes a feature of the system. A feature may have many scenarios.
   *
   * @param featureName name of feature
   * @param block the contents of the feature
   *
   * @see #scenario
   */
  static void feature(final String featureName, final Block block) {
    describe("Feature: " + featureName, block);
  }

  /**
   * Describes a scenario of the system. These can be at root level, though scenarios are best
   * grouped inside {@link #feature} declarations.
   *
   * @param scenarioName name of scenario
   * @param block the contents of the scenario - given/when/then steps
   *
   * @see #feature
   * @see #given
   * @see #when
   * @see #then
   */
  static void scenario(final String scenarioName, final Block block) {
    compositeSpec("Scenario: " + scenarioName, block);
  }

  /**
   * Define a precondition step with a Gherkin-like {@code given} block. Must be used inside a
   * {@link #scenario}.
   *
   * @param behavior the behavior to associate with the precondition
   * @param block how to execute that precondition
   *
   * @see #when
   * @see #then
   */
  static void given(final String behavior, final Block block) {
    it("Given " + behavior, block);
  }

  /**
   * Define the action performed by the system under test using a Gherkin-like {@code when } block.
   * Must be used inside a {@link #scenario}.
   *
   * @param behavior the behavior to associate with the manipulation of the system under test
   * @param block how to execute that behavior
   *
   * @see #given
   * @see #then
   */
  static void when(final String behavior, final Block block) {
    it("When " + behavior, block);
  }

  /**
   * Define a postcondition step with a Gherkin-like {@code then} block. Must be used inside a
   * {@link #scenario}.
   *
   * @param behavior the behavior to associate with the postcondition
   * @param block how to execute that postcondition
   *
   * @see #given
   * @see #when
   */
  static void then(final String behavior, final Block block) {
    it("Then " + behavior, block);
  }

  /**
   * Syntactic sugar for an additional {@link #given} or {@link then} step. Must be used inside a
   * {@link #scenario}.
   *
   * @param behavior what we would like to describe as an and
   * @param block how to achieve the and block
   *
   * @see #given
   * @see #when
   * @see #then
   */
  static void and(final String behavior, final Block block) {
    it("And " + behavior, block);
  }
}

package com.greghaskins.spectrum.dsl.gherkin;

import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.ParameterizedBlock;
import com.greghaskins.spectrum.ParameterizedBlock.EightArgBlock;
import com.greghaskins.spectrum.ParameterizedBlock.FiveArgBlock;
import com.greghaskins.spectrum.ParameterizedBlock.FourArgBlock;
import com.greghaskins.spectrum.ParameterizedBlock.OneArgBlock;
import com.greghaskins.spectrum.ParameterizedBlock.SevenArgBlock;
import com.greghaskins.spectrum.ParameterizedBlock.SixArgBlock;
import com.greghaskins.spectrum.ParameterizedBlock.ThreeArgBlock;
import com.greghaskins.spectrum.ParameterizedBlock.TwoArgBlock;
import com.greghaskins.spectrum.internal.DeclarationState;
import com.greghaskins.spectrum.internal.Suite;

import java.util.Arrays;

/**
 * A translation from Spectrum describe/it to Gherkin-like Feature/Scenario/Given/When/Then syntax
 * Note - any beforeEach and afterEach within a Scenario will still be executed between
 * given/when/then steps which may not make sense in many situations.
 */
public interface Gherkin {

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
    final Suite suite = DeclarationState.instance().getCurrentSuiteBeingDeclared()
        .addCompositeSuite("Scenario: " + scenarioName);
    DeclarationState.instance().beginDeclaration(suite, block);
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
   * Syntactic sugar for an additional {@link #given} or {@link #then} step. Must be used inside a
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

  /**
   * Scenario outline - composed of examples under a shared name. Example:
   * 
   * <pre>
   * <code>
   * scenarioOutline("Cuke eating - Gherkin style",
        (start, eat, left) -&gt; {
  
         Variable&lt;CukeEater&gt; me = new Variable&lt;&gt;();
  
         given("there are " + start + " cucumbers", () -&gt; {
            me.set(new CukeEater(start));
         });
  
         when("I eat " + eat + " cucumbers", () -&gt; {
            me.get().eatCucumbers(eat);
         });
  
         then("I should have " + left + " cucumbers", () -&gt; {
            assertThat(me.get().remainingCucumbers(), is(left));
         });
      },
  
      withExamples(
         example(12, 5, 7),
         example(20, 5, 15))
      );
   * </code>
   * </pre>
   * 
   * @param name name of scenario outline
   * @param block a {@link ParameterizedBlock} to execute that consumes the parameters from the
   *        examples
   * @param examples the examples to run through, built using
   *        {@link Gherkin#withExamples(TableRow[])}
   * @param <T> the type parameter, best derived implicitly from the examples
   */
  static <T extends ParameterizedBlock> void scenarioOutline(final String name, final T block,
      final Examples<T> examples) {

    describe("Scenario outline: " + name, () -> {
      describe("Examples:", () -> {
        examples.rows().forEach(example -> {
          describe(example.toString(), () -> example.runDeclaration(block));
        });
      });
    });
  }

  /**
   * Construct an Examples table for {@link scenarioOutline}. Used this method to compose individual
   * rows created with {@link #example} type methods into a type-implicit container. You should try
   * to lay out your examples like a data table as that's what they essentially are. Better than
   * just providing some primitives in an example block would be to provide some objects with fields
   * that represent the input parameters more strongly. However, this pattern allows you to create
   * ad-hoc tuples of type-consistent columns with rows of object values.
   * 
   * @param rows example cases - use the 1-8 argument versions of {@link #example(Object)}
   * @param <T> the resulting number-of-arguments type
   * @return a new stream of examples for parameterized tests to use
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  static <T> Examples<T> withExamples(TableRow<T>... rows) {
    return new Examples<>(Arrays.asList(rows));
  }

  /**
   * Create a row for a one-column Examples table.
   * 
   * @param arg the argument
   * @param <T> the type of argument
   * @return single value {@code TableRow}
   */
  static <T> TableRow<OneArgBlock<T>> example(T arg) {
    return new TableRow<>(block -> block.run(arg), arg);
  }

  /**
   * Create a row for a two-column Examples table.
   * 
   * @param arg0 argument 0
   * @param arg1 argument 1
   * @param <T0> the type of argument 0
   * @param <T1> the type of argument 1
   * @return two value {@code TableRow}
   */
  static <T0, T1> TableRow<TwoArgBlock<T0, T1>> example(T0 arg0, T1 arg1) {
    return new TableRow<>(block -> block.run(arg0, arg1), arg0, arg1);
  }

  /**
   * Create a row for a three-column Examples table.
   * 
   * @param arg0 argument 0
   * @param arg1 argument 1
   * @param arg2 argument 2
   * @param <T0> the type of argument 0
   * @param <T1> the type of argument 1
   * @param <T2> the type of argument 2
   * @return three value {@code TableRow}
   */
  static <T0, T1, T2> TableRow<ThreeArgBlock<T0, T1, T2>> example(T0 arg0, T1 arg1, T2 arg2) {
    return new TableRow<>(block -> block.run(arg0, arg1, arg2), arg0, arg1, arg2);
  }

  /**
   * Create a row for a four-column Examples table.
   * 
   * @param arg0 argument 0
   * @param arg1 argument 1
   * @param arg2 argument 2
   * @param arg3 argument 3
   * @param <T0> the type of argument 0
   * @param <T1> the type of argument 1
   * @param <T2> the type of argument 2
   * @param <T3> the type of argument 3
   * @return four value {@code TableRow}
   */
  static <T0, T1, T2, T3> TableRow<FourArgBlock<T0, T1, T2, T3>> example(T0 arg0, T1 arg1, T2 arg2,
      T3 arg3) {
    return new TableRow<>(block -> block.run(arg0, arg1, arg2, arg3), arg0, arg1, arg2, arg3);
  }

  /**
   * Create a row for a five-column Examples table.
   * 
   * @param arg0 argument 0
   * @param arg1 argument 1
   * @param arg2 argument 2
   * @param arg3 argument 3
   * @param arg4 argument 4
   * @param <T0> the type of argument 0
   * @param <T1> the type of argument 1
   * @param <T2> the type of argument 2
   * @param <T3> the type of argument 3
   * @param <T4> the type of argument 4
   * @return five value {@code TableRow}
   */
  static <T0, T1, T2, T3, T4> TableRow<FiveArgBlock<T0, T1, T2, T3, T4>> example(T0 arg0, T1 arg1,
      T2 arg2, T3 arg3, T4 arg4) {
    return new TableRow<>(block -> block.run(arg0, arg1, arg2, arg3, arg4), arg0, arg1, arg2, arg3,
        arg4);
  }

  /**
   * Create a row for a six-column Examples table.
   * 
   * @param arg0 argument 0
   * @param arg1 argument 1
   * @param arg2 argument 2
   * @param arg3 argument 3
   * @param arg4 argument 4
   * @param arg5 argument 5
   * @param <T0> the type of argument 0
   * @param <T1> the type of argument 1
   * @param <T2> the type of argument 2
   * @param <T3> the type of argument 3
   * @param <T4> the type of argument 4
   * @param <T5> the type of argument 5
   * @return six value {@code TableRow}
   */
  static <T0, T1, T2, T3, T4, T5> TableRow<SixArgBlock<T0, T1, T2, T3, T4, T5>> example(T0 arg0,
      T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5) {
    return new TableRow<>(block -> block.run(arg0, arg1, arg2, arg3, arg4, arg5), arg0, arg1, arg2,
        arg3, arg4, arg5);
  }

  /**
   * Create a row for a seven-column Examples table.
   * 
   * @param arg0 argument 0
   * @param arg1 argument 1
   * @param arg2 argument 2
   * @param arg3 argument 3
   * @param arg4 argument 4
   * @param arg5 argument 5
   * @param arg6 argument 6
   * @param <T0> the type of argument 0
   * @param <T1> the type of argument 1
   * @param <T2> the type of argument 2
   * @param <T3> the type of argument 3
   * @param <T4> the type of argument 4
   * @param <T5> the type of argument 5
   * @param <T6> the type of argument 6
   * @return seven value {@code TableRow}
   */
  static <T0, T1, T2, T3, T4, T5, T6> TableRow<SevenArgBlock<T0, T1, T2, T3, T4, T5, T6>> example(
      T0 arg0, T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6) {
    return new TableRow<>(block -> block.run(arg0, arg1, arg2, arg3, arg4, arg5, arg6), arg0, arg1,
        arg2, arg3, arg4, arg5, arg6);
  }

  /**
   * Create a row for an eight-column Examples table.
   * 
   * @param arg0 argument 0
   * @param arg1 argument 1
   * @param arg2 argument 2
   * @param arg3 argument 3
   * @param arg4 argument 4
   * @param arg5 argument 5
   * @param arg6 argument 6
   * @param arg7 argument 7
   * @param <T0> the type of argument 0
   * @param <T1> the type of argument 1
   * @param <T2> the type of argument 2
   * @param <T3> the type of argument 3
   * @param <T4> the type of argument 4
   * @param <T5> the type of argument 5
   * @param <T6> the type of argument 6
   * @param <T7> the type of argument 7
   * @return eight value {@code TableRow}
   */
  static <T0, T1, T2, T3, T4, T5, T6, T7> TableRow<EightArgBlock<T0, T1, T2, T3, T4, T5, T6, T7>> example(
      T0 arg0, T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7) {
    return new TableRow<>(block -> block.run(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7), arg0,
        arg1, arg2, arg3, arg4, arg5, arg6, arg7);
  }
}

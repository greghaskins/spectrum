package com.greghaskins.spectrum;

import com.greghaskins.spectrum.internal.parameterized.Example;
import com.greghaskins.spectrum.internal.parameterized.ParameterizedDefinitionBlock;
import com.greghaskins.spectrum.internal.parameterized.ParameterizedDefinitionBlock.EightArgBlock;
import com.greghaskins.spectrum.internal.parameterized.ParameterizedDefinitionBlock.FiveArgBlock;
import com.greghaskins.spectrum.internal.parameterized.ParameterizedDefinitionBlock.FourArgBlock;
import com.greghaskins.spectrum.internal.parameterized.ParameterizedDefinitionBlock.OneArgBlock;
import com.greghaskins.spectrum.internal.parameterized.ParameterizedDefinitionBlock.SevenArgBlock;
import com.greghaskins.spectrum.internal.parameterized.ParameterizedDefinitionBlock.SixArgBlock;
import com.greghaskins.spectrum.internal.parameterized.ParameterizedDefinitionBlock.ThreeArgBlock;
import com.greghaskins.spectrum.internal.parameterized.ParameterizedDefinitionBlock.TwoArgBlock;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Common syntax for parameterization.
 */
public interface ParamaterizedSyntax {
  /**
   * Describes a parameterized specification suite. E.g.
   * <pre><code>
   *         describe("Cuke eating without gherkin",
               (start, eat, left) -&gt; {
                 it("eats cukes properly", () -&gt; {
                   CukeEater me = new CukeEater(start);
                   me.eatCucumbers(eat);
                   assertThat(me.remainingCucumbers(), is(left));
                 });
               },
  
               withExamples(
                 example(12, 5, 7),
                 example(20, 5, 15))
               );
   * </code></pre>
   * @param name name of the parameterized test
   * @param block a {@link ParameterizedDefinitionBlock} to execute that consumes the parameters
   *              from the examples
   * @param examples the examples to run through, built using
   *                {@link ParamaterizedSyntax#withExamples(Example[])}
   * @param <T> the type parameter, best derived implicitly from the examples
   */
  static <T> void describeParameterized(final String name, final T block,
      final Stream<Example<T>> examples) {
    Spectrum.describe(name, () -> {
      Spectrum.context("Examples:", () -> {
        examples.forEach(example -> {
          Spectrum.describe(example.toString(), () -> example.runDeclaration(block));
        });
      });
    });
  }


  /**
   * Construct examples. Used to convert from individual objects created with
   * {@link #example(Object)} type methods into a type-implicit stream. You should try to lay out
   * your examples like a data table as that's what they essentially are. Better than just
   * providing some primitives in an example block would be to provide some objects with fields
   * that represent the input parameters more strongly. However, this pattern allows you to create
   * ad-hoc tuples of type-consistent columns with rows of object values.
   * @param examples example objects - use the 1-8 parameter versions of {@link #example(Object)}
   * @param <T> the resulting number-of-arguments type
   * @return a new stream of examples for parameterized tests to use
   */
  @SafeVarargs
  static <T> Stream<Example<T>> withExamples(Example<T>... examples) {
    return Arrays.stream(examples);
  }

  /**
   * Single value example.
   * @param t1 the parameter
   * @param <T1> the type of param 1
   * @return single value example
   */
  static <T1> Example<OneArgBlock<T1>> example(T1 t1) {
    return new Example<>(block -> block.run(t1), t1);
  }

  /**
   * Two value example.
   * @param t1 parameter 1
   * @param t2 parameter 2
   * @param <T1> the type of param 1
   * @param <T2> the type of param 2
   * @return two value example
   */
  static <T1, T2> Example<TwoArgBlock<T1, T2>> example(T1 t1, T2 t2) {
    return new Example<>(block -> block.run(t1, t2), t1, t2);
  }

  /**
   * Three value example.
   * @param t1 parameter 1
   * @param t2 parameter 2
   * @param t3 parameter 3
   * @param <T1> the type of param 1
   * @param <T2> the type of param 2
   * @param <T3> the type of param 3
   * @return three value example
   */
  static <T1, T2, T3> Example<ThreeArgBlock<T1, T2, T3>> example(T1 t1, T2 t2, T3 t3) {
    return new Example<>(block -> block.run(t1, t2, t3), t1, t2, t3);
  }

  /**
   * Four value example.
   * @param t1 parameter 1
   * @param t2 parameter 2
   * @param t3 parameter 3
   * @param t4 parameter 34
   * @param <T1> the type of param 1
   * @param <T2> the type of param 2
   * @param <T3> the type of param 3
   * @param <T4> the type of param 4
   * @return four value example
   */
  static <T1, T2, T3, T4> Example<FourArgBlock<T1, T2, T3, T4>> example(T1 t1, T2 t2, T3 t3,
      T4 t4) {
    return new Example<>(block -> block.run(t1, t2, t3, t4), t1, t2, t3, t4);
  }

  /**
   * Five value example.
   * @param t1 parameter 1
   * @param t2 parameter 2
   * @param t3 parameter 3
   * @param t4 parameter 4
   * @param t5 parameter 5
   * @param <T1> the type of param 1
   * @param <T2> the type of param 2
   * @param <T3> the type of param 3
   * @param <T4> the type of param 4
   * @param <T5> the type of param 5
   * @return five value example
   */
  static <T1, T2, T3, T4, T5> Example<FiveArgBlock<T1, T2, T3, T4, T5>> example(T1 t1, T2 t2,
      T3 t3, T4 t4, T5 t5) {
    return new Example<>(block -> block.run(t1, t2, t3, t4, t5), t1, t2, t3, t4, t5);
  }

  /**
   * Six value example.
   * @param t1 parameter 1
   * @param t2 parameter 2
   * @param t3 parameter 3
   * @param t4 parameter 4
   * @param t5 parameter 5
   * @param t6 parameter 6
   * @param <T1> the type of param 1
   * @param <T2> the type of param 2
   * @param <T3> the type of param 3
   * @param <T4> the type of param 4
   * @param <T5> the type of param 5
   * @param <T6> the type of param 6
   * @return six value example
   */
  static <T1, T2, T3, T4, T5, T6> Example<SixArgBlock<T1, T2, T3, T4, T5, T6>> example(T1 t1, T2 t2,
      T3 t3, T4 t4, T5 t5, T6 t6) {
    return new Example<>(block -> block.run(t1, t2, t3, t4, t5, t6), t1, t2, t3, t4, t5, t6);
  }

  /**
   * Seven value example.
   * @param t1 parameter 1
   * @param t2 parameter 2
   * @param t3 parameter 3
   * @param t4 parameter 4
   * @param t5 parameter 5
   * @param t6 parameter 6
   * @param t7 parameter 7
   * @param <T1> the type of param 1
   * @param <T2> the type of param 2
   * @param <T3> the type of param 3
   * @param <T4> the type of param 4
   * @param <T5> the type of param 5
   * @param <T6> the type of param 6
   * @param <T7> the type of param 7
   * @return seven value example
   */
  static <T1, T2, T3, T4, T5, T6, T7> Example<SevenArgBlock<T1, T2, T3, T4, T5, T6, T7>> example(
      T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7) {
    return new Example<>(block -> block.run(t1, t2, t3, t4, t5, t6, t7),
        t1, t2, t3, t4, t5, t6, t7);
  }

  /**
   * Eight value example.
   * @param t1 parameter 1
   * @param t2 parameter 2
   * @param t3 parameter 3
   * @param t4 parameter 4
   * @param t5 parameter 5
   * @param t6 parameter 6
   * @param t7 parameter 7
   * @param t8 parameter 8
   * @param <T1> the type of param 1
   * @param <T2> the type of param 2
   * @param <T3> the type of param 3
   * @param <T4> the type of param 4
   * @param <T5> the type of param 5
   * @param <T6> the type of param 6
   * @param <T7> the type of param 7
   * @param <T8> the type of param 8
   * @return eight value example
   */
  static <T1, T2, T3, T4, T5, T6, T7, T8> Example<EightArgBlock<T1, T2, T3, T4, T5, T6, T7, T8>> example(
      T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8) {
    return new Example<>(block -> block.run(t1, t2, t3, t4, t5, t6, t7, t8),
        t1, t2, t3, t4, t5, t6, t7, t8);
  }
}

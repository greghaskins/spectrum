package com.greghaskins.spectrum;

import com.greghaskins.spectrum.internal.parameterized.Example;
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
   * Construct examples. Used to convert from individual objects created with
   * {@link #example(Object)} type methods into a type-implicit stream. You should try to lay out
   * your examples like a data table as that's what they essentially are. Better than just
   * providing some primitives in an example block would be to provide some objects with fields
   * that represent the input parameters more strongly. However, this pattern allows you to create
   * ad-hoc tuples of type-consistent columns with rows of object values.
   * @param examples example objects - use the 1-8 argument versions of {@link #example(Object)}
   * @param <T> the resulting number-of-arguments type
   * @return a new stream of examples for parameterized tests to use
   */
  @SafeVarargs
  static <T> Stream<Example<T>> withExamples(Example<T>... examples) {
    return Arrays.stream(examples);
  }

  /**
   * Single value example.
   * @param arg the parameter
   * @param <T> the type of argumenteter
   * @return single value example
   */
  static <T> Example<OneArgBlock<T>> example(T arg) {
    return new Example<>(block -> block.run(arg), arg);
  }

  /**
   * Two value example.
   * @param arg0 argument 0
   * @param arg1 argument 1
   * @param <T0> the type of argument 0
   * @param <T1> the type of argument 1
   * @return two value example
   */
  static <T0, T1> Example<TwoArgBlock<T0, T1>> example(T0 arg0, T1 arg1) {
    return new Example<>(block -> block.run(arg0, arg1), arg0, arg1);
  }

  /**
   * Three value example.
   * @param arg0 argument 0
   * @param arg1 argument 1
   * @param arg2 argument 2
   * @param <T0> the type of argument 0
   * @param <T1> the type of argument 1
   * @param <T2> the type of argument 2
   * @return three value example
   */
  static <T0, T1, T2> Example<ThreeArgBlock<T0, T1, T2>> example(T0 arg0, T1 arg1, T2 arg2) {
    return new Example<>(block -> block.run(arg0, arg1, arg2), arg0, arg1, arg2);
  }

  /**
   * Four value example.
   * @param arg0 argument 0
   * @param arg1 argument 1
   * @param arg2 argument 2
   * @param arg3 argument 3
   * @param <T0> the type of argument 0
   * @param <T1> the type of argument 1
   * @param <T2> the type of argument 2
   * @param <T3> the type of argument 3
   * @return four value example
   */
  static <T0, T1, T2, T3> Example<FourArgBlock<T0, T1, T2, T3>> example(T0 arg0, T1 arg1, T2 arg2,
      T3 arg3) {
    return new Example<>(block -> block.run(arg0, arg1, arg2, arg3), arg0, arg1, arg2, arg3);
  }

  /**
   * Five value example.
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
   * @return five value example
   */
  static <T0, T1, T2, T3, T4> Example<FiveArgBlock<T0, T1, T2, T3, T4>> example(T0 arg0, T1 arg1,
      T2 arg2, T3 arg3, T4 arg4) {
    return new Example<>(block -> block.run(arg0, arg1, arg2, arg3, arg4), arg0, arg1, arg2, arg3, arg4);
  }

  /**
   * Six value example.
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
   * @return six value example
   */
  static <T0, T1, T2, T3, T4, T5> Example<SixArgBlock<T0, T1, T2, T3, T4, T5>> example(T0 arg0, T1 arg1,
      T2 arg2, T3 arg3, T4 arg4, T5 arg5) {
    return new Example<>(block -> block.run(arg0, arg1, arg2, arg3, arg4, arg5), arg0, arg1, arg2, arg3, arg4, arg5);
  }

  /**
   * Seven value example.
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
   * @return seven value example
   */
  static <T0, T1, T2, T3, T4, T5, T6> Example<SevenArgBlock<T0, T1, T2, T3, T4, T5, T6>> example(
      T0 arg0, T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6) {
    return new Example<>(block -> block.run(arg0, arg1, arg2, arg3, arg4, arg5, arg6),
        arg0, arg1, arg2, arg3, arg4, arg5, arg6);
  }

  /**
   * Eight value example.
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
   * @return eight value example
   */
  static <T0, T1, T2, T3, T4, T5, T6, T7> Example<EightArgBlock<T0, T1, T2, T3, T4, T5, T6, T7>> example(
      T0 arg0, T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7) {
    return new Example<>(block -> block.run(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7),
        arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
  }
}

package com.greghaskins.spectrum.internal.parameterized;

import com.greghaskins.spectrum.ParameterizedSyntax;

/**
 * The common interface of a parameterized definition block. This provides type safety
 * to the {@link ParameterizedSyntax} which takes argument
 * blocks as an input. It looks similar to Java 8's Consumer.
 */
public interface ParameterizedDefinitionBlock<T> {
  @FunctionalInterface
  interface OneArgBlock<T> extends ParameterizedDefinitionBlock<OneArgBlock<T>> {
    void run(T arg0);
  }

  @FunctionalInterface
  interface TwoArgBlock<T0, T1> extends ParameterizedDefinitionBlock<TwoArgBlock<T0, T1>> {
    void run(T0 arg0, T1 arg1);
  }

  @FunctionalInterface
  interface ThreeArgBlock<T0, T1, T2>
      extends ParameterizedDefinitionBlock<ThreeArgBlock<T0, T1, T2>> {
    void run(T0 arg0, T1 arg1, T2 arg2);
  }

  @FunctionalInterface
  interface FourArgBlock<T0, T1, T2, T3>
      extends ParameterizedDefinitionBlock<FourArgBlock<T0, T1, T2, T3>> {
    void run(T0 arg0, T1 arg1, T2 arg2, T3 arg3);
  }

  @FunctionalInterface
  interface FiveArgBlock<T0, T1, T2, T3, T4>
      extends ParameterizedDefinitionBlock<FiveArgBlock<T0, T1, T2, T3, T4>> {
    void run(T0 arg0, T1 arg1, T2 arg2, T3 arg3, T4 arg4);
  }

  @FunctionalInterface
  interface SixArgBlock<T0, T1, T2, T3, T4, T5>
      extends ParameterizedDefinitionBlock<SixArgBlock<T0, T1, T2, T3, T4, T5>> {
    void run(T0 arg0, T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5);
  }

  @FunctionalInterface
  interface SevenArgBlock<T0, T1, T2, T3, T4, T5, T6>
      extends ParameterizedDefinitionBlock<SevenArgBlock<T0, T1, T2, T3, T4, T5, T6>> {
    void run(T0 arg0, T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6);
  }

  @FunctionalInterface
  interface EightArgBlock<T0, T1, T2, T3, T4, T5, T6, T7>
      extends ParameterizedDefinitionBlock<EightArgBlock<T0, T1, T2, T3, T4, T5, T6, T7>> {
    void run(T0 arg0, T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7);
  }

}

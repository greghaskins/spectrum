package com.greghaskins.spectrum.internal.parameterized;

/**
 * The common interface of a parameterized definition block. This provides type safety
 * to the {@link com.greghaskins.spectrum.ParamaterizedSyntax} which takes argument
 * blocks as an input. It looks similar to Java 8's Consumer.
 */
public interface ParameterizedDefinitionBlock<T> {
  @FunctionalInterface
  interface OneArgBlock<T> extends ParameterizedDefinitionBlock<OneArgBlock<T>> {
    void run(T arg0);
  }

  @FunctionalInterface
  interface TwoArgBlock<T1, T2> extends ParameterizedDefinitionBlock<TwoArgBlock<T1, T2>> {
    void run(T1 arg0, T2 arg1);
  }

  @FunctionalInterface
  interface ThreeArgBlock<T1, T2, T3>
      extends ParameterizedDefinitionBlock<ThreeArgBlock<T1, T2, T3>> {
    void run(T1 arg0, T2 arg1, T3 arg2);
  }

  @FunctionalInterface
  interface FourArgBlock<T1, T2, T3, T4>
      extends ParameterizedDefinitionBlock<FourArgBlock<T1, T2, T3, T4>> {
    void run(T1 arg0, T2 arg1, T3 arg2, T4 arg3);
  }

  @FunctionalInterface
  interface FiveArgBlock<T1, T2, T3, T4, T5>
      extends ParameterizedDefinitionBlock<FiveArgBlock<T1, T2, T3, T4, T5>> {
    void run(T1 arg0, T2 arg1, T3 arg2, T4 arg3, T5 arg4);
  }

  @FunctionalInterface
  interface SixArgBlock<T1, T2, T3, T4, T5, T6>
      extends ParameterizedDefinitionBlock<SixArgBlock<T1, T2, T3, T4, T5, T6>> {
    void run(T1 arg0, T2 arg1, T3 arg2, T4 arg3, T5 arg4, T6 arg5);
  }

  @FunctionalInterface
  interface SevenArgBlock<T1, T2, T3, T4, T5, T6, T7>
      extends ParameterizedDefinitionBlock<SevenArgBlock<T1, T2, T3, T4, T5, T6, T7>> {
    void run(T1 arg0, T2 arg1, T3 arg2, T4 arg3, T5 arg4, T6 arg5, T7 arg6);
  }

  @FunctionalInterface
  interface EightArgBlock<T1, T2, T3, T4, T5, T6, T7, T8>
      extends ParameterizedDefinitionBlock<EightArgBlock<T1, T2, T3, T4, T5, T6, T7, T8>> {
    void run(T1 arg0, T2 arg1, T3 arg2, T4 arg3, T5 arg4, T6 arg5, T7 arg6, T8 arg7);
  }

}

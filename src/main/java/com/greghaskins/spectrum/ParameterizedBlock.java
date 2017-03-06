package com.greghaskins.spectrum;

import com.greghaskins.spectrum.dsl.gherkin.GherkinSyntax;

/**
 * The common interface of a parameterized definition block. This provides type safety, for example,
 * to {@link GherkinSyntax#scenarioOutline} which takes argument blocks as an input. It looks
 * similar to Java 8's Consumer.
 */
public interface ParameterizedBlock {

  @FunctionalInterface
  interface OneArgBlock<T> extends ParameterizedBlock {
    void run(T arg0);
  }

  @FunctionalInterface
  interface TwoArgBlock<T0, T1> extends ParameterizedBlock {
    void run(T0 arg0, T1 arg1);
  }

  @FunctionalInterface
  interface ThreeArgBlock<T0, T1, T2> extends ParameterizedBlock {
    void run(T0 arg0, T1 arg1, T2 arg2);
  }

  @FunctionalInterface
  interface FourArgBlock<T0, T1, T2, T3> extends ParameterizedBlock {
    void run(T0 arg0, T1 arg1, T2 arg2, T3 arg3);
  }

  @FunctionalInterface
  interface FiveArgBlock<T0, T1, T2, T3, T4> extends ParameterizedBlock {
    void run(T0 arg0, T1 arg1, T2 arg2, T3 arg3, T4 arg4);
  }

  @FunctionalInterface
  interface SixArgBlock<T0, T1, T2, T3, T4, T5> extends ParameterizedBlock {
    void run(T0 arg0, T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5);
  }

  @FunctionalInterface
  interface SevenArgBlock<T0, T1, T2, T3, T4, T5, T6> extends ParameterizedBlock {
    void run(T0 arg0, T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6);
  }

  @FunctionalInterface
  interface EightArgBlock<T0, T1, T2, T3, T4, T5, T6, T7> extends ParameterizedBlock {
    void run(T0 arg0, T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7);
  }

}

package com.greghaskins.spectrum;

import static com.greghaskins.spectrum.ParamaterizedSyntax.describeParameterized;
import static com.greghaskins.spectrum.ParamaterizedSyntax.example;
import static com.greghaskins.spectrum.ParamaterizedSyntax.withExamples;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.runner.RunWith;

/**
 * Exercises all arg-variants of
 * {@link com.greghaskins.spectrum.internal.parameterized.ParameterizedDefinitionBlock}.
 */
@RunWith(Spectrum.class)
public class ParameterizedVariants {
  {
    describe("parameterized definition block arguments", () -> {
      describeParameterized("one arg", (arg) -> {
        it("can read the argument", () -> {
          assertNotNull(arg);
        });
      },
          withExamples(
              example("one"),
              example("two")));

      describeParameterized("two args", (intArg, stringArg) -> {
        it("arguments are correct", () -> {
          assertEquals(stringArg, Integer.toString(intArg));
        });
      },
          withExamples(
              example(1, "1"),
              example(2, "2")));

      describeParameterized("three args", (intArg, stringArg, doubleArg) -> {
        it("int matches string", () -> {
          assertEquals(stringArg, Integer.toString(intArg));
        });
        it("int matches double", () -> {
          assertEquals((int) intArg, (int) doubleArg.doubleValue());
        });
      },
          withExamples(
              example(1, "1", 1.0d),
              example(2, "2", 2.0d)));

      describeParameterized("four args", (intArg, stringArg, doubleArg, booleanArg) -> {
        it("int matches string", () -> {
          assertEquals(stringArg, Integer.toString(intArg));
        });
        it("int matches double based on boolean", () -> {
          assertThat((int) intArg == (int) doubleArg.doubleValue(), is(booleanArg));
        });
      },
          withExamples(
              example(1, "1", 1.0d, true),
              example(2, "2", 3.0d, false)));

      describeParameterized("five args", (a1, a2, a3, a4, a5) -> {
        it("arguments add up", () -> {
          assertThat("" + a1 + a2 + a3 + a4, is(a5));
        });
      },
          withExamples(
              example(1, "2", 3, "4", "1234"),
              example(2, "3", 4, "5", "2345")));

      describeParameterized("six args", (a1, a2, a3, a4, a5, a6) -> {
        it("arguments add up", () -> {
          assertThat("" + (a1 + a2) + a3 + a4 + a5, is(a6));
        });
      },
          withExamples(
              example(0, 1, "2", 3, "4", "1234"),
              example(1, 1, "3", 4, "5", "2345")));

      describeParameterized("seven args", (a1, a2, a3, a4, a5, a6, a7) -> {
        it("arguments add up", () -> {
          assertThat(a1 + a2 + a3 + a4 + a5 + a6, is(a7));
        });
      },
          withExamples(
              example("A", "B", "C", "D", "E", "F", "ABCDEF")));

      describeParameterized("eight args", (a1, a2, a3, a4, a5, a6, a7, a8) -> {
        it("arguments add up", () -> {
          assertThat(a1 == a2, is(a5));
          assertThat(a3.equals(a4), is(a6));
          assertThat(a1 + a2, is(a7));
          assertThat(a3 + a4, is(a8));
        });
      },
          withExamples(
              example(1, 2, "A", "B", false, false, 3, "AB"),
              example(1, 1, "A", "A", true, true, 2, "AA")));
    });
  }
}

package specs;

import static com.greghaskins.spectrum.Spectrum.beforeEach;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.fdescribe;
import static com.greghaskins.spectrum.Spectrum.fit;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.Spectrum.value;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Spectrum.Value;

import helpers.SpectrumRunner;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
public class FocusedSpecs {
  {

    describe("Focused specs", () -> {

      it("are declared with `fit`", () -> {
        final Result result = SpectrumRunner.run(getSuiteWithFocusedSpecs());
        assertThat(result.getFailureCount(), is(0));
      });

      it("mark siblings as ignored so they don't get forgotten", () -> {
        final Result result = SpectrumRunner.run(getSuiteWithFocusedSpecs());
        assertThat(result.getIgnoreCount(), is(1));
      });

      describe("when nested in a separate suite", () -> {

        it("cause specs in other suites to be ignored", () -> {
          final Result result = SpectrumRunner.run(getSuiteWithNestedFocusedSpecs());
          assertThat(result.getFailureCount(), is(0));
          assertThat(result.getIgnoreCount(), is(1));
        });
      });

    });

    describe("Focused suites", () -> {

      it("are declared with `fdescribe`", () -> {
        final Result result = SpectrumRunner.run(getSuiteWithFocusedSubSuites());
        assertThat(result.getFailureCount(), is(0));
      });

      it("ignores tests that aren't focused", () -> {
        final Result result = SpectrumRunner.run(getSuiteWithFocusedSubSuites());
        assertThat(result.getIgnoreCount(), is(2));
      });

      describe("when nested", () -> {
        it("cause specs in other suites to be ignored", () -> {
          final Result result = SpectrumRunner.run(getSuiteWithNestedFocusedSuites());
          assertThat(result.getFailureCount(), is(0));
          assertThat(result.getIgnoreCount(), is(1));
        });
      });

    });

    describe("Focused specs example", () -> {

      final Value<Result> result = value();

      beforeEach(() -> {
        result.value = SpectrumRunner.run(getFocusedSpecsExample());
      });

      it("has two ignored specs", () -> {
        assertThat(result.value.getIgnoreCount(), is(2));
      });

      it("does not run unfocused specs", () -> {
        assertThat(result.value.getFailureCount(), is(0));
      });

    });

  }

  private static Class<?> getSuiteWithFocusedSpecs() {
    class Suite {
      {

        describe("A spec that", () -> {

          fit("is focused and will run", () -> {
            assertThat(true, is(true));
          });

          it("is not focused and will not run", () -> {
            assertThat(true, is(false));
          });

        });
      }
    }

    return Suite.class;
  }

  private static Class<?> getSuiteWithNestedFocusedSpecs() {
    class Suite {
      {

        it("should not run because it isn't focused", () -> {
          assertThat(true, is(false));
        });

        describe("a nested context", () -> {
          fit("is focused and will run", () -> {
            assertThat(true, is(true));
          });
        });
      }
    }

    return Suite.class;
  }

  private static Class<?> getSuiteWithFocusedSubSuites() {
    class Suite {
      {
        describe("an unfocused suite", () -> {
          it("is ignored", () -> {
            assertThat(true, is(false));
          });
        });

        fdescribe("focused describe", () -> {
          it("will run", () -> {
            assertThat(true, is(true));
          });
          it("will also run", () -> {
            assertThat(true, is(true));
          });
        });

        fdescribe("another focused describe", () -> {
          fit("is focused and will run", () -> {
            assertThat(true, is(true));
          });
          it("is not focused and will not run", () -> {
            assertThat(false, is(true));
          });
        });

      }
    }

    return Suite.class;
  }

  private static Class<?> getSuiteWithNestedFocusedSuites() {
    class Suite {
      {

        describe("an unfocused suite", () -> {
          it("should not run because it isn't focused", () -> {
            assertThat(true, is(false));
          });
        });

        describe("a nested context", () -> {

          fdescribe("with a focused sub-suite", () -> {
            it("is focused and will run", () -> {
              assertThat(true, is(true));
            });
          });
        });

        describe("another nested context", () -> {
          fit("with a focused spec", () -> {
            assertThat(true, is(true));
          });
        });
      }
    }

    return Suite.class;
  }

  private static Class<?> getFocusedSpecsExample() {
    class FocusedSpecsExample {
      {

        describe("Focused specs", () -> {

          fit("is focused and will run", () -> {
            assertThat(true, is(true));
          });

          it("is not focused and will not run", () -> {
            throw new Exception();
          });

          fdescribe("a focused suite", () -> {

            it("will run", () -> {
              assertThat(true, is(true));
            });

            it("all its specs", () -> {
              assertThat(true, is(true));
            });
          });

          fdescribe("another focused suite, with focused and unfocused specs", () -> {

            fit("will run focused specs", () -> {
              assertThat(true, is(true));
            });

            it("ignores unfocused specs", () -> {
              throw new Exception();
            });
          });
        });

      }
    }

    return FocusedSpecsExample.class;
  }

}

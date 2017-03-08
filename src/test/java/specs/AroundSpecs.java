package specs;

import static com.greghaskins.spectrum.dsl.specification.Specification.afterAll;
import static com.greghaskins.spectrum.dsl.specification.Specification.afterEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.aroundAll;
import static com.greghaskins.spectrum.dsl.specification.Specification.aroundEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.beforeAll;
import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.let;
import static com.greghaskins.spectrum.dsl.specification.Specification.xit;
import static matchers.IsFailure.failure;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.SpectrumHelper;

import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@RunWith(Spectrum.class)
public class AroundSpecs {
  {
    describe("the `aroundEach` hook", () -> {

      it("allows arbitrary code to be run before/after each spec", () -> {

        ArrayList<String> steps = new ArrayList<>();
        SpectrumHelper.run(() -> {

          aroundEach(block -> {
            steps.add("A");
            block.run();
            steps.add("C");
          });

          it("first spec", () -> {
            steps.add("B1");
          });

          it("second spec", () -> {
            steps.add("B2");
          });

        });

        assertThat(steps, contains("A", "B1", "C", "A", "B2", "C"));
      });

      it("throws an error when you forget to run the spec", () -> {

        Result result = SpectrumHelper.run(() -> {

          aroundEach(block -> {
          });
          it("first spec", () -> {
          });

        });

        assertThat(result.getFailureCount(), is(1));
        assertThat(result.getFailures().get(0), is(failure("first spec",
            RuntimeException.class, "At least one of the test hooks did not run the test block.")));
      });

      describe("in multiples", () -> {

        it("each subsequent aroundEach nests inside those preceding it", () -> {

          ArrayList<String> steps = new ArrayList<>();
          SpectrumHelper.run(() -> {

            aroundEach(block -> {
              steps.add("pre1");
              block.run();
              steps.add("post1");
            });

            aroundEach(block -> {
              steps.add("pre2");
              block.run();
              steps.add("post2");
            });

            it("first spec", () -> {
              steps.add("spec");
            });

          });

          assertThat(steps, contains("pre1", "pre2", "spec", "post2", "post1"));

        });

        it("fail if any aroundEach forgets to call the block", () -> {
          Result result = SpectrumHelper.run(() -> {

            aroundEach(block -> {
              block.run();
            });

            aroundEach(block -> {
            });

            it("a spec", () -> {
            });

          });

          assertThat(result.getFailureCount(), is(1));
        });

      });



      describe("that throw errors", () -> {

        Stream.of(new RuntimeException("boom"), new Exception("boom"), new Error("boom"))
            .forEach(exception -> {
              describe(exception.getClass().getSimpleName(), () -> {

                Supplier<Result> result = let(() -> SpectrumHelper.run(() -> {

                  aroundEach(block -> {
                    throw exception;
                  });

                  it("spec1", () -> {
                  });
                  it("spec2", () -> {
                  });

                }));

                it("mark all specs in that context as failing", () -> {
                  assertThat(result.get().getFailureCount(), is(2));
                  List<Failure> failures = result.get().getFailures();
                  assertThat(failures.get(0).getMessage(), containsString("boom"));
                  assertThat(failures.get(1).getMessage(), containsString("boom"));
                });

              });

            });

      });


    });


    describe("the `aroundAll` hook", () -> {

      it("allows arbitrary code to run before/after all specs in a suite", () -> {

        ArrayList<String> steps = new ArrayList<>();
        SpectrumHelper.run(() -> {

          aroundAll(block -> {
            steps.add("pre");
            block.run();
            steps.add("post");
          });

          it("spec1", () -> {
            steps.add("spec1");
          });
          it("spec2", () -> {
            steps.add("spec2");
          });
        });

        assertThat(steps, contains("pre", "spec1", "spec2", "post"));

      });

      it("throws an error when you forget to run the block", () -> {

        Result result = SpectrumHelper.run(() -> {

          aroundAll(block -> {
          });
          it("first spec", () -> {
          });

        });

        assertThat(result.getFailureCount(), is(1));
        Failure failure = result.getFailures().get(0);
        assertThat(failure.getMessage(),
            is("At least one of the test hooks did not run the test block."));
      });

      describe("in multiples", () -> {

        it("each subsequent aroundAll nests inside those preceding", () -> {
          ArrayList<String> steps = new ArrayList<>();
          SpectrumHelper.run(() -> {

            aroundAll(block -> {
              steps.add("pre1");
              block.run();
              steps.add("post1");
            });

            aroundAll(block -> {
              steps.add("pre2");
              block.run();
              steps.add("post2");
            });

            it("first spec", () -> {
              steps.add("spec");
            });

          });

          assertThat(steps, contains("pre1", "pre2", "spec", "post2", "post1"));
        });

        it("fail if any aroundAll forgets to call the block", () -> {
          Result result = SpectrumHelper.run(() -> {

            aroundAll(block -> {
              block.run();
            });

            aroundAll(block -> {
            });

            aroundAll(block -> {
              block.run();
            });

            it("a spec", () -> {
            });

          });

          assertThat(result.getFailureCount(), is(1));
        });

      });

      describe("a suite with only ignored specs", () -> {

        it("should not run aroundAll", () -> {
          ArrayList<String> steps = new ArrayList<>();
          SpectrumHelper.run(() -> {

            aroundAll(block -> {
              steps.add("aroundAll");
              block.run();
            });

            xit("foo", () -> {
            });

          });

          assertThat(steps, is(empty()));
        });

      });

      describe("that throws an error itself", () -> {

        describe("before running the suite", () -> {

          Supplier<ArrayList<String>> steps = let(() -> new ArrayList<>());
          Supplier<Result> result = let(() -> SpectrumHelper.run(() -> {

            aroundAll(block -> {
              throw new Exception("boom");
            });

            it("spec1", () -> {
              steps.get().add("spec1");
            });
            it("spec2", () -> {
              steps.get().add("spec2");
            });

          }));

          it("throws a failure with the appropriate message", () -> {
            assertThat(result.get().getFailureCount(), is(greaterThan(0)));
            List<Failure> failures = result.get().getFailures();
            assertThat(failures.get(0).getMessage(), containsString("boom"));
          });

          it("doesn't run any of the specs", () -> {
            assertThat(steps.get(), is(empty()));
          });

        });

        describe("after running the suite", () -> {

          Supplier<ArrayList<String>> steps = let(() -> new ArrayList<>());
          Supplier<Result> result = let(() -> SpectrumHelper.run(() -> {

            aroundAll(block -> {
              block.run();
              throw new Exception("boom");
            });

            it("spec1", () -> {
              steps.get().add("spec1");
            });
            it("spec2", () -> {
              steps.get().add("spec2");
            });

          }));

          beforeEach(() -> result.get());

          it("throws a failure with the appropriate message", () -> {
            assertThat(result.get().getFailureCount(), is(1));
            List<Failure> failures = result.get().getFailures();
            assertThat(failures.get(0).getMessage(), containsString("boom"));
          });

          it("still runs the specs", () -> {
            assertThat(steps.get(), contains("spec1", "spec2"));
          });

        });

      });
    });

    describe("aroundEach and aroundAll with other fixture methods", () -> {

      Supplier<ArrayList<String>> steps = let(() -> new ArrayList<>());

      beforeEach(() -> SpectrumHelper.run(() -> {

        aroundAll(block -> {
          steps.get().add("pre-aroundAll");
          block.run();
          steps.get().add("post-aroundAll");
        });

        aroundEach(block -> {
          steps.get().add("pre-aroundEach");
          block.run();
          steps.get().add("post-aroundEach");
        });

        beforeAll(() -> {
          steps.get().add("beforeAll");
        });
        beforeEach(() -> {
          steps.get().add("beforeEach");
        });
        afterEach(() -> {
          steps.get().add("afterEach");
        });
        afterAll(() -> {
          steps.get().add("afterAll");
        });

        it("spec1", () -> {
          steps.get().add("spec1");
        });
        it("spec2", () -> {
          steps.get().add("spec2");
        });
      }));

      it("wraps around any declared beforeEach or afterEach blocks", () -> {
        assertThat(steps.get(), contains(
            "pre-aroundAll",
            "beforeAll",
            "pre-aroundEach",
            "beforeEach",
            "spec1",
            "afterEach",
            "post-aroundEach",
            "pre-aroundEach",
            "beforeEach",
            "spec2",
            "afterEach",
            "post-aroundEach",
            "afterAll",
            "post-aroundAll"));
      });

    });

    describe("with nesting", () -> {

      Supplier<ArrayList<String>> steps = let(() -> new ArrayList<>());

      beforeEach(() -> SpectrumHelper.run(() -> {

        aroundAll(block -> {
          steps.get().add("pre-aroundAll1");
          block.run();
          steps.get().add("post-aroundAll1");
        });

        aroundEach(block -> {
          steps.get().add("pre-aroundEach1");
          block.run();
          steps.get().add("post-aroundEach1");
        });

        it("spec1", () -> {
          steps.get().add("spec1");
        });

        describe("nested", () -> {

          aroundAll(block -> {
            steps.get().add("pre-aroundAll2");
            block.run();
            steps.get().add("post-aroundAll2");
          });

          aroundEach(block -> {
            steps.get().add("pre-aroundEach2");
            block.run();
            steps.get().add("post-aroundEach2");
          });

          it("spec2", () -> {
            steps.get().add("spec2");
          });
          it("spec3", () -> {
            steps.get().add("spec3");
          });
        });

      }));


      it("run from outside in", () -> {
        assertThat(steps.get(), contains(
            "pre-aroundAll1",
            "pre-aroundEach1",
            "spec1",
            "post-aroundEach1",
            "pre-aroundAll2",
            "pre-aroundEach1",
            "pre-aroundEach2",
            "spec2",
            "post-aroundEach2",
            "post-aroundEach1",
            "pre-aroundEach1",
            "pre-aroundEach2",
            "spec3",
            "post-aroundEach2",
            "post-aroundEach1",
            "post-aroundAll2",
            "post-aroundAll1"));
      });


    });

  }
}

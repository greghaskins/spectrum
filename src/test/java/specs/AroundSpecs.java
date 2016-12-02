package specs;

import static com.greghaskins.spectrum.Spectrum.afterAll;
import static com.greghaskins.spectrum.Spectrum.afterEach;
import static com.greghaskins.spectrum.Spectrum.aroundEach;
import static com.greghaskins.spectrum.Spectrum.beforeAll;
import static com.greghaskins.spectrum.Spectrum.beforeEach;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.Spectrum.let;
import static matchers.IsFailure.failure;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
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
            RuntimeException.class, "aroundEach did not run the block")));

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

      describe("with other fixture methods", () -> {

        Supplier<ArrayList<String>> steps = let(() -> new ArrayList<>());

        beforeEach(() -> SpectrumHelper.run(() -> {

          aroundEach(block -> {
            steps.get().add("pre-around");
            block.run();
            steps.get().add("post-around");
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

          it("spec", () -> {
            steps.get().add("spec");
          });
        }));

        it("wraps around any declared beforeEach or afterEach blocks", () -> {
          assertThat(steps.get(), contains(
              "beforeAll",
              "pre-around",
              "beforeEach",
              "spec",
              "afterEach",
              "post-around",
              "afterAll"));
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


  }
}

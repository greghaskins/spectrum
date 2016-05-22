package specs;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static matchers.IsFailure.failure;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import com.greghaskins.spectrum.Spectrum;

import helpers.SpectrumRunner;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

@SuppressWarnings("unchecked")
@RunWith(Spectrum.class)
public class NestingSpec {
  {

    describe("A spec with tests and nested contexts", () -> {

      it("runs them in declaration order", () -> {
        final Result result = SpectrumRunner.run(getSpecWithTestsAndNestedContextsThatAllFail());

        assertThat(result.getFailureCount(), is(3));
        assertThat(result.getFailures(),
            contains(failure("fails the first test", AssertionError.class, "boom 1"),
                failure("fails the second test", AssertionError.class, "boom 2"),
                failure("fails the third test", AssertionError.class, "boom 3")));
      });

    });

  }

  private static Class<?> getSpecWithTestsAndNestedContextsThatAllFail() {
    class Spec {
      {
        describe("A spec where everything fails", () -> {

          it("fails the first test", () -> {
            fail("boom 1");
          });

          describe("including the inner context", () -> {

            it("fails the second test", () -> {
              fail("boom 2");
            });

          });

          it("fails the third test", () -> {
            fail("boom 3");
          });

        });
      }
    }

    return Spec.class;
  }
}

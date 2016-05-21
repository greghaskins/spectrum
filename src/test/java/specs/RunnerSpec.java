package specs;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import helpers.SpectrumRunner;

import org.junit.runner.Result;
import org.junit.runner.RunWith;

import com.greghaskins.spectrum.Spectrum;

@RunWith(Spectrum.class)
public class RunnerSpec {
  {

    describe("Contexts with no tests", () -> {

      it("are ignored", () -> {
        final Result result = SpectrumRunner.run(getSpecWithNoTests());
        assertThat(result.getIgnoreCount(), is(2));
      });

    });

  }

  private static final Class<?> getSpecWithNoTests() {
    class Spec {
      {

        it("has a test in the outer context, so that doesn't get ignored", () -> {

        });

        describe("no tests by itself or in children, will be ignored", () -> {

          describe("no tests either, will be ignored", () -> {

          });

        });

      }
    }
    return Spec.class;
  }
}

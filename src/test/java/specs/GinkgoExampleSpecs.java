package specs;

import static com.greghaskins.spectrum.GinkgoSyntax.context;
import static com.greghaskins.spectrum.GinkgoSyntax.fcontext;
import static com.greghaskins.spectrum.GinkgoSyntax.xcontext;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.SpectrumHelper;

import org.junit.runner.Result;
import org.junit.runner.RunWith;

/**
 * Demonstrate how the Ginkgo Syntax can be used.
 */
@RunWith(Spectrum.class)
public class GinkgoExampleSpecs {
  {
    describe("a ginkgo style spec can have a context", () -> {
      context("surrounding the specs", () -> {
        it("helping to document the specification", () -> {
        });
      });
    });

    describe("focusing and ignoring", () -> {
      it("context focusing is possible", GinkgoExampleSpecs::focusingExample);
      it("context ignoring is possible", GinkgoExampleSpecs::ignoringExample);
    });
  }

  private static void focusingExample() throws Exception {
    class Example {
      {
        describe("a spec with", () -> {
          fcontext("a focused context", () -> {
            it("runs the spec", () -> {
            });
          });

          it("doesn't run this one", () -> {
          });
        });
      }
    }

    final Result result = SpectrumHelper.run(Example.class);
    assertThat(result.getRunCount(), is(1));
  }

  private static void ignoringExample() throws Exception {
    class Example {
      {
        describe("a spec with", () -> {
          xcontext("an ignored context", () -> {
            it("does not run the spec", () -> {
            });
          });
        });
      }
    }

    final Result result = SpectrumHelper.run(Example.class);
    assertThat(result.getIgnoreCount(), is(1));
  }

}

package specs;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.internal.ConfiguredBlock.ignore;
import static com.greghaskins.spectrum.internal.ConfiguredBlock.with;
import static com.greghaskins.spectrum.model.BlockConfiguration.Factory.ignore;

import com.greghaskins.spectrum.Spectrum;

import org.junit.runner.RunWith;

/**
 * Demonstrate how to focus and ignore specs using
 * {@link com.greghaskins.spectrum.model.BlockConfiguration}.
 */
@RunWith(Spectrum.class)
public class BlockConfigurationSpecs {
  {
    describe("A normal suite", () -> {
      describe("Has ignored suite", with(ignore(), () -> {
        it("will not run this spec", () -> {
        });
      }));

      describe("Has suite with ignored specs", () -> {
        it("is not ignored", () -> {
        });

        it("is ignored", with(ignore(), () -> {
        }));

        it("is ignored for a reason", with(ignore("not important for this release"), () -> {
        }));

        it("is a block ignored as a block", ignore(() -> {
        }));

        it("is a block ignored as a block for a reason", ignore("Not ready yet", () -> {
        }));
      });
    });
  }
}

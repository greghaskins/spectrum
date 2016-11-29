package specs;

import static com.greghaskins.spectrum.PreConditionBlock.ignore;
import static com.greghaskins.spectrum.PreConditionBlock.with;
import static com.greghaskins.spectrum.PreConditions.PreConditionsFactory.ignore;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;

import com.greghaskins.spectrum.Spectrum;

import org.junit.runner.RunWith;

/**
 * Demonstrate how to focus and ignore specs using preconditions.
 */
@RunWith(Spectrum.class)
public class PreConditionSpecs {
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

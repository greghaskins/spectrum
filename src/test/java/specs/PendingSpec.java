package specs;

import static com.greghaskins.spectrum.Spectrum.beforeEach;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.Spectrum.pending;
import static org.junit.Assert.assertFalse;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.SpectrumHelper;
import com.greghaskins.spectrum.Variable;

import org.junit.runner.RunWith;

/**
 * Use of the pending function to make a spec pending.
 */
@RunWith(Spectrum.class)
public class PendingSpec {
  {
    Variable<Boolean> hasSpecRun = new Variable<>(false);

    describe("Jasmine and RSpec style pending", () -> {

      beforeEach(() -> SpectrumHelper.run(() -> {
        it("sets to ignored a spec with pending in it", () -> {
          pending();
          hasSpecRun.set(true);
        });

        it("can have a message to show why it is pending", () -> {
          pending("not likely to be implemented for a while");
          hasSpecRun.set(true);
        });
      }));

      it("did not run any specs up to now", () -> {
        assertFalse(hasSpecRun.get());
      });
    });
  }
}

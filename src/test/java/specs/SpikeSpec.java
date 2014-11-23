package specs;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import com.greghaskins.spectrum.Spectrum;

@Ignore
@RunWith(Spectrum.class)
public class SpikeSpec {{

    describe("a spec", () -> {

        describe("and a nested context", () -> {

            it("does something", () -> {

            });

        });

        it("does what it should do", () -> {
            Assert.assertTrue(true);
        });

        it("doesn't pass, though", () -> {
            Assert.fail("bummer");
        });

    });

}}

package examples;

import org.junit.Assert;
import org.junit.runner.RunWith;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Spectrum.Describe;
import com.greghaskins.spectrum.Spectrum.It;

@Describe("A spec with one failing test")
@RunWith(Spectrum.class)
public class SpecWithOneFailingTest {

    @It("should fail") void _() {
        Assert.fail("boom goes the dynamite");
    }
}

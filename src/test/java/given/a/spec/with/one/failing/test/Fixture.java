package given.a.spec.with.one.failing.test;

import org.junit.Assert;
import org.junit.runner.RunWith;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Spectrum.Describe;
import com.greghaskins.spectrum.Spectrum.It;

public class Fixture {

    public static Class<?> getSpecWithOneFailingTest(){
        @Describe("A spec with one failing test")
        @RunWith(Spectrum.class)
        class Spec {

            @It("should fail") void _() {
                Assert.fail("boom goes the dynamite");
            }
        }
        return Spec.class;
    }

}

package specs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Spectrum.Describe;
import com.greghaskins.spectrum.Spectrum.It;

@Describe("a Spectrum test suite")
@RunWith(Spectrum.class)
public class TestSuite_Spec {

    private static Class<?> getSpecWithHelperMethod() {

        @RunWith(Spectrum.class)
        @Describe("a spec with a non-test helper method")
        class Spec {
            @It("should work") void test() {
                doSomething();
            }
            private void doSomething() { }
        }
        return Spec.class;
    }

    @It("will ignore methods without an @It annotation") void test() {
        final Result result = JUnitCore.runClasses(getSpecWithHelperMethod());
        assertThat(result.wasSuccessful(), is(true));
    }


}

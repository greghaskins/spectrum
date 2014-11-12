package specs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.runner.Result;
import org.junit.runner.RunWith;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Spectrum.BeforeEach;
import com.greghaskins.spectrum.Spectrum.Describe;
import com.greghaskins.spectrum.Spectrum.It;

@Describe("a method marked @BeforeEach")
@RunWith(Spectrum.class)
public class BeforeEach_Spec {

    int counter = -1;

    @BeforeEach void setCounterToZero() {
        counter = 0;
    }

    @It("is run before a test") void test1() {
        assertThat(counter, is(0));
        counter = 1;
    }

    @It("is run before the next test to reset the context") void test2() {
        assertThat(counter, is(0));
    }

    @It("that throws an exception causes all tests in that context to fail") void testsFail() throws Exception {
        final Result result = SpectrumRunner.run(getSpecWithExplodingBeforeEach());
        assertThat(result.getFailureCount(), is(2));
    }

    private static Class<?> getSpecWithExplodingBeforeEach(){

        @Describe("a spec with an exploding @BeforeEach method")
        class Spec {

            @BeforeEach void goBoom() throws Throwable {
                throw new Exception();
            }

            @It("should fail") void test1() { }

            @It("should also fail") void test2() { }

        }
        return Spec.class;
    }

}

package specs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.runner.Result;
import org.junit.runner.RunWith;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Spectrum.BeforeEach;
import com.greghaskins.spectrum.Spectrum.Describe;
import com.greghaskins.spectrum.Spectrum.It;

@Describe("methods marked @BeforeEach")
@RunWith(Spectrum.class)
public class BeforeEach_Spec {

    int number = -1;
    String text = null;

    @BeforeEach void setNumberToZero() {
        number = 0;
    }

    @BeforeEach void setTextToInitialValue() {
        text = "initial value";
    }

    @It("run before a test") void test1() {
        verifyContextHasBeenReset();
        changeContextValues();
    }

    @It("run before the next test to reset the context") void test2() {
        verifyContextHasBeenReset();
        changeContextValues();
    }

    private void changeContextValues() {
        number = 1;
        text = "another value";
    }

    private void verifyContextHasBeenReset() {
        assertThat(number, is(0));
        assertThat(text, is("initial value"));
    }

    @Describe("that throw an exception") static class $ {

        @It("cause all tests in that context to fail") void testsFail() throws Exception {
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

}

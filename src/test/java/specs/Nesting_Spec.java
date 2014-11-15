package specs;

import static matchers.IsFailure.failure;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import org.junit.Assert;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Spectrum.Describe;
import com.greghaskins.spectrum.Spectrum.It;
import com.greghaskins.spectrum.runner.UnableToInstantiateContextError;

@Describe("a spec with nested contexts")
@RunWith(Spectrum.class)
public class Nesting_Spec {

    @It("will run tests in those child contexts") void test() throws Exception {
        final Result result = SpectrumRunner.run(getSpecWithNestedContext());
        assertThat(result.getRunCount(), is(2));
    }

    @It("will record failures from inner contexts") void failures() throws Exception {
        final Result result = SpectrumRunner.run(getSpecWithNestedContext());
        assertThat(result.getFailures(), hasItem(failure("has a failing test", AssertionError.class, "kaboom")));
    }

    private static Class<?> getSpecWithNestedContext() {

        @Describe("a spec")
        class Spec {

            @Describe("with a nested context") class $ {

                @It("has a failing test") void fail() {
                    Assert.fail("kaboom");
                }

                @It("has a passing test") void pass() {
                    assertThat(true, is(true));
                }

            }

        }
        return Spec.class;
    }

    @It("throws an error when the inner context cannot be instantiated") void error() throws Exception {
        final Class<?> specClass = getSpecWithInvalidNestedContext();
        try {
            SpectrumRunner.run(specClass);
            Assert.fail("Should have thrown an UnableToInstantiateContextError");
        } catch (final UnableToInstantiateContextError expected) {
            assertThat(expected.getMessage(), containsString(specClass.getName()));
        }
    }

    private static Class<?> getSpecWithInvalidNestedContext() {

        @Describe("a spec")
        class Spec {

            @Describe("with a nested context that requires a constructor parameter") class Nested {

                @SuppressWarnings("unused")
                Nested(final String something) { }

                @It("has a passing test") void pass() {
                    assertThat(true, is(true));
                }

            }

        }
        return Spec.class;
    }

}

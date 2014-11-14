package specs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import org.junit.Assert;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Spectrum.Describe;
import com.greghaskins.spectrum.Spectrum.It;
import com.greghaskins.spectrum.runner.UnableToInstantiateContextError;

@Describe("a Spectrum test suite")
@RunWith(Spectrum.class)
public class TestSuite_Spec {

    @It("will ignore methods without an @It annotation") void test() throws Exception {
        final Result result = SpectrumRunner.run(getSpecWithHelperMethod());
        assertThat(result.wasSuccessful(), is(true));
    }

    private static Class<?> getSpecWithHelperMethod() {

        @Describe("a spec with a non-test helper method")
        class Spec {
            @It("should work") void test() {
                doSomething();
            }
            private void doSomething() { }
        }
        return Spec.class;
    }

    @It("throws an error when the context cannot be created") void cannotCreateContext() throws Exception {
        final Class<?> specClass = getSpecWhichCannotBeInstantiated();
        try {
            SpectrumRunner.run(specClass);
            Assert.fail("Should have thrown an UnableToInstantiateContextError");
        } catch (final UnableToInstantiateContextError expected) {
            assertThat(expected.getMessage(), containsString(specClass.getName()));
        }
    }

    private static Class<?> getSpecWhichCannotBeInstantiated() {

        @Describe("a spec with a constructor that requires a parameter")
        class Spec {

            @SuppressWarnings("unused")
            public Spec(final String something) { }

            @It("cannot be run") void test() { }

        }
        return Spec.class;
    }

    // TODO a spec with no tests gets marked as ignored
    // TODO @AfterEach

}

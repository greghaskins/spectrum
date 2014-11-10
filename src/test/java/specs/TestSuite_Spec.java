package specs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import org.junit.Assert;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Spectrum.Describe;
import com.greghaskins.spectrum.Spectrum.It;
import com.greghaskins.spectrum.UnableToInstantiateContextError;

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

    private static Class<?> getSpecWhichCannotBeInstantiated() {

        @RunWith(Spectrum.class)
        @Describe("a spec with a non-test helper method")
        class Spec {

            @SuppressWarnings("unused")
            public Spec(final String something) { }

            @It("cannot be run") void test() { }

        }
        return Spec.class;
    }

    @It("will ignore methods without an @It annotation") void test() {
        final Result result = JUnitCore.runClasses(getSpecWithHelperMethod());
        assertThat(result.wasSuccessful(), is(true));
    }

    @It("throws an error when the context cannot be created") void cannotCreateContext() throws Exception {
        final Class<?> specClass = getSpecWhichCannotBeInstantiated();
        try {
            runWithSpectrum(specClass);
            Assert.fail("Should have thrown an UnableToInstantiateContextError");
        } catch (final UnableToInstantiateContextError expected){
            assertThat(expected.getMessage(), containsString(specClass.getName()));
        }
    }

    private void runWithSpectrum(final Class<?> specClass) throws InitializationError {
        new JUnitCore().run(Request.runner(new Spectrum(specClass)));
    }


}

package given.a.spec.with.exception.in.constructor;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.greghaskins.spectrum.SpecInitializationError;
import com.greghaskins.spectrum.Spectrum;

public class WhenInitializingTheRunner {

    @Test(expected = SpecInitializationError.class)
    public void aSpecInitializationErrorIsThrown() throws Exception {
        new Spectrum(getSpecThatThrowsAnExceptionInInitializer());
    }

    @Test
    public void theErrorHasADirectCause_NotWrappedInInvocationTargetException() throws Exception {
        try {
            new Spectrum(getSpecThatThrowsAnExceptionInInitializer());
        } catch (final SpecInitializationError e) {
            assertThat(e.getCause().getMessage(), is("kaboom"));
        }
    }

    private static Class<?> getSpecThatThrowsAnExceptionInInitializer() {
        class Spec {{
            if (true) {
                throw new RuntimeException("kaboom");
            }
        }}
        return Spec.class;
    }


}

package given.a.spec.with.constructor.parameters;

import org.junit.Test;

import com.greghaskins.spectrum.SpecInitializationError;
import com.greghaskins.spectrum.Spectrum;

public class WhenInitializingTheRunner {

    @Test(expected = SpecInitializationError.class)
    public void aSpecInitializationErrorIsThrown() throws Exception {
        new Spectrum(getSpecThatRequiresAConstructorParameter());
    }

    private static Class<?> getSpecThatRequiresAConstructorParameter() {
        class Spec {
            @SuppressWarnings("unused")
            public Spec(final String something){ }
        }
        return Spec.class;
    }

}

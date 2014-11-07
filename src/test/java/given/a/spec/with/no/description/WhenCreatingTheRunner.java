package given.a.spec.with.no.description;

import org.junit.Test;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.errors.MissingDescribeAnnotationError;

public class WhenCreatingTheRunner {

    public class SpecWithoutDescription {

    }

    @Test(expected = MissingDescribeAnnotationError.class)
    public void theConstructorThrowsAMissingDescribeAnnotationError() throws Exception {
        new Spectrum(SpecWithoutDescription.class);
    }

}

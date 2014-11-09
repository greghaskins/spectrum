package given.a.spec.with.no.description;

import org.junit.Test;

import com.greghaskins.spectrum.MissingDescribeAnnotationError;
import com.greghaskins.spectrum.Spectrum;

public class WhenCreatingTheRunner {

    public class SpecWithoutDescription {

    }

    @Test(expected = MissingDescribeAnnotationError.class)
    public void theConstructorThrowsAMissingDescribeAnnotationError() throws Exception {
        new Spectrum(SpecWithoutDescription.class);
    }

}

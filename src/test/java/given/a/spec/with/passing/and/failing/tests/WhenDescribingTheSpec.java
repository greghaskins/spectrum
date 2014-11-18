package given.a.spec.with.passing.and.failing.tests;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.Description;

import com.greghaskins.spectrum.Spectrum;

public class WhenDescribingTheSpec {

    @Test
    public void thereAreFiveTests() throws Exception {
        final Description description = new Spectrum(Fixture.getSpecWithPassingAndFailingTests()).getDescription();
        assertThat(description.getChildren(), hasSize(5));
    }

}

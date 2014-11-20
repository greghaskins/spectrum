package given.a.spec.with.passing.and.failing.tests;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;

import com.greghaskins.spectrum.Spectrum;

public class WhenDescribingTheSpec {

    private Description description;

    @Before
    public void before() throws Exception {
        description = new Spectrum(Fixture.getSpecWithPassingAndFailingTests()).getDescription();
    }

    @Test
    public void thereAreFiveTests() throws Exception {
        assertThat(getFirstContext().getChildren(), hasSize(5));
    }

    private Description getFirstContext() {
        return description.getChildren().get(0);
    }

}

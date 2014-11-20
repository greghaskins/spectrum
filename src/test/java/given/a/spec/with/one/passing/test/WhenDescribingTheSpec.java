package given.a.spec.with.one.passing.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;

import com.greghaskins.spectrum.Spectrum;

public class WhenDescribingTheSpec {

    private Description description;

    @Before
    public void before() throws Exception {
        description = new Spectrum(Fixture.getSpecWithOnePassingTest()).getDescription();
    }

    @Test
    public void theRootSuiteIsTheTestClass() throws Exception {
        assertThat(description.getDisplayName(), is(Fixture.getSpecWithOnePassingTest().getName()));
    }

    @Test
    public void thereIsOneChildSuite() throws Exception {
        assertThat(description.getChildren(), hasSize(1));
    }

    @Test
    public void theSuiteDescriptionIsCorrect() throws Exception {
        assertThat(getFirstChildSuite().getDisplayName(), is("a spec with one passing test"));
    }

    @Test
    public void thereIsOneChildTest() throws Exception {
        assertThat(getFirstChildSuite().getChildren(), hasSize(1));
    }

    @Test
    public void theTestNameIsCorrect() throws Exception {
        assertThat(getFirstChildSuite().getChildren().get(0).getMethodName(), is("should pass"));
    }

    private Description getFirstChildSuite() {
        return description.getChildren().get(0);
    }

}

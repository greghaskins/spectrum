package given.a.spec.with.one.failing.test;

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
    public void describeTheSpec() throws Exception{
        description = new Spectrum(Fixture.getSpecWithOneFailingTest()).getDescription();
    }

    @Test
    public void thereShouldBeOneChildTest() throws Exception {
        assertThat(description.getChildren(), hasSize(1));
    }

    @Test
    public void theTestShouldGetItsClassNameFromTheParentContext() throws Exception {
        assertThat(getTestDescription().getClassName(), is("A spec with one failing test"));
    }

    @Test
    public void theTestShouldGetItsMethodNameFromTheItAnnotation() throws Exception {
        assertThat(getTestDescription().getMethodName(), is("should fail"));
    }

    @Test
    public void theTestShouldFail() throws Exception {

    }

    private Description getTestDescription() {
        return description.getChildren().get(0);
    }

}

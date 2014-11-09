package given.a.spec.with.two.failures.and.three.passing.tests;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;

import com.greghaskins.spectrum.Spectrum;

public class WhenDescribingTheSpec {

    private Description description;

    @Before
    public void getTheDescription() throws Exception {
        description = new Spectrum(Fixture.getSpecWithTwoFailuresAndThreePassingTests()).getDescription();
    }

    @Test
    public void thereShouldBeFiveChildTests() throws Exception {
        assertThat(description.getChildren(), hasSize(5));
    }

    @Test
    public void theTestsShouldHaveTheCorrectNames() throws Exception {
        final List<String> testNames = getMethodNames(description.getChildren());
        assertThat(testNames, containsInAnyOrder("fails test 1", "passes test 2", "passes test 3", "fails test 4", "passes test 5"));
    }

    @Test
    public void theTestsShouldAllHaveTheParentContextName() throws Exception {
        for (final Description testDescription : description.getChildren()) {
            assertThat(testDescription.getClassName(), is("A spec with two failures and three passing tests"));
        }
    }

    private static List<String> getMethodNames(final List<Description> testDescriptions) {
        final ArrayList<String> methodNames = new ArrayList<String>();
        for (final Description description : testDescriptions) {
            methodNames.add(description.getMethodName());
        }
        return methodNames;
    }

}
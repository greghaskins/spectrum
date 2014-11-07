package given.an.empty.spec;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;

import com.greghaskins.spectrum.Spectrum;

import examples.EmptySpec;

public class WhenDescribingTheSuite {

    @Test
    public void theClassDescriptionIsUsedAsTheSuiteName() throws Exception {
        final Runner runner = new Spectrum(EmptySpec.class);
        final Description description = runner.getDescription();

        assertThat(description.getDisplayName(), is("An empty specification"));
    }

}

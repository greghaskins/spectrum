package given.a.spec.with.one.passing.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import helpers.SpectrumRunner;

import org.junit.Test;
import org.junit.runner.Result;

public class WhenRunningTheSpec {

    @Test
    public void theRunCountIsOne() throws Exception {
        final Result result = SpectrumRunner.run(Fixture.getSpecWithOnePassingTest());
        assertThat(result.getRunCount(), is(1));
    }

}

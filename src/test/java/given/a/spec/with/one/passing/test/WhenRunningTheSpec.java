package given.a.spec.with.one.passing.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import helpers.SpectrumRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Result;

public class WhenRunningTheSpec {

    private Result result;

    @Before
    public void before() throws Exception {
        result = SpectrumRunner.run(Fixture.getSpecWithOnePassingTest());
    }

    @Test
    public void theRunCountIsOne() throws Exception {
        assertThat(result.getRunCount(), is(1));
    }

    @Test
    public void theRunIsSuccessful() throws Exception {
        assertThat(result.wasSuccessful(), is(true));
    }

}

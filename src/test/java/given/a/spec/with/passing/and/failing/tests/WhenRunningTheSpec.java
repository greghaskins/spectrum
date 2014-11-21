package given.a.spec.with.passing.and.failing.tests;

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
        result = SpectrumRunner.run(Fixture.getSpecWithPassingAndFailingTests());
    }

    @Test
    public void fiveTestsAreRun() throws Exception {
        assertThat(result.getRunCount(), is(5));
    }

}

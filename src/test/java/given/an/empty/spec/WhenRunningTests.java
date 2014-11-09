package given.an.empty.spec;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class WhenRunningTests {

    private Result result;

    @Before
    public void runEmptySpec(){
        result = JUnitCore.runClasses(Fixture.getEmptySpec());
    }

    @Test
    public void theRunCountShouldBeZero() throws Exception {
        assertThat(result.getRunCount(), is(0));
    }

    @Test
    public void theRunIsSuccessful() throws Exception {
        assertThat(result.wasSuccessful(), is(true));
    }

}

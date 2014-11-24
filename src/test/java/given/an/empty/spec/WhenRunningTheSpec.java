package given.an.empty.spec;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.junit.runner.Result;

public class WhenRunningTheSpec {

    @Test
    public void theRunCountIsZero() throws Exception {
        final Result result = helpers.SpectrumRunner.run(Fixture.getEmptySpec());
        assertThat(result.getRunCount(), is(0));
    }

}

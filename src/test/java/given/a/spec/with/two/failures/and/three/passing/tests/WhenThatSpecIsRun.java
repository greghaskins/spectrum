package given.a.spec.with.two.failures.and.three.passing.tests;

import static matchers.IsFailure.failure;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class WhenThatSpecIsRun {

    private Result result;

    @Before
    public void runTheSpec() {
        result = JUnitCore.runClasses(Fixture.getSpecWithTwoFailuresAndThreePassingTests());
    }

    @Test
    public void thereShouldBeFiveTotalRuns() throws Exception {
        assertThat(result.getRunCount(), is(5));
    }

    @Test
    public void thereShouldBeTwoFailures() throws Exception {
        assertThat(result.getFailureCount(), is(2));
    }

    @Test
    public void theFirstTestFailureIsRecorded() throws Exception {
        assertThat(result.getFailures(), hasItem(failure("fails test 1", AssertionError.class, "failure message one")));
    }

    @Test
    public void theSecondTestFailureIsRecorded() throws Exception {
        assertThat(result.getFailures(), hasItem(failure("fails test 4", Exception.class, "failure message four")));
    }

}
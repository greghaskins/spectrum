package given.a.spec.with.passing.and.failing.tests;

import static matchers.IsFailure.failure;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import helpers.SpectrumRunner;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

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

  @Test
  public void twoTestsFail() throws Exception {
    assertThat(result.getFailureCount(), is(2));
  }

  @Test
  public void theFailuresDescribeWhatWentWrong() throws Exception {
    final List<Failure> failures = result.getFailures();
    assertThat(failures.get(0), is(failure("fails test 1", AssertionError.class, "failure message one")));
    assertThat(failures.get(1), is(failure("fails test 4", Exception.class, "failure message four")));
  }

}

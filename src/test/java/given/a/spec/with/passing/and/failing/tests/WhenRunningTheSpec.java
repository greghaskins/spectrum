package given.a.spec.with.passing.and.failing.tests;

import static matchers.IsFailure.failure;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.greghaskins.spectrum.SpectrumHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.List;

public class WhenRunningTheSpec {

  private Result result;

  @Before
  public void before() throws Exception {
    this.result = SpectrumHelper.run(Fixture.getSpecWithPassingAndFailingTests());
  }

  @Test
  public void fiveTestsAreRun() throws Exception {
    assertThat(this.result.getRunCount(), is(5));
  }

  @Test
  public void twoTestsFail() throws Exception {
    assertThat(this.result.getFailureCount(), is(2));
  }

  @Test
  public void theFailuresDescribeWhatWentWrong() throws Exception {
    final List<Failure> failures = this.result.getFailures();
    assertThat(failures.get(0),
        is(failure("fails test 1", AssertionError.class, "failure message one")));
    assertThat(failures.get(1),
        is(failure("fails test 4", Exception.class, "failure message four")));
  }

}

package given.a.spec.with.exception.in.beforeeach.block.and.aftereach.block;

import helpers.SpectrumRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Result;

import static matchers.IsFailure.failure;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenRunningTheSpec {

  private Result result;

  @Before
  public void before() throws Exception {
    this.result = SpectrumRunner.run(Fixture.getSpecThatThrowsAnExceptionInBeforeEachBlock());
  }

  @Test
  public void thereIsOneFailureForEachAffectedTest() throws Exception {
    assertThat(this.result.getFailureCount(), is(2));
  }

  @Test
  public void theFailureExplainsWhatHappened() throws Exception {
    assertThat(this.result.getFailures().get(0),
        is(failure("a failing test", Fixture.SomeException.class, "kaboom")));
  }

}

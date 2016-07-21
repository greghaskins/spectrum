package given.a.spec.with.constructor.parameters;

import static matchers.IsFailure.failure;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.greghaskins.spectrum.UnableToConstructSpecException;

import helpers.SpectrumRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Result;

public class WhenRunningTheSpec {

  private Result result;

  @Before
  public void before() throws Exception {
    this.result = SpectrumRunner.run(Fixture.getSpecThatRequiresAConstructorParameter());
  }

  @Test
  public void thereIsOneFailure() throws Exception {
    assertThat(this.result.getFailureCount(), is(1));
  }

  @Test
  public void theFailureExplainsWhatHappened() throws Exception {
    assertThat(this.result.getFailures().get(0),
        is(failure("encountered an error", UnableToConstructSpecException.class,
            Fixture.getSpecThatRequiresAConstructorParameter().getName())));
  }

}

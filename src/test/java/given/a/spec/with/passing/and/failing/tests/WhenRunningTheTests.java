package given.a.spec.with.passing.and.failing.tests;

import static org.mockito.Mockito.mock;

import com.greghaskins.spectrum.Spectrum;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class WhenRunningTheTests {

  private static final String CONTEXT_NAME = "a spec with three passing and two failing tests";

  private RunNotifier runNotifier;

  @Before
  public void before() throws Exception {
    final Runner runner = new Spectrum(Fixture.getSpecWithPassingAndFailingTests());
    this.runNotifier = mock(RunNotifier.class);

    runner.run(this.runNotifier);
  }

  @Test
  public void theStartFailureAndFinishedNotificationsAreFiredForFailingTests() throws Exception {
    final Description descriptionOfFailingTest =
        Description.createTestDescription(CONTEXT_NAME, "fails test 1");

    final InOrder inOrder = Mockito.inOrder(this.runNotifier);
    inOrder.verify(this.runNotifier).fireTestStarted(descriptionOfFailingTest);
    inOrder.verify(this.runNotifier).fireTestFailure(Matchers.any());
    inOrder.verify(this.runNotifier).fireTestFinished(descriptionOfFailingTest);
  }

  @Test
  public void theStartAndFinishedNotificationsAreFiredForPassingTests() throws Exception {
    final Description descriptionOfPassingTest =
        Description.createTestDescription(CONTEXT_NAME, "passes test 3");

    final InOrder inOrder = Mockito.inOrder(this.runNotifier);
    inOrder.verify(this.runNotifier).fireTestStarted(descriptionOfPassingTest);
    inOrder.verify(this.runNotifier).fireTestFinished(descriptionOfPassingTest);
  }

}

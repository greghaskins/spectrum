package given.a.spec.with.passing.and.failing.tests;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;

import com.greghaskins.spectrum.Spectrum;

public class WhenDescribingTheSpec {

  private Description description;

  @Before
  public void before() throws Exception {
    description = new Spectrum(Fixture.getSpecWithPassingAndFailingTests()).getDescription();
  }

  @Test
  public void thereAreFiveTests() throws Exception {
    assertThat(getFirstContext().getChildren(), hasSize(5));
  }

  @Test
  public void theTestsGetTheirClassNameFromTheContainingDescribeBlock() throws Exception {
    for (final Description testDescription : getFirstContext().getChildren()) {
      assertThat(testDescription.getClassName(), is("a spec with three passing and two failing tests"));
    }
  }

  @Test
  public void theTestsAreInDeclarationOrder() throws Exception {
    final ArrayList<Description> testDescriptions = getFirstContext().getChildren();
    assertThat(testDescriptions.get(0).getMethodName(), is("fails test 1"));
    assertThat(testDescriptions.get(1).getMethodName(), is("passes test 2"));
    assertThat(testDescriptions.get(2).getMethodName(), is("passes test 3"));
    assertThat(testDescriptions.get(3).getMethodName(), is("fails test 4"));
    assertThat(testDescriptions.get(4).getMethodName(), is("passes test 5"));
  }

  private Description getFirstContext() {
    return description.getChildren().get(0);
  }

}

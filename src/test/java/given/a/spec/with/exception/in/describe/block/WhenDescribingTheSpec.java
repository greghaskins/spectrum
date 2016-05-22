package given.a.spec.with.exception.in.describe.block;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.greghaskins.spectrum.Spectrum;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;

public class WhenDescribingTheSpec {

  private Description description;

  @Before
  public void before() throws Exception {
    description =
        new Spectrum(Fixture.getSpecThatThrowsAnExceptionInDescribeBlock()).getDescription();
  }

  @Test
  public void thereIsOneChildOfTheExplodingContext() throws Exception {
    assertThat(getDescriptionForExplodingContext().getChildren(), hasSize(1));
  }

  @Test
  public void itIsClearThatAnErrorWasEncountered() throws Exception {
    assertThat(getDescriptionForError().getMethodName(), is("encountered an error"));
  }

  @Test
  public void itIsClearWhichDescribeBlockHadTheError() throws Exception {
    assertThat(getDescriptionForError().getClassName(), is("an exploding context"));
  }

  private Description getDescriptionForError() {
    return getDescriptionForExplodingContext().getChildren().get(0);
  }

  private Description getDescriptionForExplodingContext() {
    return description.getChildren().get(0);
  }

}

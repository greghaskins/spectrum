package given.a.spec.with.exception.in.beforeeach.block.and.aftereach.block;

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
    this.description =
        new Spectrum(Fixture.getSpecThatThrowsAnExceptionInBeforeEachBlock()).getDescription();
  }

  @Test
  public void thereAreTwoTests() throws Exception {
    assertThat(getFirstContext().getChildren(), hasSize(2));
  }

  @Test
  public void itIsClearThatAnErrorWasEncountered() throws Exception {
    assertThat(getDescriptionForError().getMethodName(), is("a failing test"));
  }

  @Test
  public void itIsClearWhichBeforeEachBlockHadTheError() throws Exception {
    assertThat(getDescriptionForError().getClassName(), is("an exploding beforeEach"));
  }

  private Description getDescriptionForError() {
    return getFirstContext().getChildren().get(0);
  }

  private Description getFirstContext() {
    return this.description.getChildren().get(0);
  }

}

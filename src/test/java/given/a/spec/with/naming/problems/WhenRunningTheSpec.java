package given.a.spec.with.naming.problems;

import static com.greghaskins.spectrum.SpectrumHelper.runWithListener;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.greghaskins.spectrum.SpectrumHelper;

import org.junit.Test;
import org.junit.runner.Description;

import java.util.List;

/**
 * Name filtering tests, to demonstrate how Spectrum will handle naming problems
 * of duplication and bad characters.
 */
public class WhenRunningTheSpec {
  @Test
  public void theNamesWillBeFilteredForDuplication() throws Exception {
    List<Description> names =
        runWithListener(specWithDuplicates(), new SpectrumHelper.RecordingListener())
            .getTestsStarted();

    assertThat(names.get(0).getDisplayName(), is("is awesome(My suite)"));
    assertThat(names.get(1).getDisplayName(), is("is awesome(My suite_1)"));
    assertThat(names.get(2).getDisplayName(), is("is awesome(My other suite)"));
    assertThat(names.get(3).getDisplayName(), is("is awesome_1(My other suite)"));
    assertThat(names.get(4).getDisplayName(), is("is awesome_2(My other suite)"));
  }

  @Test
  public void theNamesWillBeFilteredForBadCharacters() throws Exception {
    List<Description> names =
        runWithListener(specWithBadCharacters(), new SpectrumHelper.RecordingListener())
            .getTestsStarted();

    assertThat(names.get(0).getDisplayName(), is("is awesome [totally](My suite [awesome])"));
  }

  private static Class<?> specWithDuplicates() {
    class SpecWithDuplicates {
      {
        describe("My suite", () -> {
          it("is awesome", () -> {
          });
        });

        describe("My suite", () -> {
          it("is awesome", () -> {
          });
        });

        describe("My other suite", () -> {
          it("is awesome", () -> {
          });
          it("is awesome", () -> {
          });
          it("is awesome", () -> {
          });
        });

      }
    }

    return SpecWithDuplicates.class;
  }

  private static Class<?> specWithBadCharacters() {
    class SpecWithWithBadCharacters {
      {
        describe("My suite (awesome)", () -> {
          it("is awesome (totally)", () -> {
          });
        });

      }
    }

    return SpecWithWithBadCharacters.class;
  }
}

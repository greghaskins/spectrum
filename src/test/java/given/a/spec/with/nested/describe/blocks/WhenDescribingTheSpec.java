package given.a.spec.with.nested.describe.blocks;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

import com.greghaskins.spectrum.Spectrum;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;

public class WhenDescribingTheSpec {

  private Description mainDescription;

  @Before
  public void before() throws Exception {
    final Description rootDescription =
        new Spectrum(getSpecWithNestedDescribeBlocks()).getDescription();
    mainDescription = rootDescription.getChildren().get(0);
  }

  @Test
  public void theMainDescriptionHasTwoContextsAsChildren() throws Exception {
    assertThat(mainDescription.getChildren(),
        contains(Description.createSuiteDescription("with a first child context"),
            Description.createSuiteDescription("with a second child context")));
  }

  @Test
  public void theFirstSubContextHasThreeTests() throws Exception {
    assertThat(mainDescription.getChildren().get(0).getChildren(), hasSize(3));
  }

  @Test
  public void theSecondSubContextHasOneTest() throws Exception {
    assertThat(mainDescription.getChildren().get(1).getChildren(), hasSize(1));
  }

  private static Class<?> getSpecWithNestedDescribeBlocks() {
    class Spec {
      {

        describe("the main context", () -> {

          describe("with a first child context", () -> {

            it("has a test", () -> {
              Assert.assertTrue(true);
            });

            it("has another test", () -> {
              Assert.assertTrue(true);
            });

            it("has a third test", () -> {
              Assert.assertTrue(true);
            });

          });

          describe("with a second child context", () -> {

            it("does something", () -> {
              Assert.assertTrue(true);
            });

          });

        });
      }
    }

    return Spec.class;
  }

}

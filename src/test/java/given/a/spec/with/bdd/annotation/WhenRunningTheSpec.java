package given.a.spec.with.bdd.annotation;

import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.feature;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.given;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.scenario;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.then;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.when;
import static org.junit.Assert.assertFalse;
import static org.junit.Assume.assumeTrue;

import com.greghaskins.spectrum.SpectrumHelper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WhenRunningTheSpec {
  private static boolean thenRan = false;

  @Before
  public void before() {
    thenRan = false;
  }

  @Test
  public void bddStepFailureStopsTheSpec() throws Exception {
    SpectrumHelper.run(getBddExampleWhichFailsSpec());
    assertFalse(thenRan);
  }

  @Test
  public void bddAssumptionFailureStopsTheSpec() throws Exception {
    SpectrumHelper.run(getBddExampleWithAssumptionFailure());
    assertFalse(thenRan);
  }

  private static Class<?> getBddExampleWhichFailsSpec() {
    class Spec {
      {
        feature("BDD steps stop at failure", () -> {

          scenario("failing at when", () -> {

            given("a passing given", () -> {
              Assert.assertTrue(true);
            });

            when("the when fails", () -> {
              Assert.assertTrue(false);
            });

            then("the then can't do its thing", () -> {
              thenRan = true;
            });

          });
        });
      }
    }

    return Spec.class;
  }

  private static Class<?> getBddExampleWithAssumptionFailure() {
    class Spec {
      {
        feature("BDD steps stop at failure", () -> {

          scenario("failing at when", () -> {

            given("an assumption failure in step one", () -> {
              assumeTrue(false);
            });

            when("the when can't do its thing", () -> {
              thenRan = true;
            });

            then("the then can't do its thing", () -> {
              thenRan = true;
            });

          });
        });
      }
    }

    return Spec.class;
  }
}

package given.a.spec.with.passing.and.failing.tests;

import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;

import org.junit.Assert;

class Fixture {

  public static Class<?> getSpecWithPassingAndFailingTests() {
    class Spec {
      {
        describe("a spec with three passing and two failing tests", () -> {

          it("fails test 1", () -> {
            Assert.fail("failure message one");
          });

          it("passes test 2", () -> {
            Assert.assertTrue(true);
          });

          it("passes test 3", () -> {
            Assert.assertTrue(true);
          });

          it("fails test 4", () -> {
            throw new Exception("failure message four");
          });

          it("passes test 5", () -> {
            Assert.assertTrue(true);
          });

        });
      }
    }

    return Spec.class;
  }

}

package given.a.spec.with.one.passing.test;

import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;


class Fixture {

  public static Class<?> getSpecWithOnePassingTest() {
    class Spec {
      {
        describe("a spec with one passing test", () -> {

          it("should pass", () -> {

          });

        });
      }
    }

    return Spec.class;
  }

}

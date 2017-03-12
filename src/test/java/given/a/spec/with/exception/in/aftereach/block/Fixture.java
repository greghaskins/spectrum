package given.a.spec.with.exception.in.aftereach.block;

import static com.greghaskins.spectrum.dsl.specification.Specification.afterEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;


class Fixture {

  public static Class<?> getSpecThatThrowsAnExceptionInAfterEachBlock() {
    class Spec {
      {
        describe("an exploding afterEach", () -> {

          afterEach(() -> {
            throw new SomeException("kaboom");
          });

          it("a passing test", () -> {

          });
        });
      }
    }

    return Spec.class;
  }

  public static class SomeException extends Exception {
    private static final long serialVersionUID = 1L;

    public SomeException(final String message) {
      super(message);
    }
  }

}

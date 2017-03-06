package given.a.spec.with.exception.in.describe.block;

import static com.greghaskins.spectrum.dsl.spec.Spec.describe;
import static com.greghaskins.spectrum.dsl.spec.Spec.it;


class Fixture {

  public static Class<?> getSpecThatThrowsAnExceptionInDescribeBlock() {
    @SuppressWarnings("unused")
    class Spec {
      {
        describe("an exploding context", () -> {

          it("should not run", () -> {
            throw new Exception();
          });

          if (true) {
            throw new SomeException("kaboom");
          }

          it("also should not run", () -> {
            throw new Exception();
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

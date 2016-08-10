package given.a.spec.with.exception.in.aftereach.block;

import static com.greghaskins.spectrum.Spectrum.afterEach;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;

class Fixture {

  public static Class<?> getSpecThatThrowsAnExceptionInAfterEachBlock() {
    @SuppressWarnings("unused")
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

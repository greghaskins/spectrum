package given.a.spec.with.exception.in.beforeeach.block.and.aftereach.block;

import static com.greghaskins.spectrum.Spectrum.afterEach;
import static com.greghaskins.spectrum.Spectrum.beforeEach;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;

class Fixture {

  public static Class<?> getSpecThatThrowsAnExceptionInBeforeEachBlock() {
    class Spec {
      {
        describe("an exploding beforeEach", () -> {

          beforeEach(() -> {
            throw new SomeException("kaboom");
          });

          afterEach(() -> {
            throw new Exception();
          });

          it("a failing test", () -> {
            throw new Exception();
          });

          it("another failing test", () -> {
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

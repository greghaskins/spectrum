package given.a.spec.with.exception.in.describe.block;

import static com.greghaskins.spectrum.Spectrum.describe;

class Fixture {

  public static Class<?> getSpecThatThrowsAnExceptionInDescribeBlock() {
    class Spec {
      {
        describe("an exploding context", () -> {
          throw new SomeException("kaboom");
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

package given.a.spec.with.exception.in.beforeeach.block.and.aftereach.block;

import static com.greghaskins.spectrum.dsl.spec.Spec.afterEach;
import static com.greghaskins.spectrum.dsl.spec.Spec.beforeEach;
import static com.greghaskins.spectrum.dsl.spec.Spec.describe;
import static com.greghaskins.spectrum.dsl.spec.Spec.it;


class Fixture {

  public static Class<?> getSpecThatThrowsAnExceptionInBeforeEachAndAfterEachBlocks() {
    class Spec {
      {
        describe("an exploding beforeEach", () -> {

          beforeEach(() -> {
            throw new SomeException("beforeEach went kaboom");
          });

          afterEach(() -> {
            throw new SomeException("afterEach went poof");
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

  public static class SomeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SomeException(final String message) {
      super(message);
    }
  }

}

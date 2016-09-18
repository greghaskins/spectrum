package specs;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.Spectrum.let;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import com.greghaskins.spectrum.Spectrum;

import helpers.SpectrumRunner;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@RunWith(Spectrum.class)
public class LetSpecs {
  {
    describe("The `let` helper function", () -> {

      final Supplier<List<String>> items = let(() -> new ArrayList<>(asList("foo", "bar")));

      it("is a way to supply a value for specs", () -> {
        assertThat(items.get(), contains("foo", "bar"));
      });

      it("caches the value so it doesn't get created multiple times for the same spec", () -> {
        assertThat(items.get(), is(sameInstance(items.get())));

        items.get().add("baz");
        items.get().add("blah");
        assertThat(items.get(), contains("foo", "bar", "baz", "blah"));
      });

      it("creates a fresh value for every spec", () -> {
        assertThat(items.get(), contains("foo", "bar"));
      });

      describe("when trying to use a value outside a spec", () -> {

        final Supplier<Result> result = let(() -> {
          try {
            return helpers.SpectrumRunner.run(getSuiteThatUsesLetValueOutsideSpec());
          } catch (final Exception exception) {
            throw new RuntimeException(exception);
          }
        });

        it("causes a failure", () -> {
          assertThat(result.get().getFailureCount(), is(1));
        });

        it("describes the error", () -> {
          final Failure failure = result.get().getFailures().get(0);
          assertThat(failure.getException(), instanceOf(IllegalStateException.class));
          assertThat(failure.getMessage(),
              is("Cannot use the value from let() in a suite declaration. "
                  + "It may only be used in the context of a running spec."));
        });

      });

      describe("when errors happen in the supplier", () -> {

        describe("checked exceptions", () -> {

          it("should be wrapped in RuntimeException", () -> {
            final Result result = SpectrumRunner.run(getSuiteWithLetThatThrowsCheckedException());

            assertThat(result.getFailures(), hasSize(1));
            final Failure failure = result.getFailures().get(0);
            assertThat(failure.getException(), instanceOf(RuntimeException.class));
            assertThat(failure.getException().getCause(), instanceOf(DummyException.class));
          });

        });

        describe("runtime exceptions", () -> {

          it("should be re-thrown as-is", () -> {
            final Result result = SpectrumRunner.run(getSuiteWithLetThatThrowsRuntimeException());

            assertThat(result.getFailures(), hasSize(1));
            final Failure failure = result.getFailures().get(0);
            assertThat(failure.getException(), instanceOf(DummyRuntimeException.class));
            assertThat(failure.getException().getCause(), is(nullValue()));
          });

        });

        describe("errors", () -> {

          it("should be re-thrown as-is", () -> {
            final Result result = SpectrumRunner.run(getSuiteWithLetThatThrowsError());

            assertThat(result.getFailures(), hasSize(1));
            final Failure failure = result.getFailures().get(0);
            assertThat(failure.getException(), instanceOf(DummyError.class));
            assertThat(failure.getException().getCause(), is(nullValue()));
          });

        });

        describe("custom throwables", () -> {

          it("should be wrapped in RuntimeException", () -> {
            final Result result = SpectrumRunner.run(getSuiteWithLetThatThrowsCustomThrowable());

            assertThat(result.getFailures(), hasSize(1));
            final Failure failure = result.getFailures().get(0);
            assertThat(failure.getException(), instanceOf(RuntimeException.class));
            assertThat(failure.getException().getCause(), instanceOf(DummyThrowable.class));
          });

        });


      });
    });
  }

  private static Class<?> getSuiteThatUsesLetValueOutsideSpec() {
    class Suite {
      {
        describe("a thing", () -> {

          final Supplier<Integer> value = let(() -> 1);
          value.get();

          it("does stuff", () -> {
          });
          it("does more stuff", () -> {
          });

        });
      }
    }

    return Suite.class;
  }

  private static class DummyException extends Exception {
    private static final long serialVersionUID = 1L;
  }

  private static Class<?> getSuiteWithLetThatThrowsCheckedException() {
    class Suite {
      {
        describe("a thing", () -> {

          final Supplier<Object> dummy = let(() -> {
            throw new DummyException();
          });

          it("should fail", () -> {
            dummy.get();
          });

        });

      }
    }

    return Suite.class;
  }

  private static class DummyRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;
  }

  private static Class<?> getSuiteWithLetThatThrowsRuntimeException() {
    class Suite {
      {
        describe("a thing", () -> {

          final Supplier<Object> dummy = let(() -> {
            throw new DummyRuntimeException();
          });

          it("should fail", () -> {
            dummy.get();
          });

        });

      }
    }

    return Suite.class;
  }

  private static class DummyError extends Error {
    private static final long serialVersionUID = 1L;
  }

  private static Class<?> getSuiteWithLetThatThrowsError() {
    class Suite {
      {
        describe("a thing", () -> {

          final Supplier<Object> dummy = let(() -> {
            throw new DummyError();
          });

          it("should fail", () -> {
            dummy.get();
          });

        });

      }
    }

    return Suite.class;
  }

  private static class DummyThrowable extends Throwable {
    private static final long serialVersionUID = 1L;
  }

  private static Class<?> getSuiteWithLetThatThrowsCustomThrowable() {
    class Suite {
      {
        describe("a thing", () -> {

          final Supplier<Object> dummy = let(() -> {
            throw new DummyThrowable();
          });

          it("should fail", () -> {
            dummy.get();
          });

        });

      }
    }

    return Suite.class;
  }
}

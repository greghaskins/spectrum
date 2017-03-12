package specs;

import static com.greghaskins.spectrum.dsl.specification.Specification.afterEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.let;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.SpectrumHelper;

import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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

      describe("in complex test hierarchies", () -> {
        describe("a new let object is created for each spec", () -> {
          AtomicInteger integer = new AtomicInteger();
          describe("a thing", () -> {

            final Supplier<Integer> intLet = let(integer::getAndIncrement);

            it("starts with one value", () -> {
              assertThat(intLet.get(), is(0));

              // still the same inside this test
              assertThat(intLet.get(), is(0));
            });

            it("gets a second", () -> {
              assertThat(intLet.get(), is(1));
            });

            it("and another", () -> {
              assertThat(intLet.get(), is(2));
            });

            describe("a sub suite", () -> {
              it("gets another", () -> {
                assertThat(intLet.get(), is(3));
              });

              it("and another", () -> {
                assertThat(intLet.get(), is(4));
              });

              describe("and a sub suite of that", () -> {
                it("gets another", () -> {
                  assertThat(intLet.get(), is(5));
                });
              });
            });

          });

        });
      });

      describe("when trying to use a value outside a spec", () -> {

        final Supplier<Result> result =
            let(() -> SpectrumHelper.run(getSuiteThatUsesLetValueOutsideSpec()));

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
            final Result result = SpectrumHelper.run(getSuiteWithLetThatThrowsCheckedException());

            assertThat(result.getFailures(), hasSize(1));
            final Failure failure = result.getFailures().get(0);
            assertThat(failure.getException(), instanceOf(RuntimeException.class));
            assertThat(failure.getException().getCause(), instanceOf(DummyException.class));
          });

        });

        describe("runtime exceptions", () -> {

          it("should be re-thrown as-is", () -> {
            final Result result = SpectrumHelper.run(getSuiteWithLetThatThrowsRuntimeException());

            assertThat(result.getFailures(), hasSize(1));
            final Failure failure = result.getFailures().get(0);
            assertThat(failure.getException(), instanceOf(DummyRuntimeException.class));
            assertThat(failure.getException().getCause(), is(nullValue()));
          });

        });

        describe("errors", () -> {

          it("should be re-thrown as-is", () -> {
            final Result result = SpectrumHelper.run(getSuiteWithLetThatThrowsError());

            assertThat(result.getFailures(), hasSize(1));
            final Failure failure = result.getFailures().get(0);
            assertThat(failure.getException(), instanceOf(DummyError.class));
            assertThat(failure.getException().getCause(), is(nullValue()));
          });

        });

        describe("custom throwables", () -> {

          it("should be wrapped in RuntimeException", () -> {
            final Result result = SpectrumHelper.run(getSuiteWithLetThatThrowsCustomThrowable());

            assertThat(result.getFailures(), hasSize(1));
            final Failure failure = result.getFailures().get(0);
            assertThat(failure.getException(), instanceOf(RuntimeException.class));
            assertThat(failure.getException().getCause(), instanceOf(DummyThrowable.class));
          });

        });

        describe("state of let between specs", () -> {
          it("should not be preserved when a spec has an exception", () -> {
            final Result result = SpectrumHelper.run(getSuiteWithLetAndSpecThatThrowsError());

            assertThat(result.getFailures(), hasSize(1));
            final Failure failure = result.getFailures().get(0);
            assertThat(failure.getException(), instanceOf(RuntimeException.class));
            assertThat(failure.getException().getMessage(), is("Bong!"));
          });

          it("should not be preserved when after has an exception", () -> {
            final Result result = SpectrumHelper.run(getSuiteWithLetAndAfterThatThrowsError());

            assertThat(result.getFailures(), hasSize(2));
            assertThat(result.getFailures().get(0).getMessage(), is("Bong!"));
            assertThat(result.getFailures().get(1).getMessage(), is("Bong!"));
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

  private static Class<?> getSuiteWithLetAndSpecThatThrowsError() {
    class Suite {
      {
        describe("a thing", () -> {

          final Supplier<ArrayList<String>> list = let(ArrayList::new);

          it("has a failing spec which changes the let", () -> {
            list.get().add("hello world");
            throw new RuntimeException("Bong!");
          });

          it("has a spec which should still receive a fresh let", () -> {
            assertThat(list.get().isEmpty(), is(true));
          });

        });

      }
    }

    return Suite.class;
  }

  private static Class<?> getSuiteWithLetAndAfterThatThrowsError() {
    class Suite {
      {
        describe("a thing", () -> {

          final Supplier<ArrayList<String>> list = let(ArrayList::new);
          afterEach(() -> {
            throw new RuntimeException("Bong!");
          });

          it("has a spec which changes the let", () -> {
            list.get().add("hello world");
          });

          it("has a spec which should still receive a fresh let", () -> {
            assertThat(list.get().isEmpty(), is(true));
          });

        });

      }
    }

    return Suite.class;
  }
}

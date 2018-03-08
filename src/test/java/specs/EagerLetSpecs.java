package specs;

import static com.greghaskins.spectrum.dsl.specification.Specification.afterEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.beforeAll;
import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.eagerLet;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.let;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.SpectrumHelper;
import com.greghaskins.spectrum.Variable;

import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@RunWith(Spectrum.class)
public class EagerLetSpecs {
  {
    describe("The `eagerLet` helper function", () -> {
      final Supplier<List<String>> items = eagerLet(() -> new ArrayList<>(asList("foo", "bar")));

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

      context("when the value returned by the supplier is `null`", () -> {
        final AtomicInteger callCounter = new AtomicInteger();

        final Supplier<String> stringLet = eagerLet(() -> {
          if (callCounter.getAndIncrement() == 0) {
            return null;
          } else {
            return "fail";
          }
        });

        it("does not call the supplier multiple times", () -> {
          assertThat(stringLet.get(), is(nullValue()));
          assertThat(stringLet.get(), is(nullValue()));
        });
      });

      describe("in complex test hierarchies", () -> {
        describe("a new eager let object is created for each spec", () -> {
          AtomicInteger integer = new AtomicInteger();

          describe("a thing", () -> {
            final Supplier<Integer> intLet = eagerLet(integer::getAndIncrement);

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

      describe("eager initialization", () -> {
        final Supplier<List<String>> eagerItemsCopy =
            eagerLet(() -> new ArrayList<>(items.get()));

        context("when `beforeEach`, `let`, and `eagerLet` are used", () -> {
          final Supplier<List<String>> lazyItemsCopy =
              let(() -> new ArrayList<>(items.get()));

          beforeEach(() -> {
            // This would throw a NullPointerException if it ran before eagerItems
            items.get().add("baz");
          });

          it("evaluates all `eagerLet` blocks at once", () -> {
            assertThat(eagerItemsCopy.get(), contains("foo", "bar"));
          });

          it("evaluates `beforeEach` after `eagerLet`", () -> {
            assertThat(items.get(), contains("foo", "bar", "baz"));
          });

          it("evaluates `let` upon first use", () -> {
            assertThat(lazyItemsCopy.get(), contains("foo", "bar", "baz"));
          });
        });

        context("when `beforeAll` and `eagerLet` are used", () -> {
          beforeAll(() -> {
            assertThat(items.get(), is(nullValue()));
            assertThat(eagerItemsCopy.get(), is(nullValue()));
          });

          it("evaluates `beforeAll` prior to `eagerLet`", () -> {
            assertThat(items.get(), is(not(nullValue())));
            assertThat(eagerItemsCopy.get(), is(not(nullValue())));
          });
        });
      });

      describe("when trying to use a value outside a spec", () -> {
        final Supplier<Result> result =
            eagerLet(() -> SpectrumHelper.run(getSuiteThatUsesEagerLetValueOutsideSpec()));

        it("causes a failure", () -> {
          assertThat(result.get().getFailureCount(), is(1));
        });

        it("describes the error", () -> {
          final Failure failure = result.get().getFailures().get(0);
          assertThat(failure.getException(), instanceOf(IllegalStateException.class));
          assertThat(failure.getMessage(),
              is("Cannot use the value from eagerLet() in a suite declaration. "
                  + "It may only be used in the context of a running spec."));
        });

      });

      describe("when errors happen in the supplier", () -> {
        describe("checked exceptions", () -> {
          it("should be wrapped in RuntimeException", () -> {
            final Result result = SpectrumHelper.run(getSuiteWithEagerLetThatThrowsCheckedException());

            assertThat(result.getFailures(), hasSize(1));
            final Failure failure = result.getFailures().get(0);
            assertThat(failure.getException(), instanceOf(RuntimeException.class));
            assertThat(failure.getException().getCause(), instanceOf(DummyException.class));
          });

        });

        describe("runtime exceptions", () -> {
          it("should be re-thrown as-is", () -> {
            final Result result = SpectrumHelper.run(getSuiteWithEagerLetThatThrowsRuntimeException());

            assertThat(result.getFailures(), hasSize(1));
            final Failure failure = result.getFailures().get(0);
            assertThat(failure.getException(), instanceOf(DummyRuntimeException.class));
            assertThat(failure.getException().getCause(), is(nullValue()));
          });
        });

        describe("errors", () -> {
          it("should be re-thrown as-is", () -> {
            final Result result = SpectrumHelper.run(getSuiteWithEagerLetThatThrowsError());

            assertThat(result.getFailures(), hasSize(1));
            final Failure failure = result.getFailures().get(0);
            assertThat(failure.getException(), instanceOf(DummyError.class));
            assertThat(failure.getException().getCause(), is(nullValue()));
          });
        });

        describe("custom throwables", () -> {
          it("should be wrapped in RuntimeException", () -> {
            final Result result = SpectrumHelper.run(getSuiteWithEagerLetThatThrowsCustomThrowable());

            assertThat(result.getFailures(), hasSize(1));
            final Failure failure = result.getFailures().get(0);
            assertThat(failure.getException(), instanceOf(RuntimeException.class));
            assertThat(failure.getException().getCause(), instanceOf(DummyThrowable.class));
          });
        });

        describe("state of eager let between specs", () -> {
          it("should not be preserved when a spec has an exception", () -> {
            final Result result = SpectrumHelper.run(getSuiteWithEagerLetAndSpecThatThrowsError());

            assertThat(result.getFailures(), hasSize(1));
            final Failure failure = result.getFailures().get(0);
            assertThat(failure.getException(), instanceOf(RuntimeException.class));
            assertThat(failure.getException().getMessage(), is("Bong!"));
          });

          it("should not be preserved when after has an exception", () -> {
            final Result result = SpectrumHelper.run(getSuiteWithEagerLetAndAfterThatThrowsError());

            assertThat(result.getFailures(), hasSize(2));
            assertThat(result.getFailures().get(0).getMessage(), is("Bong!"));
            assertThat(result.getFailures().get(1).getMessage(), is("Bong!"));
          });
        });
      });

      describe("eager let across multiple threads", () -> {
        final Supplier<List<String>> listSupplier = eagerLet(ArrayList::new);
        it("can share the object with worker thread", () -> {
          // when the supplier's object has something added to it
          listSupplier.get().add("Hello world");

          // used as a box for the integer
          AtomicInteger atomicInteger = new AtomicInteger();

          // when we access the object within a worker thread
          Thread thread = new Thread(() -> atomicInteger.set(listSupplier.get().size()));
          thread.start();
          thread.join();

          // then the worker thread saw the same object as the outer thread
          // then the worker thread saw the same object as the outer thread
          assertThat(atomicInteger.get(), is(1));
        });
      });
    });
  }

  private static Class<?> getSuiteThatUsesEagerLetValueOutsideSpec() {
    class Suite {
      {
        describe("a thing", () -> {

          final Supplier<Integer> value = eagerLet(() -> 1);
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

  private static Class<?> getSuiteWithEagerLetThatThrowsCheckedException() {
    class Suite {
      {
        describe("a thing", () -> {

          final Supplier<Object> dummy = eagerLet(() -> {
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

  private static Class<?> getSuiteWithEagerLetThatThrowsRuntimeException() {
    class Suite {
      {
        describe("a thing", () -> {

          final Supplier<Object> dummy = eagerLet(() -> {
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

  private static Class<?> getSuiteWithEagerLetThatThrowsError() {
    class Suite {
      {
        describe("a thing", () -> {
          final Supplier<Object> dummy = eagerLet(() -> {
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

  private static Class<?> getSuiteWithEagerLetThatThrowsCustomThrowable() {
    class Suite {
      {
        describe("a thing", () -> {
          final Supplier<Object> dummy = eagerLet(() -> {
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

  private static Class<?> getSuiteWithEagerLetAndSpecThatThrowsError() {
    class Suite {
      {
        describe("a thing", () -> {
          final Supplier<ArrayList<String>> list = eagerLet(ArrayList::new);

          it("has a failing spec which changes the eager let", () -> {
            list.get().add("hello world");
            throw new RuntimeException("Bong!");
          });

          it("has a spec which should still receive a fresh eager let", () -> {
            assertThat(list.get().isEmpty(), is(true));
          });
        });
      }
    }

    return Suite.class;
  }

  private static Class<?> getSuiteWithEagerLetAndAfterThatThrowsError() {
    class Suite {
      {
        describe("a thing", () -> {

          final Supplier<ArrayList<String>> list = eagerLet(ArrayList::new);
          afterEach(() -> {
            throw new RuntimeException("Bong!");
          });

          it("has a spec which changes the eager let", () -> {
            list.get().add("hello world");
          });

          it("has a spec which should still receive a fresh eager let", () -> {
            assertThat(list.get().isEmpty(), is(true));
          });
        });
      }
    }

    return Suite.class;
  }
}

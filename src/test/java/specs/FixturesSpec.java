package specs;

import static com.greghaskins.spectrum.Spectrum.afterAll;
import static com.greghaskins.spectrum.Spectrum.afterEach;
import static com.greghaskins.spectrum.Spectrum.beforeAll;
import static com.greghaskins.spectrum.Spectrum.beforeEach;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.Spectrum.let;
import static com.greghaskins.spectrum.internal.PreConditionBlock.ignore;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Spectrum.ThrowingSupplier;
import com.greghaskins.spectrum.SpectrumHelper;

import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RunWith(Spectrum.class)
public class FixturesSpec {
  {

    describe("A spec using beforeEach and afterEach", () -> {

      final List<String> items = new ArrayList<>();

      beforeEach(() -> {
        items.add("foo");
      });

      afterEach(() -> {
        items.clear();
      });

      it("runs beforeEach before every test", () -> {
        assertThat(items, contains("foo"));
        items.add("bar");
      });

      it("runs afterEach after every test", () -> {
        assertThat(items, contains("foo"));
        assertThat(items, not(contains("bar")));
      });

      describe("nested inside another describe", () -> {

        beforeEach(() -> {
          items.add("baz");
        });

        it("is run before tests in that context", () -> {
          assertThat(items, contains("foo", "baz"));
          items.clear();
        });

        it("run in addition to the beforeEach in the parent scope", () -> {
          assertThat(items, contains("foo", "baz"));
        });

      });

    });

    describe("Multiple beforeEach and afterEach blocks", () -> {

      final List<String> words = new ArrayList<>();
      final ArrayList<Integer> numbers = new ArrayList<>();

      afterEach(() -> {
        words.clear();
      });

      beforeEach(() -> {
        words.add("foo");
      });

      afterEach(() -> {
        numbers.clear();
      });

      beforeEach(() -> {
        numbers.add(1);
      });

      it("run in order before the first test", () -> {
        assertThat(words, contains("foo", "bar"));
      });

      it("and before the other tests", () -> {
        assertThat(words, contains("foo", "bar"));
      });

      describe("even with a nested context", () -> {

        beforeEach(() -> {
          numbers.add(3);
        });

        it("all run before each test in declaration order", () -> {
          assertThat(numbers, contains(1, 2, 3, 4));
        });

        it("runs afterEach blocks from parent context", () -> {
          assertThat(numbers, contains(1, 2, 3, 4));
        });

        beforeEach(() -> {
          numbers.add(4);
        });

      });

      beforeEach(() -> {
        words.add("bar");
      });

      beforeEach(() -> {
        numbers.add(2);
      });

    });

    describe("A suite using beforeAll", () -> {

      final List<String> items = new ArrayList<>();

      beforeAll(() -> {
        items.add("foo");
      });

      beforeAll(() -> {
        items.add("bar");
      });

      it("sets the initial state before the tests run", () -> {
        assertThat(items, contains("foo", "bar"));
        items.add("baz");
      });

      it("does't reset any state between tests", () -> {
        assertThat(items, contains("foo", "bar", "baz"));
      });

      describe("with nested children", () -> {

        final ArrayList<Integer> numbers = new ArrayList<>();

        beforeAll(() -> {
          numbers.add(1);
        });

        describe("inside suites without their own tests", () -> {

          beforeAll(() -> {
            numbers.add(2);
          });

          it("runs the beforeAll blocks from outer scope first", () -> {
            assertThat(numbers, contains(1, 2));
          });

        });

      });

    });

    describe("A spec using afterAll", () -> {

      final List<String> items = new ArrayList<>();
      final List<Integer> numbers = new ArrayList<>();

      describe("with some tests", () -> {

        afterAll(() -> {
          items.clear();
        });

        afterAll(() -> {
          numbers.add(5);
        });

        it("sets the initial state before tests run", () -> {
          assertThat(items, hasSize(0));
          items.add("foo");
        });

        it("doesn't reset any state between tests", () -> {
          assertThat(items, contains("foo"));
          items.add("bar");
        });

      });

      it("runs afterAll blocks after all the tests in a context", () -> {
        assertThat(items, hasSize(0));
        assertThat(numbers, contains(5));
      });

    });

    describe("A beforeEach block that explodes", () -> {

      it("causes all tests in that context to fail", () -> {
        final Result result = SpectrumHelper.run(getSpecWithExplodingBeforeEach());
        assertThat(result.getFailureCount(), is(2));
      });

    });

    describe("An afterEach block that explodes", () -> {

      it("causes all tests in that context to fail", () -> {
        final Result result = SpectrumHelper.run(getSpecWithExplodingAfterEach());
        assertThat(result.getFailureCount(), is(2));
      });

    });

    describe("beforeAll blocks that explode", () -> {

      it("cause all tests in that context and its children to fail", () -> {
        final Result result = SpectrumHelper.run(getSpecWithExplodingBeforeAll());
        assertThat(result.getFailureCount(), is(3));
      });

    });

    describe("A suite with no specs", () -> {

      final List<String> items = new ArrayList<>();

      beforeEach(() -> {
        SpectrumHelper.run(() -> {

          final Block addItem = () -> {
            items.add("foo");
          };

          describe("suite without specs", () -> {
            beforeAll(addItem);
            beforeEach(addItem);
            afterEach(addItem);
            afterAll(addItem);
          });

        });
      });

      it("does not run fixture methods", () -> {
        assertThat(items, hasSize(0));
      });

    });

    describe("afterEach blocks", () -> {

      describe("when a spec explodes", () -> {

        it("still run", () -> {
          final Result result = SpectrumHelper.run(getSuiteWithExplodingSpec());
          assertThat(result.getFailureCount(), is(1));
          assertThat(result.getFailures().get(0).getMessage(), containsString("boom"));
        });

      });

      describe("when another afterEach explodes", () -> {

        it("still run, too", () -> {
          final Result result = SpectrumHelper.run(getSuiteWithExplodingAndNonExplodingAfterEach());
          assertThat(result.getFailureCount(), is(1));
          assertThat(result.getFailures().get(0).getMessage(), containsString("boom"));
        });

      });

      final ArrayList<String> items = new ArrayList<>();

      describe("in multiples", () -> {

        it("run in reverse order", () -> {
          assertThat(items, hasSize(0));
        });

        afterEach(() -> {
          items.add("after1");
        });
        afterEach(() -> {
          items.add("after2");
        });
        afterEach(() -> {
          items.add("after3");
        });

      });

      it("run in reverse declaration order", () -> {
        assertThat(items, contains("after3", "after2", "after1"));
      });

    });

    describe("afterAll blocks", () -> {

      final Supplier<List<String>> calls = let(() -> new ArrayList<>());

      describe("that explode", () -> {

        final Supplier<Result> result = let(() -> SpectrumHelper.run(() -> {

          describe("context description", () -> {

            afterAll(() -> {
              throw new Exception();
            });

            it("spec that passes", () -> {
              assertThat(true, is(true));
            });

          });

        }));

        it("cause a failure", () -> {
          assertThat(result.get().getFailureCount(), is(1));
        });

        it("associate the failure with the declaring suite", () -> {
          final Failure failure = result.get().getFailures().get(0);
          assertThat(failure.getDescription().getClassName(), is("context description"));
          assertThat(failure.getDescription().getMethodName(), is(nullValue()));
        });

      });

      describe("when a spec explodes", () -> {

        beforeEach(() -> SpectrumHelper.run(() -> {
          describe("context desecription", () -> {

            afterAll(() -> {
              calls.get().add("afterAll");
            });

            it("failing spec", () -> {
              throw new Exception();
            });

          });
        }));

        it("still run", () -> {
          assertThat(calls.get(), hasItem("afterAll"));
        });

      });

      describe("when an afterEach explodes", () -> {

        beforeEach(() -> SpectrumHelper.run(() -> {
          describe("context", () -> {

            afterEach(() -> {
              calls.get().add("afterEach");
              throw new Exception();
            });

            afterAll(() -> {
              calls.get().add("afterAll");
            });

            it("passing spec", () -> {
              calls.get().add("spec");
            });

          });
        }));

        it("still run", () -> {
          assertThat(calls.get(), hasItem("afterAll"));
        });

      });

      describe("when another afterAll explodes", () -> {

        beforeEach(() -> SpectrumHelper.run(() -> {
          describe("context", () -> {

            afterAll(() -> {
              calls.get().add("afterAll 1");
              throw new Exception();
            });

            afterAll(() -> {
              calls.get().add("afterAll 2");
              throw new Exception();
            });

            it("passing spec", () -> {
              calls.get().add("spec");
            });

          });
        }));

        it("still run", () -> {
          assertThat(calls.get(), hasItem("afterAll 1"));
          assertThat(calls.get(), hasItem("afterAll 2"));
        });

      });

      describe("in multiples", () -> {

        beforeEach(() -> SpectrumHelper.run(() -> {
          describe("context", () -> {

            afterAll(() -> {
              calls.get().add("afterAll 1");
            });

            afterAll(() -> {
              calls.get().add("afterAll 2");
            });

            afterAll(() -> {
              calls.get().add("afterAll 3");
            });

            it("passing spec", () -> {
              assertThat(true, is(true));
            });

          });
        }));

        it("run in reverse declaration order", () -> {
          assertThat(calls.get(), contains("afterAll 3", "afterAll 2", "afterAll 1"));
        });

        describe("that explode", () -> {

          final Supplier<Result> result = let(() -> SpectrumHelper.run(() -> {
            describe("context description", () -> {

              afterAll(() -> {
                throw new Exception("boom 1");
              });

              afterAll(() -> {
                throw new Exception("boom 2");
              });

              it("no boom", () -> {
                assertThat(true, is(true));
              });

            });
          }));

          final Supplier<List<String>> failureMessages = let(() -> result.get()
              .getFailures()
              .stream()
              .map((failure) -> failure.getMessage())
              .collect(Collectors.toList()));

          it("report the error only once", () -> {
            assertThat(result.get().getFailureCount(), is(1));

            assertThat(failureMessages.get(), hasItem("java.lang.Exception: boom 1"));
          });

        });

      });

    });

    describe("Fixtures with multiple errors", () -> {

      final Function<Supplier<Result>, ThrowingSupplier<List<String>>> getFailureMessages =
          (result) -> () -> result.get()
              .getFailures()
              .stream()
              .map((failure) -> failure.getMessage())
              .collect(Collectors.toList());

      describe("in beforeEach and afterEach", () -> {

        final Supplier<List<String>> exceptionsThrown = let(() -> new ArrayList<>());

        final Function<Throwable, Throwable> recordException = (throwable) -> {
          exceptionsThrown.get().add("java.lang.Exception: " + throwable.getMessage());

          return throwable;
        };

        final Supplier<Result> result = let(() -> SpectrumHelper.run(() -> {
          describe("an explosive suite", () -> {

            beforeEach(() -> {
              throw recordException.apply(new Exception("boom beforeEach 1"));
            });
            beforeEach(() -> {
              throw recordException.apply(new Exception("boom beforeEach 2"));
            });

            afterEach(() -> {
              throw recordException.apply(new Exception("boom afterEach 1"));
            });
            afterEach(() -> {
              throw recordException.apply(new Exception("boom afterEach 2"));
            });

            it("explodes", () -> {
              throw recordException.apply(new Exception("boom in spec"));
            });
          });
        }));

        final Supplier<List<String>> failureMessages = let(getFailureMessages.apply(result));

        it("should stop running beforeEach blocks after the first error", () -> {
          assertThat(failureMessages.get(), hasItem("java.lang.Exception: boom beforeEach 1"));
          assertThat(failureMessages.get(), not(hasItem("java.lang.Exception: boom beforeEach 2")));
        });

        it("should not run any specs", () -> {
          assertThat(exceptionsThrown.get(), not(hasItem("spec")));
        });

        it("should report all errors individually", ignore("To discuss", () -> {
          assertThat(failureMessages.get(),
              contains(
                  "java.lang.Exception: boom beforeEach 1",
                  "java.lang.Exception: boom afterEach 2",
                  "java.lang.Exception: boom afterEach 1"));
        }));

        it("should report all exceptions as failures", () -> {
          assertThat(failureMessages.get(), contains(exceptionsThrown.get().toArray()));
        });

      });

      describe("in beforeAll and afterAll", () -> {

        final Supplier<List<String>> exceptionsThrown = let(() -> new ArrayList<>());

        final Function<Throwable, Throwable> recordException = (throwable) -> {
          exceptionsThrown.get().add("java.lang.Exception: " + throwable.getMessage());

          return throwable;
        };

        final Supplier<Result> result = let(() -> SpectrumHelper.run(() -> {
          describe("an explosive suite", () -> {

            beforeAll(() -> {
              throw recordException.apply(new Exception("boom beforeAll 1"));
            });
            beforeAll(() -> {
              throw recordException.apply(new Exception("boom beforeAll 2"));
            });


            beforeEach(() -> {
              throw recordException.apply(new Exception("boom beforeEach"));
            });

            afterEach(() -> {
              throw recordException.apply(new Exception("boom afterEach"));
            });


            afterAll(() -> {
              throw recordException.apply(new Exception("boom afterAll 1"));
            });
            afterAll(() -> {
              throw recordException.apply(new Exception("boom afterAll 2"));
            });


            it("explodes", () -> {
              throw recordException.apply(new Exception("boom in spec"));
            });
          });
        }));

        final Supplier<List<String>> failureMessages = let(getFailureMessages.apply(result));

        it("stop running beforeAll blocks after the first error", () -> {
          assertThat(failureMessages.get(), hasItem("java.lang.Exception: boom beforeAll 1"));
          assertThat(failureMessages.get(), not(hasItem("java.lang.Exception: boom beforeAll 2")));
        });

        it("does not run beforeEach", () -> {
          assertThat(failureMessages.get(), not(hasItem("boom beforeEach")));
        });

        it("does not run afterEach", () -> {
          assertThat(failureMessages.get(), not(hasItem("boom afterEach")));
        });

        it("does not run any specs", () -> {
          assertThat(exceptionsThrown.get(), not(hasItem("boom in spec")));
        });

        it("should report all errors individually", ignore("To discuss", () -> {
          assertThat(failureMessages.get(),
              contains(
                  "java.lang.Exception: boom beforeAll 1",
                  "java.lang.Exception: boom afterAll 2",
                  "java.lang.Exception: boom afterAll 1"));
        }));

        it("should report all exceptions as failures", ignore("To discuss", () -> {
          assertThat(failureMessages.get(), contains(exceptionsThrown.get().toArray()));
        }));

      });
    });

  }

  private static Class<?> getSpecWithExplodingBeforeEach() {
    class Spec {
      {
        beforeEach(() -> {
          throw new Exception("boom");
        });

        it("should fail", () -> {

        });

        it("should also fail", () -> {

        });

      }
    }

    return Spec.class;
  }

  private static Class<?> getSpecWithExplodingAfterEach() {
    class Spec {
      {
        afterEach(() -> {
          throw new Exception("boom");
        });

        it("should fail", () -> {

        });

        it("should also fail", () -> {

        });

      }
    }

    return Spec.class;
  }

  private static Class<?> getSpecWithExplodingBeforeAll() {
    class Spec {
      {

        final ArrayList<String> executedSpecs = new ArrayList<String>();

        describe("failing context", () -> {

          beforeAll(() -> {
            throw new Exception("boom");
          });

          beforeAll(() -> {
            throw new Exception("boom two");
          });

          it("should fail once", () -> {
            executedSpecs.add("foo");
          });

          it("should also fail", () -> {
            executedSpecs.add("bar");
          });

          describe("failing child", () -> {

            it("fails too", () -> {
              executedSpecs.add("baz");
            });

          });
        });

        it("should not execute any specs", () -> {
          assertThat(executedSpecs, is(empty()));
        });

      }
    }

    return Spec.class;
  }

  private static Class<?> getSuiteWithExplodingSpec() {

    class Suite {
      {
        describe("suite with exploding spec", () -> {

          final ArrayList<String> items = new ArrayList<>();

          describe("boom", () -> {
            it("explodes", () -> {
              items.add("foo");
              throw new Exception("boom");
            });

            afterEach(() -> {
              items.clear();
            });
          });

          it("should still run afterEach blocks", () -> {
            assertThat(items, hasSize(0));
          });

        });
      }
    }

    return Suite.class;
  }

  private static Class<?> getSuiteWithExplodingAndNonExplodingAfterEach() {

    class Suite {
      {
        describe("suite with exploding spec", () -> {

          final ArrayList<String> items = new ArrayList<>();

          describe("boom", () -> {
            it("explodes", () -> {
              items.add("foo");
            });

            afterEach(() -> {
              throw new Exception("boom");
            });

            afterEach(() -> {
              items.clear();
            });
          });

          it("should still run afterEach blocks", () -> {
            assertThat(items, hasSize(0));
          });

        });
      }
    }

    return Suite.class;

  }
}

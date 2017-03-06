package specs;

import static com.greghaskins.spectrum.dsl.spec.Spec.afterAll;
import static com.greghaskins.spectrum.dsl.spec.Spec.afterEach;
import static com.greghaskins.spectrum.dsl.spec.Spec.beforeAll;
import static com.greghaskins.spectrum.dsl.spec.Spec.beforeEach;
import static com.greghaskins.spectrum.dsl.spec.Spec.context;
import static com.greghaskins.spectrum.dsl.spec.Spec.describe;
import static com.greghaskins.spectrum.dsl.spec.Spec.fcontext;
import static com.greghaskins.spectrum.dsl.spec.Spec.it;
import static com.greghaskins.spectrum.dsl.spec.Spec.xcontext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.SpectrumHelper;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(Spectrum.class)
public class ExampleSpecs {
  {

    describe("A spec", () -> {

      final int foo = 1;

      it("is just a code block that verifies something", () -> {
        assertEquals(1, foo);
      });

      it("can use any assertion library you like", () -> {
        org.junit.Assert.assertEquals(1, foo);
        org.hamcrest.MatcherAssert.assertThat(true, is(true));
      });

      describe("nested inside a second describe", () -> {

        final int bar = 1;

        it("can reference both scopes as needed", () -> {
          assertThat(bar, is(equalTo(foo)));
        });

      });

      it("can have `it`s and `describe`s in any order", () -> {
        assertThat(foo, is(1));
      });

    });

    describe("A suite using beforeEach and afterEach", () -> {

      final List<String> items = new ArrayList<>();

      beforeEach(() -> {
        items.add("foo");
      });

      beforeEach(() -> {
        items.add("bar");
      });

      afterEach(() -> {
        items.clear();
      });

      it("runs the beforeEach() blocks in order", () -> {
        assertThat(items, contains("foo", "bar"));
        items.add("bogus");
      });

      it("runs them before every spec", () -> {
        assertThat(items, contains("foo", "bar"));
        items.add("bogus");
      });

      it("runs afterEach after every spec", () -> {
        assertThat(items, not(contains("bogus")));
      });

      describe("when nested", () -> {

        beforeEach(() -> {
          items.add("baz");
        });

        it("runs beforeEach and afterEach from inner and outer scopes", () -> {
          assertThat(items, contains("foo", "bar", "baz"));
        });

      });

    });

    describe("A suite using beforeAll", () -> {

      final List<Integer> numbers = new ArrayList<>();

      beforeAll(() -> {
        numbers.add(1);
      });

      it("sets the initial state before any specs run", () -> {
        assertThat(numbers, contains(1));
        numbers.add(2);
      });

      describe("and afterAll", () -> {

        afterAll(() -> {
          numbers.clear();
        });

        it("does not reset anything between tests", () -> {
          assertThat(numbers, contains(1, 2));
          numbers.add(3);
        });

        it("so proceed with caution; this *will* leak shared state across specs", () -> {
          assertThat(numbers, contains(1, 2, 3));
        });
      });

      it("cleans up after running all specs in the describe block", () -> {
        assertThat(numbers, is(empty()));
      });

    });

    describe("a spec can have a context", () -> {
      context("surrounding the specs", () -> {
        it("helping to document the specification", () -> {
        });
      });
    });

    describe("selective running", () -> {
      context("with contexts", () -> {
        it("focusing is possible", ExampleSpecs::focusingContextExample);
        it("ignoring is possible", ExampleSpecs::ignoringContextExample);
      });
    });
  }

  private static void focusingContextExample() throws Exception {
    class Example {
      {
        describe("a spec with", () -> {
          fcontext("a focused context", () -> {
            it("runs the spec", () -> {
            });
          });

          it("doesn't run this one", () -> {
          });
        });
      }
    }

    final Result result = SpectrumHelper.run(Example.class);
    Assert.assertThat(result.getRunCount(), Is.is(1));
  }

  private static void ignoringContextExample() throws Exception {
    class Example {
      {
        describe("a spec with", () -> {
          xcontext("an ignored context", () -> {
            it("does not run the spec", () -> {
            });
          });
        });
      }
    }

    final Result result = SpectrumHelper.run(Example.class);
    Assert.assertThat(result.getIgnoreCount(), Is.is(1));
  }

}

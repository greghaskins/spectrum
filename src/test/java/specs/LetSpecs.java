package specs;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.Spectrum.let;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import com.greghaskins.spectrum.Spectrum;

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
}

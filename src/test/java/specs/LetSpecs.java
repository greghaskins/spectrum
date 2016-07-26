package specs;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.Spectrum.let;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import com.greghaskins.spectrum.Spectrum;

import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@RunWith(Spectrum.class)
public class LetSpecs {
  {
    describe("The `let` helper function", () -> {

      final Supplier<List<String>> words = let(() -> new ArrayList<>(asList("foo", "bar")));

      it("is a way to supply a value for specs", () -> {
        assertThat(words.get(), contains("foo", "bar"));
      });

      it("caches the value so it doesn't get created multiple times", () -> {
        words.get().add("baz");
        words.get().add("blah");
        assertThat(words.get(), is(sameInstance(words.get())));
        assertThat(words.get(), contains("foo", "bar", "baz", "blah"));
      });

      it("creates a fresh value for every spec", () -> {
        assertThat(words.get(), contains("foo", "bar"));
      });
    });
  }
}

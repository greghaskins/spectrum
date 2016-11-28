package specs;

import static com.greghaskins.spectrum.Spectrum.beforeEach;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Variable;

import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
public class VariableSpecs {
  {

    describe("The Variable convenience wrapper", () -> {

      final Variable<Integer> counter = new Variable<>();

      beforeEach(() -> {
        counter.set(0);
      });

      beforeEach(() -> {
        counter.set(counter.get() + 1);
      });

      it("lets you work around Java's requirement that closures only use `final` variables", () -> {
        counter.set(counter.get() + 1);
        assertThat(counter.get(), is(2));
      });

      it("can optionally have an initial value set", () -> {
        final Variable<String> name = new Variable<>("Alice");
        assertThat(name.get(), is("Alice"));
      });

      it("has a null value if not specified", () -> {
        final Variable<String> name = new Variable<>();
        assertNull(name.get());
      });

    });

  }
}

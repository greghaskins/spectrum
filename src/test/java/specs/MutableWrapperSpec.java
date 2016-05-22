package specs;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.Spectrum.value;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Spectrum.Value;

import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
public class MutableWrapperSpec {
  {

    describe("The Value convenience type", () -> {

      it("allows you to set the value of a 'final' variable", () -> {
        final Value<Integer> counter = value(Integer.class);
        counter.value = 0;
        counter.value = 1;
        assertThat(counter.value, is(1));
      });

      it("can be given a starting value", () -> {
        final Value<Double> pi = value(3.14);
        assertThat(pi.value, is(3.14));
      });

      it("has a default value of null if not specified", () -> {
        final Value<String> name = value(String.class);
        assertThat(name.value, is(nullValue()));
      });

    });
  }
}

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
@SuppressWarnings("deprecation")
public class MutableWrapperSpec {
  {

    describe("The Value convenience type", () -> {

      it("allows you to set the value of a 'final' variable", () -> {
        final Value<Integer> counter = value();
        counter.value = 0;
        counter.value = 1;
        assertThat(counter.value, is(1));
      });

      it("can be given a starting value", () -> {
        final Value<Double> pi = value(3.14);
        assertThat(pi.value, is(3.14));
      });

      it("has a default value of null if not specified", () -> {
        final Value<String> name = value();
        assertThat(name.value, is(nullValue()));
      });

      it("can take an explicit class parameter, for backward compatibility with ~0.6.1", () -> {
        final Value<String> stringValue = value(String.class);
        assertThat(stringValue.value, is(nullValue()));
      });

    });
  }
}

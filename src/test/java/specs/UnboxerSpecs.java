package specs;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.Spectrum.let;
import static com.greghaskins.spectrum.Unboxer.unbox;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Variable;

import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(Spectrum.class)
public class UnboxerSpecs {
  {
    describe("Using unboxer on let", () -> {
      List<String> list = unbox(let(ArrayList::new), List.class);

      it("can use the object as though it was not in a supplier", () -> {
        list.add("Hello");
        assertThat(list.get(0), is("Hello"));
      });

      it("can use multi-parameter methods correctly", () -> {
        list.add("a");
        list.add("b");
        list.add(0, "_");

        assertThat(list.size(), is(3));
        assertThat(list.get(0), is("_"));
      });
    });

    describe("Using unboxer with variable", () -> {
      Variable<ArrayList<String>> listVariable = new Variable<>(new ArrayList<>());
      List<String> list = unbox(listVariable, List.class);

      it("can read the variable contents", () -> {
        list.add("World");
        assertThat(list.size(), is(1));
        assertThat(list.get(0), is("World"));
      });

      it("can still read the same list in the next spec", () -> {
        assertThat(list.size(), is(1));
      });

      it("can reset the content via the original variable object", () -> {
        listVariable.set(new ArrayList<>());
        assertThat(list.size(), is(0));
      });
    });
  }
}

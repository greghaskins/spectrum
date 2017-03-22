package specs;

import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.and;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.feature;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.given;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.scenario;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.then;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.when;
import static com.greghaskins.spectrum.dsl.specification.Specification.afterEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Variable;

import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(Spectrum.class)
public class ReadmeSpecs {

  {
    describe("A list", () -> {

      List<String> list = new ArrayList<>();

      afterEach(list::clear);

      it("should be empty by default", () -> {
        assertThat(list.size(), is(0));
      });

      it("should be able to add items", () -> {
        list.add("foo");
        list.add("bar");

        assertThat(list, contains("foo", "bar"));
      });

    });

    feature("Lists", () -> {

      scenario("adding items", () -> {

        Variable<List<String>> list = new Variable<>();

        given("an empty list", () -> {
          list.set(new ArrayList<>());
        });

        when("you add the item 'foo'", () -> {
          list.get().add("foo");
        });

        and("you add the item 'bar'", () -> {
          list.get().add("bar");
        });

        then("it contains both foo and bar", () -> {
          assertThat(list.get(), contains("foo", "bar"));
        });

      });

    });
  }
}

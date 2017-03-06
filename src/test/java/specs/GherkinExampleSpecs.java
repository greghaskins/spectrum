package specs;

import static com.greghaskins.spectrum.dsl.gherkin.GherkinSyntax.and;
import static com.greghaskins.spectrum.dsl.gherkin.GherkinSyntax.feature;
import static com.greghaskins.spectrum.dsl.gherkin.GherkinSyntax.given;
import static com.greghaskins.spectrum.dsl.gherkin.GherkinSyntax.scenario;
import static com.greghaskins.spectrum.dsl.gherkin.GherkinSyntax.then;
import static com.greghaskins.spectrum.dsl.gherkin.GherkinSyntax.when;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Variable;

import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;

@RunWith(Spectrum.class)
public class GherkinExampleSpecs {
  {
    feature("Gherkin-like test DSL", () -> {

      scenario("using given-when-then steps", () -> {
        final AtomicInteger integer = new AtomicInteger();
        given("we start with a given", () -> {
          integer.set(12);
        });
        when("we have a when to execute the system", () -> {
          integer.incrementAndGet();
        });
        then("we can assert the outcome", () -> {
          assertThat(integer.get(), is(13));
        });
      });

      scenario("using variables within the scenario to pass data between steps", () -> {
        final Variable<String> theData = new Variable<>();

        given("the data is set", () -> {
          theData.set("Hello");
        });

        when("the data is modified", () -> {
          theData.set(theData.get() + " world!");
        });

        then("the data can be seen with the new value", () -> {
          assertThat(theData.get(), is("Hello world!"));
        });

        and("the data is still available in subsequent steps", () -> {
          assertThat(theData.get(), is("Hello world!"));
        });
      });

    });
  }
}

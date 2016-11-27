package specs;

import static com.greghaskins.spectrum.GherkinSyntax.and;
import static com.greghaskins.spectrum.GherkinSyntax.feature;
import static com.greghaskins.spectrum.GherkinSyntax.given;
import static com.greghaskins.spectrum.GherkinSyntax.scenario;
import static com.greghaskins.spectrum.GherkinSyntax.then;
import static com.greghaskins.spectrum.GherkinSyntax.when;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Variable;

import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates the BDD syntax of Spectrum.
 */
@RunWith(Spectrum.class)
public class GherkinExampleSpecs {
  {
    feature("BDD", () -> {
      scenario("allow Gherkin like syntax", () -> {
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

      scenario("uses boxes within the scenario where data is passed between steps", () -> {
        Variable<String> theData = new Variable<>();

        given("the data is set", () -> {
          theData.set("Hello world");
        });

        when("the data is modified", () -> {
          theData.set(theData.get() + "!");
        });

        then("the data can be seen with the addition", () -> {
          assertThat(theData.get(), is("Hello world!"));
        });

        and("the data is still available", () -> {
          assertNotNull(theData.get());
        });
      });

      scenario("uses default value from box", () -> {
        Variable<String> theData = new Variable<>("Hello world");

        given("the data is set correctly", () -> {
          assertThat(theData.get(), is("Hello world"));
        });

        when("the data is modified", () -> {
          theData.set(theData.get() + "!");
        });

        then("the data can be seen with the addition", () -> {
          assertThat(theData.get(), is("Hello world!"));
        });
      });
    });
  }
}

package specs;

import static com.greghaskins.spectrum.dsl.gherkin.GherkinSyntax.and;
import static com.greghaskins.spectrum.dsl.gherkin.GherkinSyntax.given;
import static com.greghaskins.spectrum.dsl.gherkin.GherkinSyntax.scenarioOutline;
import static com.greghaskins.spectrum.dsl.gherkin.GherkinSyntax.then;
import static com.greghaskins.spectrum.dsl.gherkin.GherkinSyntax.when;
import static com.greghaskins.spectrum.dsl.gherkin.ParameterizedSyntax.example;
import static com.greghaskins.spectrum.dsl.gherkin.ParameterizedSyntax.withExamples;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Variable;

import org.junit.runner.RunWith;

/**
 * Trying out Scenario outline.
 */
@RunWith(Spectrum.class)
public class ParameterizedSpecs {
  {
    scenarioOutline("Cuke eating - Gherkin style",
        (start, eat, left) -> {

          Variable<CukeEater> me = new Variable<>();

          given("there are " + start + " cucumbers", () -> {
            me.set(new CukeEater(start));
          });

          when("I eat " + eat + " cucumbers", () -> {
            me.get().eatCucumbers(eat);
          });

          then("I should have " + left + " cucumbers", () -> {
            assertThat(me.get().remainingCucumbers(), is(left));
          });
        },

        withExamples(
            example(12, 5, 7),
            example(20, 5, 15))

    );

    scenarioOutline("different types of parameters",
        (foo, bar, baz) -> {

          given("foo is " + foo, () -> {
          });
          and("bar is " + bar, () -> {
          });
          and("baz is " + baz, () -> {
          });
          when("something happens", () -> {
          });
          then("it works", () -> {
          });

        },

        withExamples(
            example(1, "boo", 3.14),
            example(1, "yay", 4.2))


    );

    scenarioOutline("with two parameters, just to see",
        (foo, bar) -> {

          given("blah " + foo, () -> {
          });
          when(bar + " - blerg", () -> {
          });
          then("something", () -> {
          });

        },

        withExamples(
            example("hey", 3.14),
            example("hi", 6.2),
            example("bye", -1.5))

    );
  }

  // dummy class under test
  static class CukeEater {

    private int amount;

    public CukeEater(int amount) {
      this.amount = amount;
    }

    public int remainingCucumbers() {
      return this.amount;
    }

    public void eatCucumbers(int number) {
      this.amount -= number;
    }

  }
}

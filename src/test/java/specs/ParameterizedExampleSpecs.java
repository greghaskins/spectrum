package specs;

import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.and;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.example;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.given;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.scenarioOutline;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.then;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.when;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.withExamples;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Variable;
import com.greghaskins.spectrum.dsl.gherkin.Gherkin;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import org.junit.runner.RunWith;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Trying out Scenario outline.
 */
@RunWith(Spectrum.class)
public class ParameterizedExampleSpecs {
  {
    scenarioOutline("Cucumber eating",
        (start, eat, remaining) -> {

          Variable<CukeEater> me = new Variable<>();

          given("there are " + start + " cucumbers", () -> {
            me.set(new CukeEater(start));
          });

          when("I eat " + eat + " cucumbers", () -> {
            me.get().eatCucumbers(eat);
          });

          then("I should have " + remaining + " cucumbers", () -> {
            assertThat(me.get().remainingCucumbers(), is(remaining));
          });
        },

        withExamples(
            example(12, 5, 7),
            example(20, 5, 15))

    );

    scenarioOutline("Simple calculations",
        (expression, expectedResult) -> {

          Variable<Calculator> calculator = new Variable<>();
          Variable<Number> result = new Variable<>();

          given("a calculator", () -> {
            calculator.set(new Calculator());
          });
          when("it computes the expression " + expression, () -> {
            result.set(calculator.get().compute(expression));
          });
          then("the result is " + expectedResult, () -> {
            assertThat(result.get(), is(expectedResult));
          });

        },

        withExamples(
            example("1 + 1", 2),
            example("5 * 9", 45),
            example("7 / 2", 3.5)));

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

  // another dummy class under test
  static class Calculator {

    private final ScriptEngine engine;

    public Calculator() {
      this.engine = new ScriptEngineManager().getEngineByName("nashorn");
    }

    public Number compute(String expression) throws Exception {
      return (Number) this.engine.eval(expression);
    }

  }
}

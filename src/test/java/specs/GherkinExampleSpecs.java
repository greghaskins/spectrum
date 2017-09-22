package specs;

import static com.greghaskins.spectrum.Configure.focus;
import static com.greghaskins.spectrum.Configure.with;
import static com.greghaskins.spectrum.Spectrum.let;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.and;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.feature;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.given;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.scenario;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.then;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.when;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.SpectrumHelper;
import com.greghaskins.spectrum.Variable;

import org.junit.runner.Result;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

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

      Supplier<List<String>> list = let(ArrayList::new);
      scenario("behaviour of let in gherkin", () -> {
        given("the data is set", () -> {
          list.get().add("hello");
        });

        then("the data is still there", () -> {
          assertThat(list.get().get(0), is("hello"));
        });
      });

      scenario("application of block configuration", () -> {
        Variable<Result> result = new Variable<>();
        when("executing a gherkin test suite with a configured block", () -> {
          result.set(SpectrumHelper.run(
              () -> {
                scenario("some scenario", () -> {
                  then("should have been ignored", () -> {
                  });
                });
                scenario("some focused scenario", with(focus(), () -> {
                  then("should not have been ignored", () -> {
                  });
                }));
              }));
        });
        then("the block configuration will have applied", () -> {
          assertThat(result.get().getRunCount(), is(1));
          assertThat(result.get().getIgnoreCount(), is(1));
        });
      });

    });
  }
}

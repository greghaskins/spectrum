package specs;

import static com.greghaskins.spectrum.Configure.randomOrder;
import static com.greghaskins.spectrum.Configure.with;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.dsl.gherkin.Gherkin.*;
import static java.util.stream.Collectors.toList;
import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.greghaskins.spectrum.BlockConfigurationChain;
import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.SpectrumHelper;

import org.junit.runner.Result;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(Spectrum.class)
public class RandomOrderSpecs {
  {
    describe("Random order specs", () -> {
      it("can execute at all", () -> {
        Result result = SpectrumHelper.run(() -> {
          buildSuite(randomOrder());
        });
        assertThat(result.getRunCount(), is(6));
      });

      it("will have different execution order each time", () -> {
        List<String> originalFailures = failureList(SpectrumHelper.run(() -> {
          buildSuite(randomOrder());
        }));

        // as we are dealing with random numbers, it may take a few goes before a new
        // permutation comes up
        int iteration = 0;
        Result nextResult;
        do {
          nextResult = SpectrumHelper.run(() -> {
            buildSuite(randomOrder());
          });
          iteration++;
        } while (iteration < 100 && failureList(nextResult).equals(originalFailures));

        // should not have reached the limit where we gave up finding a new one
        assertThat(iteration, not(is(100)));
      });

      it("can have the same execution order each time with a seed", () -> {
        Result result1 = SpectrumHelper.run(() -> {
          buildSuite(randomOrder(12345));
        });
        Result result2 = SpectrumHelper.run(() -> {
          buildSuite(randomOrder(12345));
        });

        assertThat(failureList(result1), is(failureList(result2)));
      });

      describe("composite tests", with(randomOrder(), () -> {
        scenario("a test that relies on order in a random order tree", () -> {
          final List<String> strings = new ArrayList<>();
          given("first step adds first", () -> {
            strings.add("first");
          });
          when("second step adds second", () -> {
            strings.add("second");
          });
          and("third step adds third", () -> {
            strings.add("third");
          });
          then("the order is correct", () -> {
            assertThat(strings, contains("first", "second", "third"));
          });
        });
      }));
    });
  }

  private List<String> failureList(Result result) {
    return result.getFailures().stream()
        .map(failure -> failure.getDescription().getMethodName())
        .collect(toList());
  }

  private static void buildSuite(BlockConfigurationChain order) {
    // each test fails so we can use the failures to determine if they were random
    describe("Tests in random order", with(order, () -> {
      describe("scramble each level of the hierarchy", () -> {
        it("happens whenever", () -> {
          fail();
        });
        it("happens after or before", () -> {
          fail();
        });
        it("can happen when it likes", () -> {
          fail();
        });
      });
      describe("make each level of the hierarchy different", () -> {
        it("then happens whenever", () -> {
          fail();
        });
        it("then happens after or before", () -> {
          fail();
        });
        it("then can happen when it likes", () -> {
          fail();
        });
      });
    }));
  }
}

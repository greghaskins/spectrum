package specs;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.include;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.Spectrum.let;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.greghaskins.spectrum.Spectrum;

import org.junit.runner.RunWith;

import java.util.function.Supplier;

/**
 * Examples of how to include specs into Spectrum with or without dependency injection.
 */
@RunWith(Spectrum.class)
public class IncludingSpecs {
  static void functionToInclude(Supplier<String> toTest) {
    describe("modularised or reusable by injected function", () -> {
      it("can access the dependency passed in", () -> {
        assertThat(toTest.get(), is("Hello world!"));
      });
    });
  }

  static class ClassToIncludeWithDependencyInjection {
    ClassToIncludeWithDependencyInjection(Supplier<String> toTest) {
      describe("modularised or reusable by injected object", () -> {
        it("can access the dependency passed in", () -> {
          assertThat(toTest.get(), is("Hello world!"));
        });
      });
    }
  }

  static class TestModule {
    {
      describe("test module", () -> {
        it("can be included and executed without having to be annotated for JUnit running", () -> {

        });

        it("allows for tests to be broken into separate classes, but run within the "
            + "inclusion rules of a parent suite", () -> {

            });
      });
    }
  }

  // weave the above together
  {
    describe("Dependency injection example", () -> {
      Supplier<String> string = let(() -> "Hello world!");

      functionToInclude(string);
      new ClassToIncludeWithDependencyInjection(string);
    });

    describe("Including modules example", () -> {
      include(TestModule.class);
    });
  }
}

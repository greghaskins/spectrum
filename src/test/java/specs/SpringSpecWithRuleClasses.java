package specs;

import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.junitMixin;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.greghaskins.spectrum.Spectrum;

import junit.spring.SomeService;
import junit.spring.SpringConfig;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.util.function.Supplier;

/**
 * Example of how to wire in Spring objects.
 */
@RunWith(Spectrum.class)
public class SpringSpecWithRuleClasses {
  /**
   * Note - you might usually declare this in its own file. This Mixin has the same structure as a
   * JUnit class with rules.
   */
  @ContextConfiguration(classes = {SpringConfig.class})
  public static class Mixin {
    @ClassRule
    public static final SpringClassRule classRule = new SpringClassRule();

    @Rule
    public SpringMethodRule methodRule = new SpringMethodRule();

    @Autowired
    SomeService someService;
  }

  // Normal testing starts here
  {
    describe("A spring specification", () -> {
      Supplier<Mixin> springMixin = junitMixin(Mixin.class);

      it("can access a spring bean from the mixin object", () -> {
        assertThat(springMixin.get().someService.getGreeting(), is("Hello world!"));
      });

      it("can access the bean a second time", () -> {
        assertThat(springMixin.get().someService.getGreeting(), is("Hello world!"));
      });

      it("can access a dependency of the bean", () -> {
        assertThat(springMixin.get().someService.getComponent().getState(), is(""));
      });

      it("when we write to the dependency", () -> {
        springMixin.get().someService.getComponent().setState("Bob");
      });

      it("the object stays set in Spring", () -> {
        assertThat(springMixin.get().someService.getComponent().getState(), is("Bob"));
      });

      describe("a child suite", () -> {
        it("also has access to the spring beans", () -> {
          assertThat(springMixin.get().someService.getComponent().getState(), is("Bob"));
        });
      });

    });
  }
}

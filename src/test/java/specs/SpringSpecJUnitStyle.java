package specs;

import static com.greghaskins.spectrum.dsl.spec.Spec.describe;
import static com.greghaskins.spectrum.dsl.spec.Spec.it;
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

/**
 * Example of how to mix Spring Test, JUnit and Spectrum.
 */
@RunWith(Spectrum.class)
@ContextConfiguration(classes = {SpringConfig.class})
public class SpringSpecJUnitStyle {
  @ClassRule
  public static final SpringClassRule classRule = new SpringClassRule();

  @Rule
  public SpringMethodRule methodRule = new SpringMethodRule();

  @Autowired
  SomeService someService;

  {
    describe("A spring specification", () -> {
      it("can access an autowired spring bean from the test object", () -> {
        assertThat(someService.getGreeting(), is("Hello world!"));
      });
    });
  }
}

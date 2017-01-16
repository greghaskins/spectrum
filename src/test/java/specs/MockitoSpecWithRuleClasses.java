package specs;


import static com.greghaskins.spectrum.Spectrum.applyRules;
import static com.greghaskins.spectrum.Spectrum.beforeEach;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.greghaskins.spectrum.Spectrum;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.function.Supplier;

/**
 * Example of using the Mockito JUnit Rule to provide mocks to specs.
 */
@RunWith(Spectrum.class)
public class MockitoSpecWithRuleClasses {
  // Example of a mockable
  interface SomeInterface {
    String getInput();
  }

  static class SomeClass {
    private SomeInterface someInterface;

    public SomeClass(SomeInterface someInterface) {
      this.someInterface = someInterface;
    }

    public String getResult() {
      return someInterface.getInput();
    }
  }

  // maybe this would be an external class - put inline here for clarity
  public static class Mocks {
    @Mock
    private SomeInterface mockInterface;

    @InjectMocks
    private SomeClass objectUnderTest;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
  }

  // test specs start here
  {
    describe("A suite which needs mockito", () -> {
      Supplier<Mocks> mocks = applyRules(Mocks.class);

      beforeEach(() -> {
        given(mocks.get().mockInterface.getInput()).willReturn("Hello world");
      });

      it("can use the mocks", () -> {
        assertThat(mocks.get().objectUnderTest.getResult(), is("Hello world"));
      });

      it("can use the mocks again", () -> {
        assertThat(mocks.get().objectUnderTest.getResult(), is("Hello world"));
      });

      it("gets a fresh mock each time", () -> {
        verify(mocks.get().mockInterface, never()).getInput();
      });

    });
  }
}

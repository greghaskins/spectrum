package specs;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
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

/**
 * Demonstration of how to mix metaphors and use Mockito with Spectrum via
 * class members. There is only one instance of the test objects
 * so {@link org.mockito.InjectMocks} may have unexpected behaviour in some
 * complex situations. If this doesn't work use {@link Spectrum#junitMixin(Class)}.
 */
@RunWith(Spectrum.class)
public class MockitoSpecJUnitStyle {
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

  // Test code starts here

  @Mock
  private SomeInterface mockInterface;

  @InjectMocks
  private SomeClass objectUnderTest;

  @Rule
  public MockitoRule rule = MockitoJUnit.rule();


  // test specs start here
  {
    describe("A suite which needs mockito", () -> {
      beforeEach(() -> {
        given(mockInterface.getInput()).willReturn("Hello world");
      });

      it("can use the mocks", () -> {
        assertThat(objectUnderTest.getResult(), is("Hello world"));
      });

      it("can use the mocks again", () -> {
        assertThat(objectUnderTest.getResult(), is("Hello world"));
      });

      it("uses the mock", () -> {
        objectUnderTest.getResult();
        verify(mockInterface).getInput();
      });

      it("gets a fresh mock each time", () -> {
        // so the mock has never been called
        verify(mockInterface, never()).getInput();
      });

    });
  }
}

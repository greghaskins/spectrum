package specs;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Spectrum.JUnitAdapter;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.function.Supplier;

@RunWith(Spectrum.class)
public class Spike {

  public static class Mixin extends JUnitAdapter {

    @Mock
    Foo foo;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
  }

  public static class Foo {

    public String getStuff() {
      return "hi";
    }

  }

  {


    describe("foo", () -> {
      Supplier<Mixin> junit4Mixin = Spectrum.junit4Mixin(Mixin.class);

      it("does stuff", () -> {

        Mockito.when(junit4Mixin.get().foo.getStuff()).thenReturn("bye");

        System.out.println(junit4Mixin.get().foo.getStuff());
        assertThat(junit4Mixin.get().foo, is(notNullValue()));
      });

    });
  }
}

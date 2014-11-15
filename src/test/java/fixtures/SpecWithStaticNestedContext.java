package fixtures;

import com.greghaskins.spectrum.Spectrum.Describe;
import com.greghaskins.spectrum.Spectrum.It;

@Describe("a spec")
public class SpecWithStaticNestedContext {

    @Describe("with a static nested context") static class Inner {

        @It("works too") void works() { }

    }

}

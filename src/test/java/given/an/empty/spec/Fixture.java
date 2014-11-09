package given.an.empty.spec;

import org.junit.runner.RunWith;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Spectrum.Describe;

public class Fixture {

    public static Class<?> getEmptySpec(){
        @RunWith(Spectrum.class)
        @Describe("An empty specification")
        class EmptySpec {

        }
        return EmptySpec.class;
    }

}

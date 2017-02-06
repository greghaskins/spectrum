package meta;

import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.Spectrum.xdescribe;
import static de.schauderhaft.degraph.check.Check.classpath;
import static de.schauderhaft.degraph.check.JCheck.violationFree;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.greghaskins.spectrum.Spectrum;

import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
public class DependencySpecs {
  {
    xdescribe("Spectrum dependencies", () -> {

      it("should not have any package cycles", () -> {
        assertThat(classpath().noJars().including("com.greghaskins.spectrum.**"),
            is(violationFree()));
      });

    });

  }
}

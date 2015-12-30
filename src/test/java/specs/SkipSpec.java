package specs;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.Spectrum.xit;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.runner.Result;
import org.junit.runner.RunWith;

import com.greghaskins.spectrum.Spectrum;

import helpers.SpectrumRunner;

@RunWith(Spectrum.class)
public class SkipSpec {{
	
	describe("tests using xit", () -> {
		
		it("should be ignored", () -> {
			final Result result = SpectrumRunner.run(getSpecWithSkippedTest());
			assertThat(result.getIgnoreCount(), is(1));
		});
	});
}

private static final Class<?> getSpecWithSkippedTest(){
    class Spec {{

        describe("a valid context", () -> {

            xit("with one ignored test", () -> {
				
			});
            
            it("and one passing test", () -> {
				assertThat(true, is(true));
			});

        });

    }}
    return Spec.class;
}

}

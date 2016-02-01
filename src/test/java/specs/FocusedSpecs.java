package specs;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.fit;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.runner.Result;
import org.junit.runner.RunWith;

import com.greghaskins.spectrum.Spectrum;

import helpers.SpectrumRunner;

@RunWith(Spectrum.class)
public class FocusedSpecs {{

	describe("Focused specs", () -> {

		it("are declared with `fit`", () -> {
			final Result result = SpectrumRunner.run(getSuiteWithFocusedTests());
			assertThat(result.getFailureCount(), is(0));
		});

		it("mark siblings as ignored so they don't get forgotten", () -> {
			final Result result = SpectrumRunner.run(getSuiteWithFocusedTests());
			assertThat(result.getIgnoreCount(), is(1));
		});
	});

}
private static Class<?> getSuiteWithFocusedTests() {
	class Suite {{

		describe("A spec that", () -> {

			fit("is focused and will run", () -> {
				assertThat(true, is(true));
			});

			it("is not focused and will not run", () -> {
				assertThat(true, is(false));
			});

		});

	}}

	return Suite.class;
}
}



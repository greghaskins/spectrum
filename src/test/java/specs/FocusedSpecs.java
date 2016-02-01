package specs;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.fdescribe;
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

	describe("Focused suites", () -> {

		it("are declared with `fdescribe`", () -> {
			final Result result = SpectrumRunner.run(getSuiteWithFocusedSubSuites());
			assertThat(result.getFailureCount(), is(0));
		});

		it("ignores tests that aren't focused", ()-> {
			final Result result = SpectrumRunner.run(getSuiteWithFocusedSubSuites());
			assertThat(result.getIgnoreCount(), is(2));
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
private static Class<?> getSuiteWithFocusedSubSuites() {
	class Suite {{
		describe("an unfocused suite", () -> {
			it("is ignored", () -> {
				assertThat(true, is(false));
			});
		});

		fdescribe("focused describe", () -> {
			it("will run", () -> {
				assertThat(true, is(true));
			});
			it("will also run", () -> {
				assertThat(true, is(true));
			});
		});

		fdescribe("another focused describe", () -> {
			fit("is focused and will run", () -> {
				assertThat(true, is(true));
			});
			it("is not focused and will not run", () -> {
				assertThat(false, is(true));
			});
		});

	}}
	return Suite.class;
}
}

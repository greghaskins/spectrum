package given.a.spec.with.exception.in.describe.block;

import static com.greghaskins.spectrum.Spectrum.describe;
import static matchers.IsFailure.failure;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import helpers.SpectrumRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Result;

public class WhenRunningTheSpec {

    private Result result;

    @Before
    public void before() throws Exception {
        result = SpectrumRunner.run(getSpecThatThrowsAnExceptionInDescribeBlock());
    }

    @Test
    public void thereIsOneFailure() throws Exception {
        assertThat(result.getFailureCount(), is(1));
    }

    @Test
    public void theFailureExplainsWhatHappened() throws Exception {
        assertThat(result.getFailures().get(0), is(failure("encountered an error", SomeException.class, "kaboom")));
    }

    public static Class<?> getSpecThatThrowsAnExceptionInDescribeBlock() {
        class Spec {{
            describe("an exploding context", () -> {
                throw new SomeException("kaboom");
            });
        }}
        return Spec.class;
    }

    public static class SomeException extends Exception {
        private static final long serialVersionUID = 1L;

        public SomeException(final String message) {
            super(message);
        }
    }

}

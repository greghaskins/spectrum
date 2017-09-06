package specs;

import static com.greghaskins.spectrum.Configure.timeout;
import static com.greghaskins.spectrum.Configure.with;
import static com.greghaskins.spectrum.Spectrum.*;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofMinutes;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.SpectrumHelper;

import org.junit.runner.Result;
import org.junit.runner.RunWith;

import java.util.function.Supplier;

@RunWith(Spectrum.class)
public class TimeoutSpecs {
  {
    describe("A suite with timeouts", () -> {
      it("will allow things to pass if they run quicker than the timeout", () -> {
        final Result result = SpectrumHelper.run(() -> {
          describe("Suite with generous timeout", with(timeout(ofMillis(1000)), () -> {
            it("has no problem when test lasts a tiny bit", () -> {
              Thread.sleep(1);
            });
          }));
        });
        assertThat(result.getFailureCount(), is(0));
      });

      it("will fail a single test that exceeds its timeout", () -> {
        final Result result = SpectrumHelper.run(() -> {
          describe("Suite with low timeout", with(timeout(ofMillis(1)), () -> {
            it("has spec oversleeps and fails", () -> {
              Thread.sleep(1000);
            });
          }));
        });
        assertThat(result.getFailureCount(), is(1));
      });

      it("will report a timing out test correctly when there are surrounding hooks", () -> {
        // if hooks were run in within the timeout's envelope, then when the child
        // is killed for timing out, there would be "bleed" of reporting
        final Result result = SpectrumHelper.run(() -> {
          describe("Suite with low timeout", with(timeout(ofMillis(1)), () -> {
            Supplier<String> let = let(() -> "Hello world");
            beforeEach(() -> {
              // deliberately blank before each - just for ensuring a hook is present
            });
            it("has spec oversleeps and fails", () -> {
              assertThat(let.get(), is("Hello world"));
              Thread.sleep(1000);
            });
          }));
        });
        assertThat(result.getFailureCount(), is(1));
      });

      it("will fail the test that fails its timeout and pass the others", () -> {
        final Result result = SpectrumHelper.run(() -> {
          describe("Suite with timeout", with(timeout(ofMillis(10)), () -> {
            it("has spec that takes no time", () -> {

            });

            it("has spec that oversleeps and fails", () -> {
              Thread.sleep(1000);
            });

            it("has another spec that takes no time", () -> {

            });
          }));
        });
        assertThat(result.getRunCount(), is(3));
        assertThat(result.getFailures().get(0).getDescription().getMethodName(),
            is("has spec that oversleeps and fails"));
        assertThat(result.getFailureCount(), is(1));
      });

      it("will propagate the timeout from parent to child", () -> {
        final Result result = SpectrumHelper.run(() -> {
          describe("Suite with low timeout", with(timeout(ofMillis(1)), () -> {
            describe("with child suite", () -> {
              it("oversleeps and fails", () -> {
                Thread.sleep(1000);
              });
            });
          }));
        });
        assertThat(result.getFailureCount(), is(1));
      });

      it("will allow a lower level to supersede the parent's timeout", () -> {
        final Result result = SpectrumHelper.run(() -> {
          describe("Suite with low timeout", with(timeout(ofMillis(1)), () -> {
            describe("with child suite", () -> {
              it("has a more generous timeout", with(timeout(ofMinutes(1)), () -> {
                Thread.sleep(100);
              }));
            });
          }));
        });
        assertThat(result.getFailureCount(), is(0));
      });
    });
  }
}

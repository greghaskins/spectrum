package specs;

import static com.greghaskins.spectrum.Configure.junitMixin;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.expectExceptions;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.greghaskins.spectrum.ExceptionExpectation;
import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.SpectrumHelper;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.function.Supplier;

@RunWith(Spectrum.class)
public class ExpectedExceptionSpecs {
  {
    describe("Expected exceptions", () -> {
      context("using JUnit ExpectedException Rule", () -> {
        it("will not work with a member variable in test class as the "
            + "error is caught before the rule catches it",
            () -> {
              final Result result = SpectrumHelper.run(classWithLocalExceptionMember());
              assertThat(result.getFailureCount(), is(1));
            });

        it("nor will it work if using a junitMixin", () -> {
          final Result result = SpectrumHelper.run(classWithExceptionMixin());
          assertThat(result.getFailureCount(), is(1));
        });
      });

      context("using Spectrum expectedException configuration", () -> {
        it("will fail on unexpected exception", () -> {
          final Result result = SpectrumHelper.run(() -> {
            ExceptionExpectation expectation = expectExceptions();
            describe("Expect exceptions", () -> {
              it("throws unexpected exception", () -> {
                expectation.expect(IOException.class);
                throw new RuntimeException("boom");
              });
            });
          });
          assertThat(result.getFailureCount(), is(1));
          assertThat(result.getFailures().get(0).getMessage(),
              is("Expected type: java.io.IOException but got java.lang.RuntimeException: boom"));
        });

        it("will pass on expected exception by class", () -> {
          final Result result = SpectrumHelper.run(() -> {
            ExceptionExpectation expectation = expectExceptions();
            describe("Expect exceptions", () -> {
              it("throws expected exception", () -> {
                expectation.expect(IOException.class);
                throw new IOException("boom");
              });
            });
          });
          assertThat(result.getFailureCount(), is(0));
          assertThat(result.getRunCount(), is(1));
        });

        it("will pass on expected exception by message", () -> {
          final Result result = SpectrumHelper.run(() -> {
            ExceptionExpectation expectation = expectExceptions();
            describe("Expect exceptions", () -> {
              it("throws expected exception", () -> {
                expectation.expectMessage("boom");
                throw new IOException("boom");
              });
            });
          });
          assertThat(result.getFailureCount(), is(0));
          assertThat(result.getRunCount(), is(1));
        });

        it("will pass on expected exception by message and class", () -> {
          final Result result = SpectrumHelper.run(() -> {
            ExceptionExpectation expectation = expectExceptions();
            describe("Expect exceptions", () -> {
              it("throws expected exception", () -> {
                expectation.expectMessage("boom");
                expectation.expect(IOException.class);
                throw new IOException("boom");
              });
            });
          });
          assertThat(result.getFailureCount(), is(0));
          assertThat(result.getRunCount(), is(1));
        });

        it("will fail on expected exception by message being wrong when class is right", () -> {
          final Result result = SpectrumHelper.run(() -> {
            ExceptionExpectation expectation = expectExceptions();
            describe("Expect exceptions", () -> {
              it("throws expected exception", () -> {
                expectation.expectMessage("boom");
                expectation.expect(IOException.class);
                throw new IOException("not boom!");
              });
            });
          });
          assertThat(result.getFailureCount(), is(1));
          assertThat(result.getFailures().get(0).getMessage(), is(
              "Expected message: boom, type: java.io.IOException "
                  + "but got java.io.IOException: not boom!"));
        });

        it("will pass on expected exception by part of message", () -> {
          final Result result = SpectrumHelper.run(() -> {
            ExceptionExpectation expectation = expectExceptions();
            describe("Expect exceptions", () -> {
              it("throws expected exception", () -> {
                expectation.expectMessageContains("boom");
                expectation.expect(IOException.class);
                throw new IOException("This exception went boom");
              });
            });
          });
          assertThat(result.getFailureCount(), is(0));
          assertThat(result.getRunCount(), is(1));
        });

        it("will fail on expected exception by part of message", () -> {
          final Result result = SpectrumHelper.run(() -> {
            ExceptionExpectation expectation = expectExceptions();
            describe("Expect exceptions", () -> {
              it("throws expected exception", () -> {
                expectation.expectMessageContains("boom");
                expectation.expect(IOException.class);
                throw new IOException("This exception is a bang");
              });
            });
          });
          assertThat(result.getFailureCount(), is(1));
        });

        it("will fail on expected exception not being thrown", () -> {
          final Result result = SpectrumHelper.run(() -> {
            ExceptionExpectation expectation = expectExceptions();
            describe("Expect exceptions", () -> {
              it("throws expected exception", () -> {
                expectation.expectMessageContains("boom");
              });
            });
          });
          assertThat(result.getFailureCount(), is(1));
          assertThat(result.getFailures().get(0).getMessage(), is(
              "Expected message contains: boom but ended with no exception"));
        });

        it("will fail on expected exception predicate", () -> {
          final Result result = SpectrumHelper.run(() -> {
            ExceptionExpectation expectation = expectExceptions();
            describe("Expect exceptions", () -> {
              it("throws expected exception", () -> {
                expectation.expect("Should be UnsupportedOperation",
                    thrown -> thrown instanceof UnsupportedOperationException);
                throw new RuntimeException("Not what you were expecting");
              });
            });
          });
          assertThat(result.getFailureCount(), is(1));
          assertThat(result.getFailures().get(0).getMessage(), is(
              "Expected Should be UnsupportedOperation but "
                  + "got java.lang.RuntimeException: Not what you were expecting"));
        });

        it("will fail on actual exception when no expectation set", () -> {
          final Result result = SpectrumHelper.run(() -> {
            ExceptionExpectation expectation = expectExceptions();
            describe("Expect exceptions", () -> {
              it("throws", () -> {
                throw new RuntimeException("Boom!");
              });
            });
          });
          assertThat(result.getFailureCount(), is(1));
          assertThat(result.getFailures().get(0).getMessage(), is(
              "Boom!"));
        });

        context("road testing passing tests with a shared expectation object", () -> {
          ExceptionExpectation expectation = expectExceptions();
          it("correctly goes bang", () -> {
            expectation.expectMessage("bang");
            throw new RuntimeException("bang");
          });

          it("does not throw", () -> {
            // passes without exception
          });

          it("does a boom", () -> {
            expectation.expectMessageContains("boom");
            expectation.expect(IOException.class);
            throw new IOException("this went boom!");
          });

          it("wants an unsupported operation", () -> {
            expectation.expect(UnsupportedOperationException.class);
            throw new UnsupportedOperationException();
          });
        });
      });
    });
  }

  private Class<?> classWithExceptionMixin() {
    class ExceptionMixin {
      @Rule
      public ExpectedException expectedException = ExpectedException.none();
    }

    class ClassWithExceptionMixin {
      {
        Supplier<ExceptionMixin> exceptionMixin = junitMixin(ExceptionMixin.class);
        it("should expect this exception", () -> {
          exceptionMixin.get().expectedException.expect(RuntimeException.class);
          throw new RuntimeException("boom");
        });
      }
    }

    return ClassWithExceptionMixin.class;
  }

  private Class<?> classWithLocalExceptionMember() {
    class ClassWithExceptionRuleAsMember {
      @Rule
      public ExpectedException expectedException = org.junit.rules.ExpectedException.none();

      {
        it("should expect this exception", () -> {
          expectedException.expect(RuntimeException.class);
          throw new RuntimeException("boom");
        });
      }
    }

    return ClassWithExceptionRuleAsMember.class;
  }
}

package junit;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.greghaskins.spectrum.Spectrum;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.MethodSorters;
import org.junit.runners.model.InitializationError;
import org.mockito.InOrder;
import org.mockito.Mockito;

/**
 * These tests are to lock down the exact behaviour of the {@link RunNotifier}
 * so that we can replicate "good citizenship". Proves the same behaviour with both
 * JUnit's {@link BlockJUnit4ClassRunner} and Spectrum itself
 */
@RunWith(Spectrum.class)
public class RunNotifierTest {
  {
    describe("Native RunNotifier run by JUnit", () -> {
      it("Starts and finishes a successful test", () -> {
        RunNotifier notifier = runWithNotifier(OnePassing.class);
        InOrder inOrder = Mockito.inOrder(notifier);
        inOrder.verify(notifier).fireTestStarted(forMethod("passes"));
        inOrder.verify(notifier).fireTestFinished(forMethod("passes"));
        inOrder.verifyNoMoreInteractions();
      });

      it("Starts and finishes an unsuccessful test", () -> {
        RunNotifier notifier = runWithNotifier(OneFailing.class);
        InOrder inOrder = Mockito.inOrder(notifier);
        inOrder.verify(notifier).fireTestStarted(forMethod("fails"));
        inOrder.verify(notifier).fireTestFailure(any());
        inOrder.verify(notifier).fireTestFinished(forMethod("fails"));
        inOrder.verifyNoMoreInteractions();
      });

      it("Reports transitively failing tests owing to their before method failing", () -> {
        RunNotifier notifier = runWithNotifier(BeforeMethodFails.class);
        InOrder inOrder = Mockito.inOrder(notifier);
        inOrder.verify(notifier).fireTestStarted(forMethod("failsTransitively1"));
        inOrder.verify(notifier).fireTestFailure(any());
        inOrder.verify(notifier).fireTestFinished(forMethod("failsTransitively1"));
        inOrder.verify(notifier).fireTestStarted(forMethod("failsTransitively2"));
        inOrder.verify(notifier).fireTestFailure(any());
        inOrder.verify(notifier).fireTestFinished(forMethod("failsTransitively2"));
        inOrder.verifyNoMoreInteractions();
      });

      it("Reports single failure for a class which has a failing before all method", () -> {
        RunNotifier notifier = runWithNotifier(BeforeClassMethodFails.class);
        InOrder inOrder = Mockito.inOrder(notifier);
        inOrder.verify(notifier).fireTestFailure(any());
        inOrder.verifyNoMoreInteractions();
      });
    });

    describe("RunNotifier run by Spectrum", () -> {
      it("Starts and finishes a successful test", () -> {
        RunNotifier notifier = runWithSpectrumNotifier(onePassingSpectrumTest());
        InOrder inOrder = Mockito.inOrder(notifier);
        inOrder.verify(notifier).fireTestStarted(forMethod("passes"));
        inOrder.verify(notifier).fireTestFinished(forMethod("passes"));
        inOrder.verifyNoMoreInteractions();
      });

      it("Starts and finishes an unsuccessful test", () -> {
        RunNotifier notifier = runWithSpectrumNotifier(oneFailingSpectrumTest());
        InOrder inOrder = Mockito.inOrder(notifier);
        inOrder.verify(notifier).fireTestStarted(forMethod("fails"));
        inOrder.verify(notifier).fireTestFailure(any());
        inOrder.verify(notifier).fireTestFinished(forMethod("fails"));
        inOrder.verifyNoMoreInteractions();
      });

      it("Reports transitively failing tests owing to their before method failing", () -> {
        RunNotifier notifier = runWithSpectrumNotifier(failingBeforeEachTest());
        InOrder inOrder = Mockito.inOrder(notifier);
        inOrder.verify(notifier).fireTestStarted(forMethod("failsTransitively1"));
        inOrder.verify(notifier).fireTestFailure(any());
        inOrder.verify(notifier).fireTestFinished(forMethod("failsTransitively1"));
        inOrder.verify(notifier).fireTestStarted(forMethod("failsTransitively2"));
        inOrder.verify(notifier).fireTestFailure(any());
        inOrder.verify(notifier).fireTestFinished(forMethod("failsTransitively2"));
        inOrder.verifyNoMoreInteractions();
      });

      it("Reports multiple failures for a class which has a failing before all method", () -> {
        // note: this is a deviation from the way BlockJUnitRunner specifically runs
        RunNotifier notifier = runWithSpectrumNotifier(failingBeforeAllTest());
        InOrder inOrder = Mockito.inOrder(notifier);
        inOrder.verify(notifier, times(2)).fireTestFailure(any());
        inOrder.verifyNoMoreInteractions();
      });
    });
  }

  private Description forMethod(String name) {
    return argThat(description -> description.getMethodName().equals(name));
  }

  private RunNotifier runWithNotifier(Class<?> clazz) {
    RunNotifier notifier = mock(RunNotifier.class);
    try {
      new BlockJUnit4ClassRunner(clazz).run(notifier);
    } catch (InitializationError initializationError) {
      throw new RuntimeException("Cannot initialize test: " + initializationError.getMessage(),
          initializationError);
    }

    return notifier;
  }

  private RunNotifier runWithSpectrumNotifier(Class<?> clazz) {
    RunNotifier notifier = mock(RunNotifier.class);
    new Spectrum(clazz).run(notifier);

    return notifier;
  }

  // these classes will ACTUALLY be run with BlockJUnitRunner above, but we mark them
  // as run with Spectrum so that they are passed over if an IDE's test runner decides
  // to try to execute them
  @RunWith(Spectrum.class)
  public static class OnePassing {
    @Test
    public void passes() {
      successfulAssertion();
    }
  }

  @RunWith(Spectrum.class)
  public static class OneFailing {
    @Test
    public void fails() {
      failAnAssertion();
    }
  }

  // method order is fixed to enable order-based verification
  @RunWith(Spectrum.class)
  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class BeforeMethodFails {
    @Before
    public void beforeFails() {
      failAnAssertion();
    }

    @Test
    public void failsTransitively1() {
      successfulAssertion();
    }

    @Test
    public void failsTransitively2() {
      successfulAssertion();
    }
  }

  // method order is fixed to enable order-based verification
  @RunWith(Spectrum.class)
  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class BeforeClassMethodFails {
    @BeforeClass
    public static void beforeClassFails() {
      failAnAssertion();
    }

    @Test
    public void failsTransitively1() {
      successfulAssertion();
    }

    @Test
    public void failsTransitively2() {
      successfulAssertion();
    }
  }

  private static Class<?> onePassingSpectrumTest() {
    class Passing {
      {
        describe("suite", () -> {
          it("passes", () -> {
          });
        });
      }
    }

    return Passing.class;
  }

  private static Class<?> oneFailingSpectrumTest() {
    class Failing {
      {
        describe("suite", () -> {
          it("fails", () -> {
            assertTrue(false);
          });
        });
      }
    }

    return Failing.class;
  }

  private static Class<?> failingBeforeEachTest() {
    class FailingBeforeEach {
      {
        describe("suite", () -> {
          beforeEach(() -> {
            throw new IllegalArgumentException("aaagh");
          });

          it("failsTransitively1", () -> {
          });

          it("failsTransitively2", () -> {
          });
        });
      }
    }

    return FailingBeforeEach.class;
  }

  private static Class<?> failingBeforeAllTest() {
    class FailingBeforeAll {
      {
        describe("suite", () -> {
          beforeAll(() -> {
            throw new IllegalArgumentException("aaagh");
          });

          it("failsTransitively1", () -> {
          });

          it("failsTransitively2", () -> {
          });
        });
      }
    }

    return FailingBeforeAll.class;
  }

  private static void successfulAssertion() {
    assertThat("black", is("black"));
  }

  private static void failAnAssertion() {
    assertThat("black", is("white"));
  }
}

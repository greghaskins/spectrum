package com.greghaskins.spectrum;

import static com.greghaskins.spectrum.PreConditionBlock.with;
import static com.greghaskins.spectrum.PreConditions.Factory.focus;
import static com.greghaskins.spectrum.PreConditions.Factory.ignore;

import org.junit.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Implements a BDD-style test runner, similar to RSpec and Jasmine. It uses JUnit's standard
 * reporting mechanisms ({@link org.junit.runner.Description}), but provides a completely different
 * way of writing tests. Annotate you class with {@code @RunWith(Spectrum.class)}, and use the
 * static methods to declare your specs.
 *
 * @see #describe
 * @see #it
 * @see #beforeEach
 * @see #afterEach
 * @see #let
 *
 */
public final class Spectrum extends Runner {

  /**
   * A generic code block with a {@link #run()} method to perform any action. Usually defined by a
   * lambda function.
   * @deprecated since 1.0.1 - use {@link com.greghaskins.spectrum.Block} instead
   */
  @Deprecated
  @FunctionalInterface
  public interface Block extends com.greghaskins.spectrum.Block {
    /**
     * Execute the code block, raising any {@code Throwable} that may occur.
     *
     * @throws Throwable any uncaught Error or Exception
     */
    @Override
    void run() throws Throwable;
  }

  /**
   * Declare a test suite that is made of interdependent children. The whole
   * suite should pass atomically and if it fails, any remaining children
   * can stop running.
   *
   * @param context Description of the context for this suite
   * @param block {@link com.greghaskins.spectrum.Block} with one or more calls to
   *    {@link #it(String, com.greghaskins.spectrum.Block) it} that define each expected behavior
   *
   */
  static void compositeSpec(final String context, final com.greghaskins.spectrum.Block block) {
    final Suite suite = getCurrentSuiteBeingDeclared().addAbortingSuite(context);
    beginDefinition(suite, block);
  }

  /**
   * Declare a test suite that describes the expected behavior of the system in a given context.
   *
   * @param context Description of the context for this suite
   * @param block {@link com.greghaskins.spectrum.Block} with one or more calls to
   *        {@link #it(String, com.greghaskins.spectrum.Block) it} that define
   *        each expected behavior
   */
  public static void describe(final String context, final com.greghaskins.spectrum.Block block) {
    final Suite suite = getCurrentSuiteBeingDeclared().addSuite(context);
    suite.applyPreConditions(block);
    beginDefinition(suite, block);
  }

  /**
   * Focus on this specific suite, while ignoring others.
   *
   * @param context Description of the context for this suite
   * @param block {@link com.greghaskins.spectrum.Block} with one or more calls to
   *        {@link #it(String, com.greghaskins.spectrum.Block) it} that define
   *        each expected behavior
   *
   * @see #describe(String, com.greghaskins.spectrum.Block)
   *
   */
  public static void fdescribe(final String context, final com.greghaskins.spectrum.Block block) {
    describe(context, with(focus(), block));
  }

  /**
   * Ignore the specific suite.
   *
   * @param context Description of the context for this suite
   * @param block {@link com.greghaskins.spectrum.Block} with one or more calls to
   *        {@link #it(String, com.greghaskins.spectrum.Block) it} that define
   *        each expected behavior
   *
   * @see #describe(String, com.greghaskins.spectrum.Block)
   *
   */
  public static void xdescribe(final String context, final com.greghaskins.spectrum.Block block) {
    describe(context, with(ignore(), block));
  }

  /**
   * Declare a spec, or test, for an expected behavior of the system in this suite context.
   *
   * @param behavior Description of the expected behavior
   * @param block {@link com.greghaskins.spectrum.Block} that verifies the system behaves as
   *        expected and throws a {@link java.lang.Throwable Throwable} if that expectation
   *        is not met.
   */
  public static void it(final String behavior, final com.greghaskins.spectrum.Block block) {
    getCurrentSuiteBeingDeclared().addSpec(behavior, block);
  }

  /**
   * Declare a pending spec (without a block) that will be ignored.
   *
   * @param behavior Description of the expected behavior
   *
   * @see #xit(String, com.greghaskins.spectrum.Block)
   */
  public static void it(final String behavior) {
    getCurrentSuiteBeingDeclared().addSpec(behavior, null).ignore();
  }

  /**
   * Focus on this specific spec, while ignoring others.
   *
   * @param behavior Description of the expected behavior
   * @param block {@link com.greghaskins.spectrum.Block} that verifies the system behaves as
   *        expected and throws a {@link java.lang.Throwable Throwable} if that expectation
   *        is not met.
   *
   * @see #it(String, com.greghaskins.spectrum.Block)
   */
  public static void fit(final String behavior, final com.greghaskins.spectrum.Block block) {
    it(behavior, with(focus(), block));
  }

  /**
   * Mark a spec as ignored so that it will be skipped.
   *
   * @param behavior Description of the expected behavior
   * @param block {@link com.greghaskins.spectrum.Block} that will not run, since this spec is
   *              ignored.
   *
   * @see #it(String, com.greghaskins.spectrum.Block)
   */
  public static void xit(final String behavior, final com.greghaskins.spectrum.Block block) {
    it(behavior);
  }

  /**
   * Call this from within a Spec to make the spec as ignored/pending.
   */
  public static void pending() {
    throw new AssumptionViolatedException("pending");
  }

  /**
   * Call this from within a Spec to make the spec as ignored/pending.
   * @param message the annotation of the pending
   */
  public static void pending(final String message) {
    throw new AssumptionViolatedException(message);
  }

  /**
   * Set the test to require certain tags of all tests below.
   * @param tags required tags - suites must have at least one of these if any are specified
   */
  public static void requireTags(final String... tags) {
    getCurrentSuiteBeingDeclared().requireTags(tags);
  }

  /**
   * Set the test to exclude certain tags of all tests below.
   * @param tags excluded tags - suites and specs must not have any of these if any are specified
   */
  public static void excludeTags(final String... tags) {
    getCurrentSuiteBeingDeclared().excludeTags(tags);
  }

  /**
   * Declare a {@link com.greghaskins.spectrum.Block} to be run before each spec in the suite.
   *
   * <p>
   * Use this to perform setup actions that are common across tests in the context. If multiple
   * {@code beforeEach} blocks are declared, they will run in declaration order.
   * </p>
   *
   * @param block {@link com.greghaskins.spectrum.Block} to run once before each spec
   */
  public static void beforeEach(final com.greghaskins.spectrum.Block block) {
    getCurrentSuiteBeingDeclared().beforeEach(block);
  }

  /**
   * Declare a {@link Block} to be run after each spec in the current suite.
   *
   * <p>
   * Use this to perform teardown or cleanup actions that are common across specs in this suite. If
   * multiple {@code afterEach} blocks are declared, they will run in declaration order.
   * </p>
   *
   * @param block {@link Block} to run once after each spec
   */
  public static void afterEach(final com.greghaskins.spectrum.Block block) {
    getCurrentSuiteBeingDeclared().afterEach(block);
  }

  /**
   * Declare a {@link Block} to be run once before all the specs in the current suite begin.
   *
   * <p>
   * Use {@code beforeAll} and {@link #afterAll(com.greghaskins.spectrum.Block) afterAll}
   * blocks with caution: since they only run once, shared state <strong>will</strong>
   * leak across specs.
   * </p>
   *
   * @param block {@link com.greghaskins.spectrum.Block} to run once before all specs in this suite
   */
  public static void beforeAll(final com.greghaskins.spectrum.Block block) {
    getCurrentSuiteBeingDeclared().beforeAll(block);
  }

  /**
   * Declare a {@link com.greghaskins.spectrum.Block} to be run once after all the specs in the
   * current suite have run.
   *
   * <p>
   * Use {@link #beforeAll(com.greghaskins.spectrum.Block) beforeAll} and {@code afterAll} blocks
   * with caution: since they only run once, shared state <strong>will</strong> leak across tests.
   * </p>
   *
   * @param block {@link com.greghaskins.spectrum.Block} to run once after all specs in this suite
   */
  public static void afterAll(final com.greghaskins.spectrum.Block block) {
    getCurrentSuiteBeingDeclared().afterAll(block);
  }

  /**
   * Define a memoized helper function. The value will be cached across multiple calls in the same
   * spec, but not across specs.
   *
   * <p>
   * Note that {@code let} is lazy-evaluated: the {@code supplier} is not called until the first
   * time it is used.
   * </p>
   *
   * @param <T> The type of value
   *
   * @param supplier {@link ThrowingSupplier} function that either generates the value, or throws a
   *        `Throwable`
   * @return memoized supplier
   */
  public static <T> Supplier<T> let(final ThrowingSupplier<T> supplier) {
    final ConcurrentHashMap<Supplier<T>, T> cache = new ConcurrentHashMap<>(1);
    afterEach(cache::clear);

    return () -> {
      if (getCurrentSuiteBeingDeclared() == null) {
        return cache.computeIfAbsent(supplier, Supplier::get);
      }
      throw new IllegalStateException("Cannot use the value from let() in a suite declaration. "
          + "It may only be used in the context of a running spec.");
    };
  }

  /**
   * Supplier of results similar to {@link Supplier}, but may optionally throw checked exceptions.
   * Using {@link ThrowingSupplier} is more convenient for lambda functions since it requires less
   * exception handling.
   *
   * @see Supplier
   *
   * @param <T> The type of result that will be supplied
   */
  @FunctionalInterface
  public interface ThrowingSupplier<T> extends Supplier<T> {

    /**
     * Get a result.
     *
     * @return a result
     * @throws Throwable any uncaught Error or Exception
     */
    T getOrThrow() throws Throwable;

    @Override
    default T get() {
      try {
        return getOrThrow();
      } catch (final RuntimeException | Error unchecked) {
        throw unchecked;
      } catch (final Throwable checked) {
        throw new RuntimeException(checked);
      }
    }
  }


  private static final Deque<Suite> suiteStack = new ArrayDeque<>();

  private final Suite rootSuite;

  /**
   * Main constructor called via reflection by the JUnit runtime.
   *
   * @param testClass The class file that defines the current suite
   *
   * @see org.junit.runner.Runner
   */
  public Spectrum(final Class<?> testClass) {
    this(testClass, Description.createSuiteDescription(testClass), new ConstructorBlock(testClass));
  }

  Spectrum(final Class<?> testClass, final Description description,
      final com.greghaskins.spectrum.Block definitionBlock) {
    this.rootSuite = Suite.rootSuite(description);
    this.rootSuite.readTagging(testClass);
    beginDefinition(this.rootSuite, definitionBlock);
  }

  @Override
  public Description getDescription() {
    return this.rootSuite.getDescription();
  }

  @Override
  public void run(final RunNotifier notifier) {
    this.rootSuite.run(notifier);
  }

  private static synchronized void beginDefinition(final Suite suite,
      final com.greghaskins.spectrum.Block definitionBlock) {
    suiteStack.push(suite);
    try {
      definitionBlock.run();
    } catch (final Throwable error) {
      suite.removeAllChildren();
      it("encountered an error", () -> {
        throw error;
      });
    }
    suiteStack.pop();
  }

  private static synchronized Suite getCurrentSuiteBeingDeclared() {
    return suiteStack.peek();
  }

}

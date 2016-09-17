package com.greghaskins.spectrum;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class Spectrum extends Runner {

  /**
   * A generic code block with a {@link #run()} method.
   *
   */
  @FunctionalInterface
  public interface Block {

    /**
     * Execute the code block, raising any {@code Throwable} that may occur.
     *
     * @throws Throwable if anything goes awry.
     */
    void run() throws Throwable;
  }

  /**
   * Declare a test suite that describes the expected behavior of the system in a given context.
   *
   * @param context Description of the context for this suite
   * @param block {@link Block} with one or more calls to {@link #it(String, Block) it} that define
   *        each expected behavior
   *
   */
  public static void describe(final String context, final Block block) {
    final Suite suite = getCurrentSuiteBeingDeclared().addSuite(context);
    beginDefintion(suite, block);
  }

  /**
   * Focus on this specific suite, while ignoring others.
   *
   * @param context Description of the context for this suite
   * @param block {@link Block} with one or more calls to {@link #it(String, Block) it} that define
   *        each expected behavior
   *
   * @see #describe(String, Block)
   *
   */
  public static void fdescribe(final String context, final Block block) {
    final Suite suite = getCurrentSuiteBeingDeclared().addSuite(context);
    suite.focus();
    beginDefintion(suite, block);
  }

  /**
   * Ignore the specific suite.
   *
   * @param context Description of the context for this suite
   * @param block {@link Block} with one or more calls to {@link #it(String, Block) it} that define
   *        each expected behavior
   *
   * @see #describe(String, Block)
   *
   */
  public static void xdescribe(final String context, final Block block) {
    final Suite suite = getCurrentSuiteBeingDeclared().addSuite(context);
    suite.ignore();
    beginDefintion(suite, block);
  }

  /**
   * Declare a spec, or test, for an expected behavior of the system in this suite context.
   *
   * @param behavior Description of the expected behavior
   * @param block {@link Block} that verifies the system behaves as expected and throws a
   *        {@link java.lang.Throwable Throwable} if that expectation is not met.
   */
  public static void it(final String behavior, final Block block) {
    getCurrentSuiteBeingDeclared().addSpec(behavior, block);
  }

  /**
   * Declare a pending spec (without a block) that will be ignored.
   *
   * @param behavior Description of the expected behavior
   *
   * @see #xit(String, Block)
   */
  public static void it(final String behavior) {
    getCurrentSuiteBeingDeclared().addSpec(behavior, null).ignore();
  }

  /**
   * Focus on this specific spec, while ignoring others.
   *
   * @param behavior Description of the expected behavior
   * @param block {@link Block} that verifies the system behaves as expected and throws a
   *        {@link java.lang.Throwable Throwable} if that expectation is not met.
   *
   * @see #it(String, Block)
   */
  public static void fit(final String behavior, final Block block) {
    getCurrentSuiteBeingDeclared().addSpec(behavior, block).focus();
  }

  /**
   * Mark a spec as ignored so that it will be skipped.
   *
   * @param behavior Description of the expected behavior
   * @param block {@link Block} that will not run, since this spec is ignored.
   *
   * @see #it(String, Block)
   */
  public static void xit(final String behavior, final Block block) {
    it(behavior);
  }

  /**
   * Declare a {@link Block} to be run before each spec in the suite.
   *
   * <p>
   * Use this to perform setup actions that are common across tests in the context. If multiple
   * {@code beforeEach} blocks are declared, they will run in declaration order.
   * </p>
   *
   * @param block {@link Block} to run once before each spec
   */
  public static void beforeEach(final Block block) {
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
  public static void afterEach(final Block block) {
    getCurrentSuiteBeingDeclared().afterEach(block);
  }

  /**
   * Declare a {@link Block} to be run once before all the specs in the current suite begin.
   *
   * <p>
   * Use {@code beforeAll} and {@link #afterAll(Block) afterAll} blocks with caution: since they
   * only run once, shared state <strong>will</strong> leak across specs.
   * </p>
   *
   * @param block {@link Block} to run once before all specs in this suite
   */
  public static void beforeAll(final Block block) {
    getCurrentSuiteBeingDeclared().beforeAll(block);
  }

  /**
   * Declare a {@link Block} to be run once after all the specs in the current suite have run.
   *
   * <p>
   * Use {@link #beforeAll(Block) beforeAll} and {@code afterAll} blocks with caution: since they
   * only run once, shared state <strong>will</strong> leak across tests.
   * </p>
   *
   * @param block {@link Block} to run once after all specs in this suite
   */
  public static void afterAll(final Block block) {
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
   * @param supplier Function that generates the value
   * @return memoized supplier
   */
  public static <T> Supplier<T> let(final Supplier<T> supplier) {
    final ConcurrentHashMap<Supplier<T>, T> cache = new ConcurrentHashMap<>(1);
    afterEach(() -> cache.clear());

    return () -> {
      if (getCurrentSuiteBeingDeclared() == null) {
        return cache.computeIfAbsent(supplier, s -> s.get());
      }
      throw new IllegalStateException("Cannot use the value from let() in a suite declaration. "
          + "It may only be used in the context of a running spec.");
    };
  }

  /**
   * Create a new Value wrapper. This is just a pointer to an instance of type <tt>T</tt>, which is
   * <tt>null</tt> by default. Having a reference that can be <tt>final</tt> , but with a mutable
   * <tt>value</tt> is helpful when working with Java closures.
   *
   * @deprecated Use {@link #let(Supplier)} instead.
   *
   * @param <T> The type of object to wrap
   *
   * @return A new wrapper object with <tt>null</tt> instance of <tt>T</tt>.
   */
  @Deprecated
  public static <T> Value<T> value() {
    return new Value<>(null);
  }

  /**
   * @deprecated Use {@link #let(Supplier)} instead.
   *
   * @param <T> The type of object to wrap
   * @param type Class of type <tt>T</tt> to wrap
   *
   * @return A new wrapper object with <tt>null</tt> instance of <tt>T</tt>.
   */
  @Deprecated
  public static <T> Value<T> value(final Class<T> type) {
    return value();
  }

  /**
   * Create a new Value wrapper. This is just a pointer to an instance of type <tt>T</tt>,
   * initialized to <tt>startingValue</tt> by default. Having a reference that can be <tt>final</tt>
   * , but with a mutable <tt>value</tt> is helpful when working with Java closures.
   *
   * @deprecated Use {@link #let(Supplier)} instead.
   *
   * @param <T> The type of object to wrap
   * @param startingValue The initial value to wrap.
   *
   * @return A new wrapper object around <tt>startingValue</tt>.
   */
  @Deprecated
  public static <T> Value<T> value(final T startingValue) {
    return new Value<>(startingValue);
  }

  /**
   * @deprecated Use {@link #let(Supplier)} instead.
   */
  @Deprecated
  public static class Value<T> {
    public T value;

    private Value(final T value) {
      this.value = value;
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
    final Description description = Description.createSuiteDescription(testClass);
    this.rootSuite = Suite.rootSuite(description);
    beginDefintion(this.rootSuite, new ConstructorBlock(testClass));
  }

  @Override
  public Description getDescription() {
    return this.rootSuite.getDescription();
  }

  @Override
  public void run(final RunNotifier notifier) {
    this.rootSuite.run(notifier);
  }

  private static synchronized void beginDefintion(final Suite suite, final Block definitionBlock) {
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

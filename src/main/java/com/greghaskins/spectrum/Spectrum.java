package com.greghaskins.spectrum;

import static com.greghaskins.spectrum.internal.AfterHook.after;
import static com.greghaskins.spectrum.internal.BeforeHook.before;
import static com.greghaskins.spectrum.internal.ConfiguredBlock.with;
import static com.greghaskins.spectrum.model.BlockConfiguration.Factory.focus;
import static com.greghaskins.spectrum.model.BlockConfiguration.Factory.ignore;

import com.greghaskins.spectrum.internal.LetHook;
import com.greghaskins.spectrum.model.ConstructorBlock;
import com.greghaskins.spectrum.model.HookContext;
import com.greghaskins.spectrum.model.IdempotentBlock;

import org.junit.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import java.util.ArrayDeque;
import java.util.Deque;
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
   *
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
   * Supplier that is allowed to throw.
   * @param <T> type of object to supply
   * @deprecated since 1.0.1 - use {@link com.greghaskins.spectrum.ThrowingSupplier} instead
   */
  @Deprecated
  @FunctionalInterface
  public interface ThrowingSupplier<T> extends com.greghaskins.spectrum.ThrowingSupplier<T> {
  }

  /**
   * Include a test suite into the current one.
   * @param testClass the class containing the Spectrum definitions
   * @throws Throwable on error
   */
  public static void include(final Class<?> testClass) throws Throwable {
    new ConstructorBlock(testClass).run();
  }

  /**
   * Declare a test suite that is made of interdependent children. The whole suite should pass
   * atomically and if it fails, any remaining children can stop running.
   *
   * @param context Description of the context for this suite
   * @param block {@link com.greghaskins.spectrum.Block} with one or more calls to
   *        {@link #it(String, com.greghaskins.spectrum.Block) it} that define each expected
   *        behavior
   *
   */
  static void compositeSpec(final String context, final com.greghaskins.spectrum.Block block) {
    final Suite suite = getCurrentSuiteBeingDeclared().addCompositeSuite(context);
    beginDefinition(suite, block);
  }

  /**
   * Declare a test suite that describes the expected behavior of the system in a given context.
   *
   * @param context Description of the context for this suite
   * @param block {@link com.greghaskins.spectrum.Block} with one or more calls to
   *        {@link #it(String, com.greghaskins.spectrum.Block) it} that define each expected
   *        behavior
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
   *        {@link #it(String, com.greghaskins.spectrum.Block) it} that define each expected
   *        behavior
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
   *        {@link #it(String, com.greghaskins.spectrum.Block) it} that define each expected
   *        behavior
   *
   * @see #describe(String, com.greghaskins.spectrum.Block)
   *
   */
  public static void xdescribe(final String context, final com.greghaskins.spectrum.Block block) {
    describe(context, with(ignore(), block));
  }

  /**
   * Define a test context.
   * @param context the description of the context
   * @param block the block to execute
   */
  public static void context(final String context, final com.greghaskins.spectrum.Block block) {
    describe(context, block);
  }

  /**
   * Define a focused test context. See {@link #fdescribe(String, com.greghaskins.spectrum.Block)}.
   * @param context the description of the context
   * @param block the block to execute
   */
  public static void fcontext(final String context, final com.greghaskins.spectrum.Block block) {
    fdescribe(context, block);
  }

  /**
   * Define an ignored test context. See
   * {@link #xdescribe(String, com.greghaskins.spectrum.Block)}.
   * @param context the description of the context
   * @param block the block to execute
   */
  public static void xcontext(final String context, final com.greghaskins.spectrum.Block block) {
    xdescribe(context, block);
  }

  /**
   * Declare a spec, or test, for an expected behavior of the system in this suite context.
   *
   * @param behavior Description of the expected behavior
   * @param block {@link com.greghaskins.spectrum.Block} that verifies the system behaves as
   *        expected and throws a {@link java.lang.Throwable Throwable} if that expectation is not
   *        met.
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
   *        expected and throws a {@link java.lang.Throwable Throwable} if that expectation is not
   *        met.
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
   *        ignored.
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
   *
   * @param message the annotation of the pending
   */
  public static void pending(final String message) {
    throw new AssumptionViolatedException(message);
  }

  public static Configuration configure() {
    return new Configuration(getCurrentSuiteBeingDeclared());
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
    addHook(new HookContext(before(block), getDepth(),
        HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.LOCAL));
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
    addHook(new HookContext(after(block), getDepth(),
        HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.GUARANTEED_CLEAN_UP_LOCAL));
  }

  /**
   * Declare a {@link Block} to be run once before all the specs in the current suite begin.
   *
   * <p>
   * Use {@code beforeAll} and {@link #afterAll(com.greghaskins.spectrum.Block) afterAll} blocks
   * with caution: since they only run once, shared state <strong>will</strong> leak across specs.
   * </p>
   *
   * @param block {@link com.greghaskins.spectrum.Block} to run once before all specs in this suite
   */
  public static void beforeAll(final com.greghaskins.spectrum.Block block) {
    addHook(new HookContext(before(new IdempotentBlock(block)), getDepth(),
        HookContext.AppliesTo.EACH_CHILD, HookContext.Precedence.SET_UP));
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
    addHook(new HookContext(after(block), getDepth(), HookContext.AppliesTo.ONCE,
        HookContext.Precedence.GUARANTEED_CLEAN_UP_GLOBAL));
  }


  /**
   * Declare a block of code that runs around each spec, partly before and partly after. You must
   * call {@link com.greghaskins.spectrum.Block#run} inside this Consumer. This code is applied to
   * every spec in the current suite.
   *
   * @param consumer to run each spec block
   */
  public static void aroundEach(ThrowingConsumer<com.greghaskins.spectrum.Block> consumer) {
    addHook(new HookContext(consumer::acceptOrThrow, getDepth(),
        HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.GUARANTEED_CLEAN_UP_LOCAL));
  }

  /**
   * Declare a block of code that runs once around all specs, partly before and partly after specs
   * are run. You must call {@link com.greghaskins.spectrum.Block#run} inside this Consumer. This
   * code is applied once per suite, so be careful about shared state across specs.
   *
   * @param consumer to run each spec block
   */
  public static void aroundAll(ThrowingConsumer<com.greghaskins.spectrum.Block> consumer) {
    addHook(new HookContext(consumer::acceptOrThrow, getDepth(),
        HookContext.AppliesTo.ONCE, HookContext.Precedence.OUTER));
  }

  /**
   * A value that will be fresh within each spec and cannot bleed across specs.
   *
   * <p>
   * Note that {@code let} is lazy-evaluated: the {@code supplier} is not called until the first
   * time it is used.
   * </p>
   *
   * @param <T> The type of value
   *
   * @param supplier {@link com.greghaskins.spectrum.ThrowingSupplier} function that either
   *        generates the value, or throws a `Throwable`
   * @return supplier which is refreshed for each spec's context
   */
  public static <T> Supplier<T> let(final com.greghaskins.spectrum.ThrowingSupplier<T> supplier) {
    LetHook<T> letHook = new LetHook<>(supplier);
    HookContext hookContext = new HookContext(letHook, getDepth(),
        HookContext.AppliesTo.ATOMIC_ONLY, HookContext.Precedence.LOCAL);
    addHook(hookContext);

    return letHook;
  }

  /**
   * Will throw an exception if this method happens to be called while Spectrum is still defining
   * tests, rather than executing them. Useful to see if a hook is being accidentally used
   * during definition.
   */
  public static void assertSpectrumInTestMode() {
    if (getCurrentSuiteBeingDeclared() != null) {
      throw new IllegalStateException("Cannot use this statement in a suite declaration. "
          + "It may only be used in the context of a running spec.");
    }
  }

  private static final Variable<Deque<Suite>> suiteStack = new Variable<>(ArrayDeque::new);

  private final Suite rootSuite;

  /**
   * Main constructor called via reflection by the JUnit runtime.
   *
   * @param testClass The class file that defines the current suite
   *
   * @see org.junit.runner.Runner
   */
  public Spectrum(final Class<?> testClass) {
    this(Description.createSuiteDescription(testClass), new ConstructorBlock(testClass));
  }

  Spectrum(Description description, com.greghaskins.spectrum.Block definitionBlock) {
    this.rootSuite = Suite.rootSuite(description);
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

  private static void beginDefinition(final Suite suite,
      final com.greghaskins.spectrum.Block definitionBlock) {
    getSuiteStack().push(suite);
    try {
      definitionBlock.run();
    } catch (final Throwable error) {
      suite.removeAllChildren();
      it("encountered an error", () -> {
        throw error;
      });
    }
    getSuiteStack().pop();
  }

  private static Deque<Suite> getSuiteStack() {
    return suiteStack.get();
  }

  private static int getDepth() {
    return getSuiteStack().size();
  }

  private static Suite getCurrentSuiteBeingDeclared() {
    return getSuiteStack().peek();
  }

  private static void addHook(HookContext hook) {
    getCurrentSuiteBeingDeclared().addHook(hook);
  }
}

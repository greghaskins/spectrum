package com.greghaskins.spectrum;

import static com.greghaskins.spectrum.Unboxer.unbox;

import com.greghaskins.spectrum.dsl.specification.Specification;
import com.greghaskins.spectrum.internal.DeclarationState;
import com.greghaskins.spectrum.internal.Suite;
import com.greghaskins.spectrum.internal.blocks.ConstructorBlock;
import com.greghaskins.spectrum.internal.junit.Rules;
import com.greghaskins.spectrum.internal.junit.RunNotifierReporting;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import java.util.function.Supplier;

/**
 * Implements a BDD-style test runner, similar to RSpec and Jasmine. It uses JUnit's standard
 * reporting mechanisms ({@link org.junit.runner.Description}), but provides a completely different
 * way of writing tests. Annotate your class with {@code @RunWith(Spectrum.class)}, and use the
 * static methods to declare your specs.
 *
 * @see Specification#describe
 * @see Specification#it
 * @see Specification#beforeEach
 * @see Specification#afterEach
 * @see Specification#let
 */
public final class Spectrum extends Runner {

  /**
   * A generic code block with a {@link #run()} method to perform any action. Usually defined by a
   * lambda function.
   *
   * @deprecated since 1.1.0 - use {@link com.greghaskins.spectrum.Block} instead
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
   *
   * @param <T> type of object to supply
   * @deprecated since 1.1.0 - use {@link com.greghaskins.spectrum.ThrowingSupplier} instead
   */
  @Deprecated
  @FunctionalInterface
  public interface ThrowingSupplier<T> extends com.greghaskins.spectrum.ThrowingSupplier<T> {

  }

  /**
   * Declare a test suite that describes the expected behavior of the system in a given context.
   *
   * @param context Description of the context for this suite
   * @param block   {@link com.greghaskins.spectrum.Block} with one or more calls to {@link
   *                #it(String, com.greghaskins.spectrum.Block) it} that define each expected
   *                behavior
   * @see Specification#describe
   */
  public static void describe(final String context, final com.greghaskins.spectrum.Block block) {
    Specification.describe(context, block);
  }

  /**
   * Focus on this specific suite, while ignoring others.
   *
   * @param context Description of the context for this suite
   * @param block   {@link com.greghaskins.spectrum.Block} with one or more calls to {@link
   *                #it(String, com.greghaskins.spectrum.Block) it} that define each expected
   *                behavior
   * @see #describe(String, com.greghaskins.spectrum.Block)
   * @see Specification#fdescribe
   */
  public static void fdescribe(final String context, final com.greghaskins.spectrum.Block block) {
    Specification.fdescribe(context, block);
  }

  /**
   * Ignore the specific suite.
   *
   * @param context Description of the context for this suite
   * @param block   {@link com.greghaskins.spectrum.Block} with one or more calls to {@link
   *                #it(String, com.greghaskins.spectrum.Block) it} that define each expected
   *                behavior
   * @see #describe(String, com.greghaskins.spectrum.Block)
   * @see Specification#xdescribe
   */
  public static void xdescribe(final String context, final com.greghaskins.spectrum.Block block) {
    Specification.xdescribe(context, block);
  }

  /**
   * Declare a spec, or test, for an expected behavior of the system in this suite context.
   *
   * @param behavior Description of the expected behavior
   * @param block    {@link com.greghaskins.spectrum.Block} that verifies the system behaves as
   *                 expected and throws a {@link java.lang.Throwable Throwable} if that expectation
   *                 is not met.
   * @see Specification#it
   */
  public static void it(final String behavior, final com.greghaskins.spectrum.Block block) {
    Specification.it(behavior, block);
  }

  /**
   * Declare a pending spec (without a block) that will be ignored.
   *
   * @param behavior Description of the expected behavior
   * @see #xit(String, com.greghaskins.spectrum.Block)
   * @see Specification#it(String)
   */
  public static void it(final String behavior) {
    Specification.it(behavior);
  }

  /**
   * Focus on this specific spec, while ignoring others.
   *
   * @param behavior Description of the expected behavior
   * @param block    {@link com.greghaskins.spectrum.Block} that verifies the system behaves as
   *                 expected and throws a {@link java.lang.Throwable Throwable} if that expectation
   *                 is not met.
   * @see #it(String, com.greghaskins.spectrum.Block)
   * @see Specification#fit
   */
  public static void fit(final String behavior, final com.greghaskins.spectrum.Block block) {
    Specification.fit(behavior, block);
  }

  /**
   * Mark a spec as ignored so that it will be skipped.
   *
   * @param behavior Description of the expected behavior
   * @param block    {@link com.greghaskins.spectrum.Block} that will not run, since this spec is
   *                 ignored.
   * @see #it(String, com.greghaskins.spectrum.Block)
   * @see Specification#xit
   */
  public static void xit(final String behavior, final com.greghaskins.spectrum.Block block) {
    Specification.xit(behavior, block);
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
   * @see Specification#beforeEach
   */
  public static void beforeEach(final com.greghaskins.spectrum.Block block) {
    Specification.beforeEach(block);
  }

  /**
   * Declare a {@link com.greghaskins.spectrum.Block Block} to be run after each spec in the current
   * suite.
   *
   * <p>
   * Use this to perform teardown or cleanup actions that are common across specs in this suite. If
   * multiple {@code afterEach} blocks are declared, they will run in declaration order.
   * </p>
   *
   * @param block {@link com.greghaskins.spectrum.Block Block} to run once after each spec
   * @see Specification#afterEach
   */
  public static void afterEach(final com.greghaskins.spectrum.Block block) {
    Specification.afterEach(block);
  }

  /**
   * Declare a {@link com.greghaskins.spectrum.Block Block} to be run once before all the specs in
   * the current suite begin.
   *
   * <p>
   * Use {@code beforeAll} and {@link #afterAll(com.greghaskins.spectrum.Block) afterAll} blocks
   * with caution: since they only run once, shared state <strong>will</strong> leak across specs.
   * </p>
   *
   * @param block {@link com.greghaskins.spectrum.Block} to run once before all specs in this suite
   * @see Specification#beforeAll
   */
  public static void beforeAll(final com.greghaskins.spectrum.Block block) {
    Specification.beforeAll(block);
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
   * @see Specification#afterAll
   */
  public static void afterAll(final com.greghaskins.spectrum.Block block) {
    Specification.afterAll(block);
  }


  /**
   * A value that will be fresh within each spec and cannot bleed across specs.
   *
   * <p>
   * Note that {@code let} is lazy-evaluated: the {@code supplier} is not called until the first
   * time it is used.
   * </p>
   *
   * @param <T>      The type of value
   * @param supplier {@link com.greghaskins.spectrum.ThrowingSupplier} function that either
   *                 generates the value, or throws a {@link Throwable}
   * @return supplier which is refreshed for each spec's context
   * @see Specification#let
   */
  public static <T> Supplier<T> let(final com.greghaskins.spectrum.ThrowingSupplier<T> supplier) {
    return Specification.let(supplier);
  }

  /**
   * A value that will be fresh within each spec and cannot bleed across specs. This is provided
   * as a proxy to a lazy loading object, with the interface given.
   *
   * <p>
   * Note that {@code let} is lazy-evaluated: the internal {@code supplier} is not called until the first
   * time it is used.
   * </p>
   *
   * @param <T>      The type of value
   * @param <R>      the required type at the consumer - allowing for generic interfaces
   *                 (e.g. <code>List&lt;String&gt;</code>)
   * @param <S>      the type of the value's interface.
   * @param supplier {@link com.greghaskins.spectrum.ThrowingSupplier} function that either
   *                 generates the value, or throws a {@link Throwable}
   * @param interfaceToUse an interface type, that is supported by the supplied object and can be used
   *                       to access it through the supplier without having to call {@link Supplier#get()}
   * @return proxy to the object supplied, using the interface given
   * @see Specification#let
   * @see Unboxer#unbox(Supplier, Class)
   */
  public static <T extends S, R extends S, S> R let(
      final com.greghaskins.spectrum.ThrowingSupplier<T> supplier,
      Class<S> interfaceToUse) {
    return unbox(let(supplier), interfaceToUse);
  }

  private final Suite rootSuite;

  /**
   * Main constructor called via reflection by the JUnit runtime.
   *
   * @param testClass The class file that defines the current suite
   * @see org.junit.runner.Runner
   */
  public Spectrum(final Class<?> testClass) {
    this(Description.createSuiteDescription(testClass), createTestClassDefinitionBlock(testClass));
  }

  Spectrum(Description description, com.greghaskins.spectrum.Block definitionBlock) {
    this.rootSuite = Suite.rootSuite(description);
    DeclarationState.instance().beginDeclaration(this.rootSuite, definitionBlock);
  }

  @Override
  public Description getDescription() {
    return this.rootSuite.getDescription();
  }

  @Override
  public void run(final RunNotifier notifier) {
    this.rootSuite.run(new RunNotifierReporting(notifier));
  }

  /**
   * Links the test class construction to JUnit rules implementation. This creates a block which
   * when executed will perform test definition against Spectrum and also hooks JUnit rule
   * implementation to the definition based on any "@Rule" annotations on the members - see {@link
   * Rules}
   *
   * @param testClass type of the test object
   * @return a block with JUnit rules activated
   */
  private static <T> com.greghaskins.spectrum.Block createTestClassDefinitionBlock(
      final Class<T> testClass) {
    ConstructorBlock<T> constructTestClass = new ConstructorBlock<>(testClass);

    return () -> {
      constructTestClass.run();
      Rules.applyRules(constructTestClass.get(), DeclarationState.instance()::addHook);
    };
  }
}

package com.greghaskins.spectrum;

import com.greghaskins.spectrum.dsl.specification.Specification;
import com.greghaskins.spectrum.internal.DeclarationState;
import com.greghaskins.spectrum.internal.Suite;
import com.greghaskins.spectrum.internal.blocks.ConstructorBlock;
import com.greghaskins.spectrum.internal.configuration.BlockFocused;
import com.greghaskins.spectrum.internal.configuration.BlockIgnore;
import com.greghaskins.spectrum.internal.configuration.BlockTagging;
import com.greghaskins.spectrum.internal.configuration.ConfiguredBlock;
import com.greghaskins.spectrum.internal.junit.Rules;

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
 *
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
   * @param block {@link com.greghaskins.spectrum.Block} with one or more calls to
   *        {@link #it(String, com.greghaskins.spectrum.Block) it} that define each expected
   *        behavior
   */
  public static void describe(final String context, final com.greghaskins.spectrum.Block block) {
    Specification.describe(context, block);
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
   */
  public static void fdescribe(final String context, final com.greghaskins.spectrum.Block block) {
    Specification.fdescribe(context, block);
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
    Specification.xdescribe(context, block);
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
    Specification.it(behavior, block);
  }

  /**
   * Declare a pending spec (without a block) that will be ignored.
   *
   * @param behavior Description of the expected behavior
   *
   * @see #xit(String, com.greghaskins.spectrum.Block)
   */
  public static void it(final String behavior) {
    Specification.it(behavior);
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
    Specification.fit(behavior, block);
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
    Specification.xit(behavior, block);
  }

  public static Configuration configure() {
    return new Configuration(DeclarationState.instance().getCurrentSuiteBeingDeclared());
  }


  /**
   * Uses the given class as a mix-in for JUnit rules to be applied. These rules will cascade down
   * and be applied at the level of specs or atomic specs.
   *
   * @param rulesClass type of object to create and apply rules to for each spec.
   * @param <T> type of the object
   * @return a supplier of the rules object
   */
  public static <T> Supplier<T> junitMixin(final Class<T> rulesClass) {
    return Rules.applyRules(rulesClass, DeclarationState.instance()::addHook);
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
   * @param <T> The type of value
   *
   * @param supplier {@link com.greghaskins.spectrum.ThrowingSupplier} function that either
   *        generates the value, or throws a {@link Throwable}
   * @return supplier which is refreshed for each spec's context
   */
  public static <T> Supplier<T> let(final com.greghaskins.spectrum.ThrowingSupplier<T> supplier) {
    return Specification.let(supplier);
  }

  /**
   * Surround a {@link com.greghaskins.spectrum.Block} with the {@code with} statement to add
   * preconditions and metadata to it. E.g. <code>with(tags("foo"), () -&gt; {})</code>.<br>
   * Note: preconditions and metadata can be chained using the
   * {@link BlockConfigurationChain#and(BlockConfigurationChain)} method. E.g.
   * <code>with(tags("foo").and(ignore()), () -&gt; {})</code>
   *
   * @param configuration the chainable block configuration
   * @param block the enclosed block
   * @return a wrapped block with the given configuration
   *
   * @see #ignore(String)
   * @see #ignore()
   * @see #focus()
   * @see #tags(String...)
   *
   */
  public static com.greghaskins.spectrum.Block with(final BlockConfigurationChain configuration,
      final com.greghaskins.spectrum.Block block) {

    return ConfiguredBlock.with(configuration.getBlockConfiguration(), block);
  }

  /**
   * Mark a block as ignored by surrounding it with the ignore method.
   *
   * @param why explanation of why this block is being ignored
   * @param block the block to ignore
   * @return a wrapped block which will be ignored
   */
  public static com.greghaskins.spectrum.Block ignore(final String why,
      final com.greghaskins.spectrum.Block block) {
    return with(ignore(why), block);
  }

  /**
   * Mark a block as ignored by surrounding it with the ignore method.
   *
   * @param block the block to ignore
   * @return a wrapped block which will be ignored
   */
  public static com.greghaskins.spectrum.Block ignore(final com.greghaskins.spectrum.Block block) {
    return with(ignore(), block);
  }

  /**
   * Ignore the suite or spec.
   *
   * @return a chainable configuration that will ignore the block within a {@link #with}
   */
  public static BlockConfigurationChain ignore() {
    return new BlockConfigurationChain().with(new BlockIgnore());
  }

  /**
   * Ignore the suite or spec.
   *
   * @param reason why this block is ignored
   * @return a chainable configuration that will ignore the block within a {@link #with}
   */
  public static BlockConfigurationChain ignore(final String reason) {
    return new BlockConfigurationChain().with(new BlockIgnore(reason));
  }

  /**
   * Tags the suite or spec that is being declared with the given strings. Depending on the current
   * filter criteria, this may lead to the item being ignored during test execution.
   *
   * @param tags tags that relate to the suite or spec
   * @return a chainable configuration that has these tags set for the block in {@link #with}
   */
  public static BlockConfigurationChain tags(final String... tags) {
    return new BlockConfigurationChain().with(new BlockTagging(tags));
  }

  /**
   * Marks the suite or spec to be focused.
   *
   * @return a chainable configuration that will focus the suite or spec in the {@link #with}
   */
  public static BlockConfigurationChain focus() {
    return new BlockConfigurationChain().with(new BlockFocused());
  }

  private final Suite rootSuite;

  /**
   * Main constructor called via reflection by the JUnit runtime.
   *
   * @param testClass The class file that defines the current suite
   *
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
    this.rootSuite.run(notifier);
  }

  /**
   * Links the test class construction to JUnit rules implementation.
   *
   * @param testClass type of the test object
   * @return a block which when executed will perform test definition against Spectrum and also
   *         hooks JUnit rule implementation to the definition based on any "@Rule" annotations on
   *         the members - see {@link Rules}
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

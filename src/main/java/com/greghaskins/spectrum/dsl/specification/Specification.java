package com.greghaskins.spectrum.dsl.specification;

import static com.greghaskins.spectrum.Configure.focus;
import static com.greghaskins.spectrum.Configure.ignore;
import static com.greghaskins.spectrum.Configure.with;
import static com.greghaskins.spectrum.internal.hooks.AfterHook.after;
import static com.greghaskins.spectrum.internal.hooks.BeforeHook.before;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.ThrowingConsumer;
import com.greghaskins.spectrum.ThrowingSupplier;
import com.greghaskins.spectrum.internal.DeclarationState;
import com.greghaskins.spectrum.internal.Suite;
import com.greghaskins.spectrum.internal.blocks.IdempotentBlock;
import com.greghaskins.spectrum.internal.hooks.Hook;
import com.greghaskins.spectrum.internal.hooks.HookContext.AppliesTo;
import com.greghaskins.spectrum.internal.hooks.HookContext.Precedence;
import com.greghaskins.spectrum.internal.hooks.LetHook;

import org.junit.AssumptionViolatedException;

import java.util.function.Supplier;

public interface Specification {

  /**
   * Declare a test suite that describes the expected behavior of the system in a given context.
   *
   * @param context Description of the context for this suite
   * @param block   {@link Block} with one or more calls to {@link #it(String, Block) it} that
   *                define each expected behavior
   */
  static void describe(final String context, final Block block) {
    final Suite suite = DeclarationState.instance()
        .getCurrentSuiteBeingDeclared()
        .addSuite(context);
    suite.applyPreconditions(block);
    DeclarationState.instance().beginDeclaration(suite, block);
  }

  /**
   * Focus on this specific suite, while ignoring others.
   *
   * @param context Description of the context for this suite
   * @param block   {@link Block} with one or more calls to {@link #it(String, Block) it} that
   *                define each expected behavior
   * @see #describe(String, Block)
   */
  static void fdescribe(final String context, final Block block) {
    describe(context, with(focus(), block));
  }

  /**
   * Ignore the specific suite.
   *
   * @param context Description of the context for this suite
   * @param block   {@link Block} with one or more calls to {@link #it(String, Block) it} that
   *                define each expected behavior
   * @see #describe(String, Block)
   */
  static void xdescribe(final String context, final Block block) {
    describe(context, with(ignore(), block));
  }

  /**
   * Declare a spec, or test, for an expected behavior of the system in this suite context.
   *
   * @param behavior Description of the expected behavior
   * @param block    {@link Block} that verifies the system behaves as expected and throws a {@link
   *                 java.lang.Throwable Throwable} if that expectation is not met.
   */
  static void it(final String behavior, final Block block) {
    DeclarationState.instance().getCurrentSuiteBeingDeclared().addSpec(behavior, block);
  }

  /**
   * Declare a pending spec (without a block) that will be ignored.
   *
   * @param behavior Description of the expected behavior
   * @see #xit(String, Block)
   */
  static void it(final String behavior) {
    DeclarationState.instance().getCurrentSuiteBeingDeclared().addSpec(behavior, null).ignore();
  }

  /**
   * Focus on this specific spec, while ignoring others.
   *
   * @param behavior Description of the expected behavior
   * @param block    {@link Block} that verifies the system behaves as expected and throws a {@link
   *                 java.lang.Throwable Throwable} if that expectation is not met.
   * @see #it(String, Block)
   */
  static void fit(final String behavior, final Block block) {
    it(behavior, with(focus(), block));
  }

  /**
   * Mark a spec as ignored so that it will be skipped.
   *
   * @param behavior Description of the expected behavior
   * @param block    {@link Block} that will not run, since this spec is ignored.
   * @see #it(String, Block)
   */
  static void xit(final String behavior, final Block block) {
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
  static void beforeEach(final Block block) {
    DeclarationState.instance().addHook(before(block), AppliesTo.ATOMIC_ONLY, Precedence.LOCAL);
  }

  /**
   * Declare a {@link Block Block} to be run after each spec in the current suite.
   *
   * <p>
   * Use this to perform teardown or cleanup actions that are common across specs in this suite. If
   * multiple {@code afterEach} blocks are declared, they will run in declaration order.
   * </p>
   *
   * @param block {@link Block Block} to run once after each spec
   */
  static void afterEach(final Block block) {
    DeclarationState.instance().addHook(after(block), AppliesTo.ATOMIC_ONLY,
        Precedence.GUARANTEED_CLEAN_UP_LOCAL);
  }

  /**
   * Declare a {@link Block Block} to be run once before all the specs in the current suite begin.
   *
   * <p>
   * Use {@code beforeAll} and {@link #afterAll(Block) afterAll} blocks with caution: since they
   * only run once, shared state <strong>will</strong> leak across specs.
   * </p>
   *
   * @param block {@link Block} to run once before all specs in this suite
   */
  static void beforeAll(final Block block) {
    DeclarationState.instance().addHook(before(new IdempotentBlock(block)), AppliesTo.ATOMIC_ONLY,
        Precedence.SET_UP);
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
  static void afterAll(final Block block) {
    DeclarationState.instance().addHook(after(block), AppliesTo.ONCE,
        Precedence.GUARANTEED_CLEAN_UP_GLOBAL);
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
   * @param supplier {@link ThrowingSupplier} function that either generates the value, or throws a
   *                 {@link Throwable}
   * @return supplier which is refreshed for each spec's context
   */
  static <T> Supplier<T> let(final ThrowingSupplier<T> supplier) {
    LetHook<T> letHook = new LetHook<>(supplier);
    DeclarationState.instance().addHook(letHook, AppliesTo.ATOMIC_ONLY, Precedence.LOCAL);

    return letHook;
  }

  /**
   * Define a test context. Alias for {@link #describe}.
   *
   * @param context the description of the context
   * @param block   the block to execute
   */
  static void context(final String context, final Block block) {
    describe(context, block);
  }

  /**
   * Define a focused test context. Alias for {@link #fdescribe}.
   *
   * @param context the description of the context
   * @param block   the block to execute
   */
  static void fcontext(final String context, final Block block) {
    fdescribe(context, block);
  }

  /**
   * Define an ignored test context. Alias for {@link #xdescribe}.
   *
   * @param context the description of the context
   * @param block   the block to execute
   */
  static void xcontext(final String context, final Block block) {
    xdescribe(context, block);
  }

  /**
   * Call this from within a Spec block to mark the spec as ignored/pending.
   */
  static void pending() {
    throw new AssumptionViolatedException("pending");
  }

  /**
   * Call this from within a Specification to mark the spec as ignored/pending.
   *
   * @param message the annotation of the pending
   */
  static void pending(final String message) {
    throw new AssumptionViolatedException(message);
  }

  /**
   * Declare a block of code that runs around each spec, partly before and partly after. You must
   * call {@link com.greghaskins.spectrum.Block#run} inside this Consumer. This code is applied to
   * every spec in the current suite.
   *
   * @param consumer to run each spec block
   */
  static void aroundEach(ThrowingConsumer<Block> consumer) {
    DeclarationState.instance().addHook(Hook.from(consumer), AppliesTo.ATOMIC_ONLY,
        Precedence.GUARANTEED_CLEAN_UP_LOCAL);
  }

  /**
   * Declare a block of code that runs once around all specs, partly before and partly after specs
   * are run. You must call {@link Block#run} inside this Consumer. This code is applied once per
   * suite, so be careful about shared state across specs.
   *
   * @param consumer to run each spec block
   */
  static void aroundAll(ThrowingConsumer<Block> consumer) {
    DeclarationState.instance().addHook(Hook.from(consumer), AppliesTo.ONCE, Precedence.OUTER);
  }

}

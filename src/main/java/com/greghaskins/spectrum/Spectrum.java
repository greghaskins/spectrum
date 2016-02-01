package com.greghaskins.spectrum;

import java.util.ArrayDeque;
import java.util.Deque;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public class Spectrum extends Runner {

    /**
     * A generic code block with a {@link #run()} method.
     *
     */
    public static interface Block {

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
     * @param context
     *            Description of the context for this suite
     * @param block
     *            {@link Block} with one or more calls to {@link #it(String, Block) it} that define each expected behavior
     *
     */
    public static void describe(final String context, final Block block) {
    	final Suite suite = getCurrentSuite().addSuite(context);
        beginDefintion(suite, block);
    }

    /**
     * Declare a spec, or test, for an expected behavior of the system in this suite context.
     *
     * @param behavior
     *            Description of the expected behavior
     * @param block
     *            {@link Block} that verifies the system behaves as expected and throws a {@link java.lang.Throwable Throwable}
     *            if that expectation is not met.
     */
    public static void it(final String behavior, final Block block) {
        getCurrentSuite().addSpec(behavior, block);
    }
    
    /**
     * Focus on this specific test, while ignoring others.
     *
     * @param behavior
     *            Description of the expected behavior
     * @param block
     *            {@link Block} that verifies the system behaves as expected and throws a {@link java.lang.Throwable Throwable}
     *            if that expectation is not met.
     * 
     * @see {@link #it(String, Block) it}
     */
    public static void fit(final String behavior, final Block block) {
        getCurrentContext().addTest(behavior, block);
    }

    /**
     * Declare a {@link Block} to be run before each spec in the suite.
     *
     * <p>
     * Use this to perform setup actions that are common across tests in the context. If multiple {@code beforeEach} blocks are
     * declared, they will run in declaration order.
     * </p>
     *
     * @param block
     *            {@link Block} to run once before each spec
     */
    public static void beforeEach(final Block block) {
        getCurrentSuite().beforeEach(block);
    }

    /**
     * Declare a {@link Block} to be run after each spec in the current suite.
     *
     * <p>
     * Use this to perform teardown or cleanup actions that are common across specs in this suite. If multiple
     * {@code afterEach} blocks are declared, they will run in declaration order.
     * </p>
     *
     * @param block
     *            {@link Block} to run once after each spec
     */
    public static void afterEach(final Block block) {
        getCurrentSuite().afterEach(block);
    }

    /**
     * Declare a {@link Block} to be run once before all the specs in the current suite begin.
     *
     * <p>
     * Use {@code beforeAll} and {@link #afterAll(Block) afterAll} blocks with caution: since they only run once, shared state
     * <strong>will</strong> leak across specs.
     * </p>
     *
     * @param block
     *            {@link Block} to run once before all specs in this suite
     */
    public static void beforeAll(final Block block) {
        getCurrentSuite().beforeAll(block);
    }

    /**
     * Declare a {@link Block} to be run once after all the specs in the current suite have run.
     *
     * <p>
     * Use {@link #beforeAll(Block) beforeAll} and {@code afterAll} blocks with caution: since they only run once, shared state
     * <strong>will</strong> leak across tests.
     * </p>
     *
     * @param block
     *            {@link Block} to run once after all specs in this suite
     */
    public static void afterAll(final Block block) {
        getCurrentSuite().afterAll(block);
    }

    public static <T> Value<T> value(@SuppressWarnings("unused") final Class<T> type) {
        return new Value<T>(null);
    }

    public static <T> Value<T> value(final T startingValue) {
        return new Value<T>(startingValue);
    }

    public static class Value<T> {
        public T value;

        private Value(final T value) {
            this.value = value;
        }
    }

    private static final Deque<Suite> suiteStack = new ArrayDeque<Suite>();

    private final Suite rootSuite;

    public Spectrum(final Class<?> testClass) {
        final Description description = Description.createSuiteDescription(testClass);
        this.rootSuite = new Suite(description);
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

    synchronized private static void beginDefintion(final Suite suite, final Block definitionBlock) {
        suiteStack.push(suite);
        try {
            definitionBlock.run();
        } catch (final Throwable e) {
            it("encountered an error", new FailingBlock(e));
        }
        suiteStack.pop();
    }

    synchronized private static Suite getCurrentSuite() {
        return suiteStack.peek();
    }

}

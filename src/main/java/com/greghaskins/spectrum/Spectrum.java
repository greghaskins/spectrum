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
     * Declare a test suite to describe the expected behaviors of the system in a given context.
     *
     * @param context
     *            Description of the test context
     * @param block
     *            {@link Block} with one or more calls to {@link #it(String, Block) it} that define each expected behavior
     *
     */
    public static void describe(final String context, final Block block) {
    	final Suite suite = getCurrentContext().addSuite(context);
        enterContext(suite, block);
    }

    /**
     * Declare a test for an expected behavior of the system.
     *
     * @param behavior
     *            Description of the expected behavior
     * @param block
     *            {@link Block} that verifies the system behaves as expected and throws a {@link java.lang.Throwable Throwable}
     *            if that expectation is not met.
     */
    public static void it(final String behavior, final Block block) {
        getCurrentContext().addSpec(behavior, block);
    }

    /**
     * Declare a {@link Block} to be run before each test in the current context.
     *
     * <p>
     * Use this to perform setup actions that are common across tests in the context. If multiple {@code beforeEach} blocks are
     * declared, they will run in declaration order.
     * </p>
     *
     * @param block
     *            {@link Block} to run before each test
     */
    public static void beforeEach(final Block block) {
        getCurrentContext().beforeEach(block);
    }

    /**
     * Declare a {@link Block} to be run after each test in the current context.
     *
     * <p>
     * Use this to perform teardown or cleanup actions that are common across tests in this context. If multiple
     * {@code afterEach} blocks are declared, they will run in declaration order.
     * </p>
     *
     * @param block
     *            {@link Block} to run after each test
     */
    public static void afterEach(final Block block) {
        getCurrentContext().afterEach(block);
    }

    /**
     * Declare a {@link Block} to be run once before all the tests in the current context.
     *
     * <p>
     * Use {@code beforeAll} and {@link #afterAll(Block) afterAll} blocks with caution: since they only run once, shared state
     * <strong>will</strong> leak across tests.
     * </p>
     *
     * @param block
     *            {@link Block} to run once before all tests
     */
    public static void beforeAll(final Block block) {
        getCurrentContext().beforeAll(block);
    }

    /**
     * Declare a {@link Block} to be run once after all the tests in the current context.
     *
     * <p>
     * Use {@link #beforeAll(Block) beforeAll} and {@code afterAll} blocks with caution: since they only run once, shared state
     * <strong>will</strong> leak across tests.
     * </p>
     *
     * @param block
     *            {@link Block} to run once after all tests
     */
    public static void afterAll(final Block block) {
        getCurrentContext().afterAll(block);
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

    private static final Deque<Suite> globalSuites = new ArrayDeque<Suite>();
    static {
        globalSuites.push(new Suite(Description.createSuiteDescription("Spectrum tests")));
    }

    private final Suite rootContext;

    public Spectrum(final Class<?> testClass) {
        final Description description = Description.createSuiteDescription(testClass);
        this.rootContext = getCurrentContext().addSuite(description);
        enterContext(this.rootContext, new ConstructorBlock(testClass));
    }

    @Override
    public Description getDescription() {
        return this.rootContext.getDescription();
    }

    @Override
    public void run(final RunNotifier notifier) {
        this.rootContext.run(notifier);
    }

    private static void enterContext(final Suite suite, final Block block) {
        globalSuites.push(suite);
        try {
            block.run();
        } catch (final Throwable e) {
            it("encountered an error", new FailingBlock(e));
        }
        globalSuites.pop();
    }

    private static Suite getCurrentContext() {
        return globalSuites.peek();
    }

}

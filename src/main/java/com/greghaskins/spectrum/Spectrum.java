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
        final Context newContext = new Context(Description.createSuiteDescription(context));
        enterContext(newContext, block);
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
        getCurrentContext().addTest(behavior, block);
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
        getCurrentContext().addTestSetup(block);
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
        getCurrentContext().addTestTeardown(block);
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
        getCurrentContext().addContextSetup(block);
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
        getCurrentContext().addContextTeardown(block);
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

    private static final Deque<Context> globalContexts = new ArrayDeque<Context>();
    static {
        globalContexts.push(new Context(Description.createSuiteDescription("Spectrum tests")));
    }

    private final Description description;
    private final Context rootContext;

    public Spectrum(final Class<?> testClass) {
        description = Description.createSuiteDescription(testClass);
        rootContext = new Context(description);
        enterContext(rootContext, new ConstructorBlock(testClass));
    }

    @Override
    public Description getDescription() {
        return description;
    }

    @Override
    public void run(final RunNotifier notifier) {
        rootContext.execute(notifier);
    }

    private static void enterContext(final Context context, final Block block) {
        getCurrentContext().addChild(context);

        globalContexts.push(context);
        try {
            block.run();
        } catch (final Throwable e) {
            it("encountered an error", new FailingBlock(e));
        }
        globalContexts.pop();
    }

    private static Context getCurrentContext() {
        return globalContexts.peek();
    }

}

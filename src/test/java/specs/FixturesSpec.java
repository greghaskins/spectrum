package specs;

import static com.greghaskins.spectrum.Spectrum.afterAll;
import static com.greghaskins.spectrum.Spectrum.afterEach;
import static com.greghaskins.spectrum.Spectrum.beforeAll;
import static com.greghaskins.spectrum.Spectrum.beforeEach;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import helpers.SpectrumRunner;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Spectrum.Block;

@RunWith(Spectrum.class)
public class FixturesSpec {{

    describe("A spec using beforeEach and afterEach", () -> {

        final List<String> items = new ArrayList<String>();

        beforeEach(() -> {
            items.add("foo");
        });

        afterEach(() -> {
            items.clear();
        });

        it("runs beforeEach before every test", () -> {
            assertThat(items, contains("foo"));
            items.add("bar");
        });

        it("runs afterEach after every test", () -> {
            assertThat(items, contains("foo"));
            assertThat(items, not(contains("bar")));
        });

        describe("nested inside another describe", () -> {

            beforeEach(() -> {
                items.add("baz");
            });

            it("is run before tests in that context", () -> {
                assertThat(items, contains("foo", "baz"));
                items.clear();
            });

            it("run in addition to the beforeEach in the parent scope", () -> {
                assertThat(items, contains("foo", "baz"));
            });

        });

    });

    describe("Multiple beforeEach and afterEach blocks", () -> {

        final List<String> words = new ArrayList<String>();
        final ArrayList<Integer> numbers = new ArrayList<Integer>();

        afterEach(() -> {
            words.clear();
        });

        beforeEach(() -> {
            words.add("foo");
        });

        afterEach(() -> {
            numbers.clear();
        });

        beforeEach(() -> {
            numbers.add(1);
        });

        it("run in order before the first test", () -> {
            assertThat(words, contains("foo", "bar"));
        });

        it("and before the other tests", () -> {
            assertThat(words, contains("foo", "bar"));
        });

        describe("even with a nested context", () -> {

            beforeEach(() -> {
                numbers.add(3);
            });

            it("all run before each test in declaration order", () -> {
                assertThat(numbers, contains(1, 2, 3, 4));
            });

            it("runs afterEach blocks from parent context", () -> {
                assertThat(numbers, contains(1, 2, 3, 4));
            });

            beforeEach(() -> {
                numbers.add(4);
            });

        });

        beforeEach(() -> {
            words.add("bar");
        });

        beforeEach(() -> {
            numbers.add(2);
        });

    });

    describe("A spec using beforeAll", () -> {

        final List<String> items = new ArrayList<String>();

        beforeAll(() ->{
            items.add("foo");
        });

        beforeAll(() ->{
            items.add("bar");
        });

        it("sets the initial state before the tests run", () -> {
            assertThat(items, contains("foo", "bar"));
            items.add("baz");
        });

        it("does't reset any state between tests", () -> {
            assertThat(items, contains("foo", "bar", "baz"));
        });

    });

    describe("A spec using afterAll", () -> {

        final List<String> items = new ArrayList<String>();
        final List<Integer> numbers = new ArrayList<Integer>();

        describe("with some tests", () -> {

            afterAll(() ->{
                items.clear();
            });

            afterAll(() -> {
                numbers.add(5);
            });

            it("sets the initial state before tests run", () -> {
                assertThat(items, hasSize(0));
                items.add("foo");
            });

            it("doesn't reset any state between tests", () -> {
                assertThat(items, contains("foo"));
                items.add("bar");
            });

        });

        it("runs afterAll blocks after all the tests in a context", () -> {
            assertThat(items, hasSize(0));
            assertThat(numbers, contains(5));
        });

    });

    describe("A beforeEach block that explodes", () -> {

        it("causes all tests in that context to fail", () -> {
            final Result result = SpectrumRunner.run(getSpecWithExplodingBeforeEach());
            assertThat(result.getFailureCount(), is(2));
        });

    });

    describe("An afterEach block that explodes", () -> {

        it("causes all tests in that context to fail", () -> {
            final Result result = SpectrumRunner.run(getSpecWithExplodingAfterEach());
            assertThat(result.getFailureCount(), is(2));
        });

    });

    describe("beforeAll blocks that explode", () -> {

        it("cause all tests in that context and its children to fail", () -> {
            final Result result = SpectrumRunner.run(getSpecWithExplodingBeforeAll());
            assertThat(result.getFailureCount(), is(3));
        });

    });

    describe("afterAll blocks that explode", () -> {

        it("cause the context to fail once", () -> {
            final Result result = SpectrumRunner.run(getSpecWithExplodingAfterAll());
            assertThat(result.getFailureCount(), is(1));
        });

        it("have a failure associated with the context", () -> {
            final Result result = SpectrumRunner.run(getSpecWithExplodingAfterAll());
            final Failure failure = result.getFailures().get(0);
            assertThat(failure.getDescription().getClassName(), is("Exploding afterAll"));
        });

        it("have a failure on the first exception", () -> {
            final Result result = SpectrumRunner.run(getSpecWithExplodingAfterAll());
            final Failure failure = result.getFailures().get(0);
            assertThat(failure.getMessage(), is("boom one"));
        });

    });

    describe("A spec with no tests", () -> {

        final List<String> items = new ArrayList<String>();
        final Block addItem = () -> { items.add("foo"); };

        describe("spec", () -> {
            beforeAll(addItem);
            beforeEach(addItem);
            afterEach(addItem);
            afterAll(addItem);
        });

        it("does not run fixture methods", () -> {
            assertThat(items, hasSize(0));
        });

    });

}

private static Class<?> getSpecWithExplodingBeforeEach(){
    class Spec {{
        beforeEach(() -> {
            throw new Exception("boom");
        });

        it("should fail", () -> {

        });

        it("should also fail", () -> {

        });

    }}
    return Spec.class;
}

private static Class<?> getSpecWithExplodingAfterEach(){
    class Spec {{
        afterEach(() -> {
            throw new Exception("boom");
        });

        it("should fail", () -> {

        });

        it("should also fail", () -> {

        });

    }}
    return Spec.class;
}

private static Class<?> getSpecWithExplodingBeforeAll(){
    class Spec {{

        describe("failing context", () ->{

            beforeAll(() -> {
                throw new Exception("boom");
            });

            beforeAll(() -> {
                throw new Exception("boom two");
            });

            it("should fail once", () -> {

            });

            it("should also fail", () -> {

            });

            describe("failing child", () -> {

                it("fails too", () -> {

                });

            });
        });

    }}
    return Spec.class;
}

private static Class<?> getSpecWithExplodingAfterAll(){
    class Spec {{

        describe("Exploding afterAll", () -> {

            afterAll(() -> {
                throw new Exception("boom one");
            });

            afterAll(() -> {
                throw new Exception("boom two");
            });

            it("should fail at the context level", () -> {

            });

            describe("passing child", () -> {

                it("passes", () -> {

                });

            });
        });

    }}
    return Spec.class;
}

}

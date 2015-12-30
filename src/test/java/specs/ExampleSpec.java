package specs;

import static com.greghaskins.spectrum.Spectrum.afterAll;
import static com.greghaskins.spectrum.Spectrum.afterEach;
import static com.greghaskins.spectrum.Spectrum.beforeAll;
import static com.greghaskins.spectrum.Spectrum.beforeEach;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.Spectrum.value;
import static com.greghaskins.spectrum.Spectrum.xit;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Spectrum.Block;
import com.greghaskins.spectrum.Spectrum.Value;

@RunWith(Spectrum.class)
public class ExampleSpec {{

    describe("A spec", () -> {

        final int foo = 1;

        it("is just a code block with a run() method", new Block() {
            @Override
            public void run() throws Throwable {
                assertEquals(1, foo);
            }
        });

        it("can also be a lambda function, which is a lot prettier", () -> {
            assertEquals(1, foo);
        });

        it("can use any assertion library you like", () -> {
            org.junit.Assert.assertEquals(1, foo);
            org.hamcrest.MatcherAssert.assertThat(true, is(true));
        });

        describe("nested inside a second describe", () -> {

            final int bar = 1;

            it("can reference both scopes as needed", () -> {
                assertThat(bar, is(equalTo(foo)));
            });

        });

        it("can have `it`s and `describe`s in any order", () -> {
            assertThat(foo, is(1));
        });

    });

    describe("A spec using beforeEach and afterEach", () -> {

        final List<String> items = new ArrayList<String>();

        beforeEach(() -> {
            items.add("foo");
        });

        beforeEach(() -> {
            items.add("bar");
        });

        afterEach(() -> {
            items.clear();
        });

        it("runs the beforeEach() blocks in order", () -> {
            assertThat(items, contains("foo", "bar"));
            items.add("bogus");
        });

        it("runs them before every test", () -> {
            assertThat(items, contains("foo", "bar"));
            items.add("bogus");
        });

        it("runs afterEach after every test", () -> {
            assertThat(items, not(contains("bogus")));
        });

        describe("when nested", () -> {

            beforeEach(() -> {
                items.add("baz");
            });

            it("runs beforeEach and afterEach from inner and outer scopes", () -> {
                assertThat(items, contains("foo", "bar", "baz"));
            });

        });

    });

    describe("The Value convenience wrapper", () -> {

        final Value<Integer> counter = value(Integer.class);

        beforeEach(() -> {
            counter.value = 0;
        });

        beforeEach(() -> {
            counter.value++;
        });

        it("lets you work around Java's requirement that closures only reference `final` variables", () -> {
            counter.value++;
            assertThat(counter.value, is(2));
        });

        it("can optionally have an initial value set", () -> {
            final Value<String> name = value("Alice");
            assertThat(name.value, is("Alice"));
        });

        it("has a null value if not specified", () -> {
            final Value<String> name = value(String.class);
            assertThat(name.value, is(nullValue()));
        });

    });

    describe("A spec using beforeAll", () -> {

        final List<Integer> numbers = new ArrayList<Integer>();

        beforeAll(() -> {
            numbers.add(1);
        });

        it("sets the initial state before any tests run", () -> {
            assertThat(numbers, contains(1));
            numbers.add(2);
        });

        describe("and afterAll", () -> {

            afterAll(() -> {
                numbers.clear();
            });

            it("does not reset anything between tests", () -> {
                assertThat(numbers, contains(1, 2));
                numbers.add(3);
            });

            it("so proceed with caution; this *will* leak shared state across tests", () -> {
                assertThat(numbers, contains(1, 2, 3));
            });
        });

        it("cleans up after running all tests in the describe block", () -> {
            assertThat(numbers, is(empty()));
        });

    });

    describe("Pending/ignored specs", () -> {

        xit("can be declared using `xit`", () -> {
            // this will not run
            assertThat(true, is(false));
        });

    });

}}

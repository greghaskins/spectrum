package specs;

import static com.greghaskins.spectrum.Spectrum.afterEach;
import static com.greghaskins.spectrum.Spectrum.beforeEach;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Spectrum.Block;


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

}}

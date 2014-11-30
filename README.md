Spectrum
========

[![Build Status](https://travis-ci.org/greghaskins/spectrum.svg?branch=master)](https://travis-ci.org/greghaskins/spectrum) [![Coverage Status](https://coveralls.io/repos/greghaskins/spectrum/badge.png)](https://coveralls.io/r/greghaskins/spectrum)

> A colorful spec-style test runner for Java

[Spectrum](https://github.com/greghaskins/spectrum) is inspired by the behavior-driven testing frameworks [Jasmine](https://jasmine.github.io/), [mocha](http://mochajs.org/), and [RSpec](http://rspec.info/), bringing their expressive syntax and functional style to Java tests. It is a custom runner for [JUnit](http://junit.org/), so it works with many development and reporting tools out of the box.

## Example

> see also [ExampleSpec.java](src/test/java/specs/ExampleSpec.java)

```java
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
```

## Dependencies

 - JUnit 4
 - Java 8

(The Spectrum runner itself should be compatible back to Java 6; verified support for specs written using [Retrolambda](https://github.com/orfjackal/retrolambda) is forthcoming.)

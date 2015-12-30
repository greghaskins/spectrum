Spectrum
========

[![Build Status](https://img.shields.io/travis/greghaskins/spectrum.svg)](https://travis-ci.org/greghaskins/spectrum) [![Coverage Status](https://img.shields.io/coveralls/greghaskins/spectrum.svg)](https://coveralls.io/r/greghaskins/spectrum) [![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

*A colorful BDD-style test runner for Java*

[Spectrum](https://github.com/greghaskins/spectrum) is inspired by the behavior-driven testing frameworks [Jasmine](https://jasmine.github.io/), [Kiwi](https://github.com/kiwi-bdd/Kiwi), and [RSpec](http://rspec.info/), bringing their expressive syntax and functional style to Java tests. It is a custom runner for [JUnit](http://junit.org/), so it works with many development and reporting tools out of the box.


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

}}
```

## Getting Started

Spectrum is available as a [package on jCenter](https://bintray.com/greghaskins/maven/Spectrum/view), so make sure you have jCenter declared as a repository in your build config. Future inclusion in Maven Central (see [#12](https://github.com/greghaskins/spectrum/issues/12)) will make this even easier.

### Dependencies

 - JUnit 4
 - Java 8

### Gradle

Make sure you have the jCenter repository in your [init script](https://docs.gradle.org/current/userguide/init_scripts.html) or project `build.gradle`:

```groovy
repositories {
    jcenter()
}
```

Then add the Spectrum dependency for your tests:

```groovy
dependencies {
	testCompile 'com.greghaskins:spectrum:0.5.0'
}

```

### Maven

Make sure you have the jCenter repository in your global `settings.xml` or project `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jcenter</id>
        <url>http://jcenter.bintray.com</url>
    </repository>
</repositories>
```

Then add Spectrum as a dependency with `test` scope in your `pom.xml`:

```xml
<project>
	<dependencies>
		<dependency>
			<groupId>com.greghaskins</groupId>
			<artifactId>spectrum</artifactId>
			<version>0.5.0</version>
			<scope>test</scope>
		</dependency>
	</dependencies>
</project>
```

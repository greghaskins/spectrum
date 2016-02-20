Spectrum
========

[![Build Status](https://img.shields.io/travis/greghaskins/spectrum.svg)](https://travis-ci.org/greghaskins/spectrum) [![Coverage Status](https://img.shields.io/coveralls/greghaskins/spectrum.svg)](https://coveralls.io/r/greghaskins/spectrum) [![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

*A colorful BDD-style test runner for Java*

[Spectrum](https://github.com/greghaskins/spectrum) is inspired by the behavior-driven testing frameworks [Jasmine](https://jasmine.github.io/), [Kiwi](https://github.com/kiwi-bdd/Kiwi), and [RSpec](http://rspec.info/), bringing their expressive syntax and functional style to Java tests. It is a custom runner for [JUnit](http://junit.org/), so it works with many development and reporting tools out of the box.


## Example

> from [ExampleSpecs.java](src/test/java/specs/ExampleSpecs.java)

```java
@RunWith(Spectrum.class)
public class ExampleSpecs {{

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

    describe("A suite using beforeEach and afterEach", () -> {

        final List<String> items = new ArrayList<>();

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

        it("runs them before every spec", () -> {
            assertThat(items, contains("foo", "bar"));
            items.add("bogus");
        });

        it("runs afterEach after every spec", () -> {
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

    describe("A suite using beforeAll", () -> {

        final List<Integer> numbers = new ArrayList<>();

        beforeAll(() -> {
            numbers.add(1);
        });

        it("sets the initial state before any specs run", () -> {
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

            it("so proceed with caution; this *will* leak shared state across specs", () -> {
                assertThat(numbers, contains(1, 2, 3));
            });
        });

        it("cleans up after running all specs in the describe block", () -> {
            assertThat(numbers, is(empty()));
        });

    });

}}
```

### Focused Specs

You can focus the runner on particular spec with `fit` or a suite with `fdescribe` so that only those specs get executed.

> from [FocusedSpecs.java](src/test/java/specs/FocusedSpecs.java)

```java
describe("Focused specs", () -> {

  fit("is focused and will run", () -> {
    assertThat(true, is(true));
  });

  it("is not focused and will not run", () -> {
    throw new Exception();
  });

  fdescribe("a focused suite", () -> {

    it("will run", () -> {
      assertThat(true, is(true));
    });

    it("all its specs", () -> {
      assertThat(true, is(true));
    });
  });

  fdescribe("another focused suite, with focused and unfocused specs", () -> {

    fit("will run focused specs", () -> {
      assertThat(true, is(true));
    });

    it("ignores unfocused specs", () -> {
      throw new Exception();
    });
  });
});
```

## Supported Features

Spectrum moving toward a `1.0` release with close alignment to Jasmine's test declaration API. The library already supports a nice subset of those features:

- [x] `describe`
- [x] `it`
- [x] `beforeEach` / `afterEach`
- [x] `beforeAll` / `afterAll`
- [x] `fit` / `fdescribe`
- [ ] `xit` / `xdescribe`

### Non-Features

Unlike some BDD-style frameworks, Spectrum is _only_ a test runner. Assertions, expectations, mocks, and matchers are the purview of other libraries.

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
	testCompile 'com.greghaskins:spectrum:0.6.1'
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
			<version>0.6.1</version>
			<scope>test</scope>
		</dependency>
	</dependencies>
</project>
```

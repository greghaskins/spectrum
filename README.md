Spectrum
========

[![Build Status](https://img.shields.io/travis/greghaskins/spectrum.svg)](https://travis-ci.org/greghaskins/spectrum) [![Codecov](https://img.shields.io/codecov/c/github/greghaskins/spectrum.svg)](https://codecov.io/gh/greghaskins/spectrum) [![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE) [![Download](https://api.bintray.com/packages/greghaskins/maven/Spectrum/images/download.svg) ](https://bintray.com/greghaskins/maven/Spectrum/_latestVersion) [![Gitter](https://img.shields.io/gitter/room/greghaskins/spectrum.svg)](https://gitter.im/greghaskins/spectrum)

*A colorful BDD-style test runner for Java*

[Spectrum](https://github.com/greghaskins/spectrum) is inspired by the behavior-driven testing frameworks [Jasmine](https://jasmine.github.io/) and [RSpec](http://rspec.info/), bringing their expressive syntax and functional style to Java tests. It is a custom runner for [JUnit](http://junit.org/), so it works with many development and reporting tools out of the box.

## Note: under active development

The latest released version is [1.0.2](https://github.com/greghaskins/spectrum/releases/tag/1.0.2). Please see the [README for 1.0.2](https://github.com/greghaskins/spectrum/tree/1.0.2) for details about the stable version. The content below applies to the latest development version on the `master` branch. If you'd like to help, we're glad to have it; please see [CONTRIBUTING.md](./CONTRIBUTING.md).

## Overview

Spectrum is a polyglot BDD test framework which converts specs in native Java 8
to a fully hierarchical test execution report in your IDE.

![Spectrum with Eclipse via JUnit](junit-screenshot.png)

## Getting Started

- Read the feature overview below
- Try our [quickstart walkthrough](doc/QuickstartWalkthrough.md)
- Jump into the JavaDoc in [Spectrum.java](src/main/java/com/greghaskins/spectrum/Spectrum.java)

## Example

> from [ExampleSpecs.java](src/test/java/specs/ExampleSpecs.java)

```java
@RunWith(Spectrum.class)
public class ExampleSpecs {
  {

    describe("A spec", () -> {

      final int foo = 1;

      it("is just a code block that verifies something", () -> {
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

  }
}
```

## Supported Features

The Spectrum API is designed to be familiar to Jasmine and RSpec users, while remaining compatible with JUnit. The features and behavior of those libraries help guide decisions on how Spectrum should work, both for common scenarios and edge cases. (See [the discussion on #41](https://github.com/greghaskins/spectrum/pull/41#issuecomment-238729178) for an example of how this factors into design decisions.)

The main functions for defining a test are:

- `describe` - a high level suite
- `it` - an individual spec
- `beforeEach` / `afterEach` - per spec set up/tear down
- `beforeAll` / `afterAll` - per suite set up/tear down
- `fit` / `fdescribe` - [for focusing](doc/FocusingAndIgnoring.md)
- `xit` / `xdescribe` - [for ignoring](doc/FocusingAndIgnoring.md)
- `let` / `Variable` - [for providing values](doc/VariablesAndValues.md)
- `with` / `ignore` / `focus` / `tags` - [for tagging blocks with metadata](doc/FocusingAndIgnoring.md)
- `feature` / `scenario` / `scenarioOutline` - [Gherkin Syntax](doc/Gherkin.md)
- `given` / `when` / `then` / `and` - [Gherkin Syntax](doc/Gherkin.md)
- `context` / `fcontext` / `xcontext` - grouping tests or suites

Spectrum also supports:

- Unlimited nesting of suites within suites
- Rigorous error handling and reporting when something unexpected goes wrong
- Compatibility with existing JUnit tools; no configuration required
- Plugging in familiar JUnit-friendly libraries like `Mockito` or `SpringJUnit` [via JUnit `@Rule`s handling](doc/JunitRules.md).
- Tagging specs for [selective running](doc/FocusingAndIgnoring.md)
- Mixing Spectrum tests and normal JUnit tests in the same project suite
- RSpec-style `aroundEach` and `aroundAll` hooks for advanced users and plugin authors

### Non-Features

Unlike some BDD-style frameworks, Spectrum is _only_ a test runner. Assertions, expectations, mocks, and matchers are the purview of other libraries such as [Hamcrest](http://hamcrest.org/JavaHamcrest/), [AssertJ](http://joel-costigliola.github.io/assertj/), [Mockito](http://mockito.org/), or [plain JUnit](https://github.com/junit-team/junit4/wiki/Assertions).

## Installation

Spectrum is available as a [package on jCenter](https://bintray.com/greghaskins/maven/Spectrum/view), so make sure you have jCenter declared as a repository in your build config. Future inclusion in Maven Central (see [#12](https://github.com/greghaskins/spectrum/issues/12)) will make this even easier.

### Dependencies

 - JUnit 4
 - Java 8 (for your tests; systems under test can use older versions)

### Gradle

Add the Spectrum dependency to your `testCompile` configuration in `build.gradle`:

```groovy
dependencies {
  testCompile 'com.greghaskins:spectrum:1.0.2'
}
```

### Maven

Add Spectrum as a dependency with `test` scope in your `pom.xml`:

```xml
<project>
  <dependencies>
    <dependency>
      <groupId>com.greghaskins</groupId>
      <artifactId>spectrum</artifactId>
      <version>1.0.2</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
</project>
```

## Can I Contribute?

Yes please! See [CONTRIBUTING.md](./CONTRIBUTING.md).

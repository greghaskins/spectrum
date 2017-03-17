Spectrum
========

[![Build Status](https://img.shields.io/travis/greghaskins/spectrum.svg)](https://travis-ci.org/greghaskins/spectrum) [![Codecov](https://img.shields.io/codecov/c/github/greghaskins/spectrum.svg)](https://codecov.io/gh/greghaskins/spectrum) [![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE) [![Download](https://api.bintray.com/packages/greghaskins/maven/Spectrum/images/download.svg) ](https://bintray.com/greghaskins/maven/Spectrum/_latestVersion) [![Gitter](https://img.shields.io/gitter/room/greghaskins/spectrum.svg)](https://gitter.im/greghaskins/spectrum)

*A colorful BDD-style test runner for Java*

[Spectrum](https://github.com/greghaskins/spectrum) is inspired by the behavior-driven testing frameworks [Jasmine](https://jasmine.github.io/) and [RSpec](http://rspec.info/), bringing their expressive syntax and functional style to Java tests. It is a custom runner for [JUnit](http://junit.org/), so it works with many development and reporting tools out of the box.

![Spectrum with Eclipse via JUnit](junit-screenshot.png)

<!---freshmark main
output = input.replace(/\b\d+\.\d+\.\d+\b/g, '{{stableVersion}}');
-->

- [Quickstart Guide](https://github.com/greghaskins/spectrum/tree/1.0.2/docs/QuickstartWalkthrough.md)
- [Documentation](https://github.com/greghaskins/spectrum/tree/1.0.2/docs)
- [Release Notes](https://github.com/greghaskins/spectrum/releases/tag/1.0.2)
- [Source Code](https://github.com/greghaskins/spectrum/tree/1.0.2)

## Examples

Spectrum supports Specification-style tests similar to [RSpec](http://rspec.info/) and [Jasmine](https://jasmine.github.io/):

```java
@RunWith(Spectrum.class)
public class Specs {{

  describe("A list", () -> {

    List<String> list = new ArrayList<>();

    afterEach(list::clear);

    it("should be empty by default", () -> {
      assertThat(list.size(), is(0));
    });

    it("should be able to add items", () -> {
      list.add("foo");
      list.add("bar");

      assertThat(list, contains("foo", "bar"));
    });

  });
}}
```

And also Gherkin-style tests similar to [Cucumber](https://cucumber.io/docs/reference):

```java
@RunWith(Spectrum.class)
public class Features {{

  feature("Lists", () -> {

    scenario("adding items", () -> {

      Variable<List<String>> list = new Variable<>();

      given("an empty list", () -> {
        list.set(new ArrayList<>());
      });

      when("you add the item 'foo'", () -> {
        list.get().add("foo");
      });

      and("you add the item 'bar'", () -> {
        list.get().add("bar");
      });

      then("it contains both foo and bar", () -> {
        assertThat(list.get(), contains("foo", "bar"));
      });

    });

  });
}}
```

For more details and examples, see the [documentation](https://github.com/greghaskins/spectrum/tree/1.0.2/docs).

## Releases

Spectrum is available as a [package on jCenter](https://bintray.com/greghaskins/maven/Spectrum/view) and [Maven Central](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.greghaskins%22%20AND%20a%3A%22spectrum%22). Release notes can be found on the GitHub [Releases Page](https://github.com/greghaskins/spectrum/releases).

## Can I Contribute?

Yes please! See [CONTRIBUTING.md](./CONTRIBUTING.md).

<!---freshmark /main -->

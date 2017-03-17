# Spectrum Features

> from [ExampleSpecs.java](../src/test/java/specs/ExampleSpecs.java)

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
- `fit` / `fdescribe` - [for focusing](docs/FocusingAndIgnoring.md)
- `xit` / `xdescribe` - [for ignoring](docs/FocusingAndIgnoring.md)
- `let` / `Variable` - [for providing values](docs/VariablesAndValues.md)
- `with` / `ignore` / `focus` / `tags` - [for tagging blocks with metadata](docs/FocusingAndIgnoring.md)
- `feature` / `scenario` / `scenarioOutline` - [Gherkin Syntax](docs/Gherkin.md)
- `given` / `when` / `then` / `and` - [Gherkin Syntax](docs/Gherkin.md)
- `context` / `fcontext` / `xcontext` - grouping tests or suites

Spectrum also supports:

- Unlimited nesting of suites within suites
- Rigorous error handling and reporting when something unexpected goes wrong
- Compatibility with existing JUnit tools; no configuration required
- Plugging in familiar JUnit-friendly libraries like `Mockito` or `SpringJUnit` [via JUnit `@Rule`s handling](docs/JunitRules.md).
- Tagging specs for [selective running](docs/FocusingAndIgnoring.md)
- Mixing Spectrum tests and normal JUnit tests in the same project suite
- RSpec-style `aroundEach` and `aroundAll` hooks for advanced users and plugin authors

### Non-Features

Unlike some BDD-style frameworks, Spectrum is _only_ a test runner. Assertions, expectations, mocks, and matchers are the purview of other libraries such as [Hamcrest](http://hamcrest.org/JavaHamcrest/), [AssertJ](http://joel-costigliola.github.io/assertj/), [Mockito](http://mockito.org/), or [plain JUnit](https://github.com/junit-team/junit4/wiki/Assertions).

# Spectrum Specification DSL

Spectrum provides a Specification-style test DSL similar to [RSpec](http://rspec.info/) and [Jasmine](https://jasmine.github.io/), accessible from the [`Specification`](../src/main/java/com/greghaskins/spectrum/dsl/specification/Specification.java) interface. The API is designed to be familiar to users of those tools, while remaining compatible with JUnit. The features and behavior of those libraries help guide decisions on how Spectrum should work, both for common scenarios and edge cases. (See [the discussion on #41](https://github.com/greghaskins/spectrum/pull/41#issuecomment-238729178) for an example of how this factors into design decisions.)

## API

- `describe` - a high level suite
- `context` - alias of `describe` for nesting and grouping
- `it` - an individual spec
- `beforeEach` / `afterEach` - per-spec setup/teardown
- `beforeAll` / `afterAll` - per-suite setup/teardown
- `let` / `eagerLet` / `Variable` - [for providing values to tests](VariablesAndValues.md)
- `fit` / `fdescribe` / `fcontext` - [for focusing](FocusingAndIgnoring.md)
- `xit` / `xdescribe` / `xcontext` - [for ignoring](FocusingAndIgnoring.md)
- `with` / `ignore` / `focus` / `tags` - [for tagging blocks with metadata](FocusingAndIgnoring.md)

## Examples

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

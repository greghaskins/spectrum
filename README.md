Spectrum
========

[![Build Status](https://img.shields.io/travis/greghaskins/spectrum.svg)](https://travis-ci.org/greghaskins/spectrum) [![Coverage Status](https://img.shields.io/coveralls/greghaskins/spectrum.svg)](https://coveralls.io/r/greghaskins/spectrum) [![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE) [![Download](https://api.bintray.com/packages/greghaskins/maven/Spectrum/images/download.svg) ](https://bintray.com/greghaskins/maven/Spectrum/_latestVersion) [![Gitter](https://img.shields.io/gitter/room/greghaskins/spectrum.svg)](https://gitter.im/greghaskins/spectrum)

*A colorful BDD-style test runner for Java*

[Spectrum](https://github.com/greghaskins/spectrum) is inspired by the behavior-driven testing frameworks [Jasmine](https://jasmine.github.io/) and [RSpec](http://rspec.info/), bringing their expressive syntax and functional style to Java tests. It is a custom runner for [JUnit](http://junit.org/), so it works with many development and reporting tools out of the box.

## Note: under active development

The latest released version is [1.0.0](https://github.com/greghaskins/spectrum/releases/tag/1.0.0). Please see the [README for 1.0.0](https://github.com/greghaskins/spectrum/tree/1.0.0) for details about the stable version. The content below applies to the latest development version on the `master` branch. If you'd like to help, we're glad to have it; please see [CONTRIBUTING.md](./CONTRIBUTING.md).


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

### Screenshot

![Spectrum with Eclipse via JUnit](junit-screenshot.png)

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

### Ignored Specs

Spectrum supports RSpec style ignoring and focusing of specs and suites. It also has a Spectrum native style which overlaps with its tagging and selecive running capability.

#### Ignored Specs RSpec style

You can ignore a spec with `xit` or ignore all the specs in a suite with `xdescribe`. Prefixing with `f` will focus execution.
The `pending` function is also available. Unlike declaring a spec as ignored as part of its set up, `pending` will
abort the execution of a spec as a JUnit assumption failure.

> from [IgnoredSpecs.java](src/test/java/specs/IgnoredSpecs.java)

```java
describe("Ignored specs", () -> {

    xit("is ignored and will not run", () -> {
        throw new Exception();
    });

    it("is not ignored and will run", () -> {
        assertThat(true, is(true));
    });

    it("is marked as pending and will abort but will run a bit", () -> {
      pending();
      assertThat(true, is(true));
    });

    xdescribe("an ignored suite", () -> {

        it("will not run", () -> {
            throw new Exception();
        });

        describe("with nesting", () -> {
            it("all its specs", () -> {
                throw new Exception();
            });

            fit("including focused specs", () -> {
                throw new Exception();
            });
        });
    });
});
```
#### Ignoring Specs Spectrum style

Spectrum allows you to specify pre-conditions on a block. These preconditions can include tagging.
The `with(PreConditions,Block)` function is used to annotate a block with the preconditions. As ignoring is a common case, there is also an `ignore` function you can
wrap around a block which has the same effect as using `with(ignore(), ...)`.
For ignoring or focusing specs, the syntax is:

```java
      describe("Has suite with ignored specs", () -> {
        it("is not ignored", () -> {
        });

        it("is ignored", with(ignore(), () -> {
        }));

        it("is ignored for a reason", with(ignore("not important for this release"), () -> {
        }));

        it("is a block ignored as a block", ignore(() -> {
        }));

        it("is a block ignored as a block for a reason", ignore("Not ready yet", () -> {
        }));
      });

      // and for focus
      describe("Has suite with focused spec", () -> {
              it("is focused", with(focus(), () -> {
              }));
      });
```
### Tagging and Selective Running

Tagging is another precondition that can be added to the block of a `describe`, `it`, `scenario`, call. The vanilla example of tagging syntax would be:

```java
it("is tagged", with(tags("tag1"), () -> {
    // some test
})));
```

The tagging metadata is presently used to control which parts of the spec are run. There are two controls over what is run,
complementary to any focus or ignore that's hard-coded into the spec.

* Include tags - when set, only suites that have at least one tag in this list can be run
* Exclude tags - when set, any suite or spec that has an excluded tag will be ignored

The rules for selective running can be set by:

* System property (See [SpectrumOptions.java](src/main/java/com/greghaskins/spectrum/SpectrumOptions.java))
  * This will be the common use case for CI Builds
  * Set `spectrum.tags.include` and `spectrum.tags.exclude` to be a comma separated list of tags
  * This is likely done using a -D option on the java invocation
* Function call (See [Configuration.java](src/main/java/com/greghaskins/spectrum/SpectrumOptions.java)) and `Spectrum.configure()`
  * `configure().includeTags("foo")` and `configure().excludeTags("bar")` allow the rules to vary based on where they are called in the declaration block.

Tags allow you run different categories of specs in different test runs, either through the
configuration of your build - usually with system property - or with hard coding in the test class or specs themselves.

Example: temporarily making only WIP tests run in a test class

```java
  @RunWith(Spectrum.class)
  public TestClass {
     {
        configure().includeTags("wip");

        describe("wip suite", with(tags("wip"), () -> {
           // tests here are run
           it("is a spec with no tags", () -> {
               // this is still run because its parent has the tags
           });
           if("is a spec with a tag", with(tags("slow", () -> {
                // in this case, this is run, but if
                // excludeTags was set to have "slow"
                // then it would not be allowed to run
           })));
        }));

        describe("some other suite", with(tags("wrongTags", () -> {
           // these are not run
        }));

        describe("untagged suite", () -> {
           // this suite is untagged so does not meet the requirement
        });
     }
  }
```
### Paramaterized Specs

Paramaterization involves supplying some examples via the `withExamples` function. These examples are objects with between 1 and 8 values. This allows you to:

- Provide a list of objects that parameterize your specs
- Provide a table of values which are turned into ad-hoc tuples with test data in

Thanks to Java 8's generic type system, you can use the examples to drive the type of paramaterized block you need to provide.

E.g.

```java
// provides examples of two ints and a String
withExamples(
  example(1,2,"12"),
  example(2,3,"23")
)
```

The above expects you to provide a parameterized block to use the parameters which has three parameters, two of which are integer and the other is String. This might formally be declared as:

```java
(int i1, int i3, String s) -> { ... }
```

But you don't need to provide the types, so it's more tersely written as:

```java
(i1, it2, s) -> { ... }
```

Think of the `withExamples` and `example` syntax as being a table structure with coherent types within each column. Think of the lambda you write to receive those parameters as the table's header. Intuitively you'd expect something like:

```java
         (num1, num2, num3) -> { ... },
withExamples(
  example(1,    2,    3),
  example(2,    3,    4)
)
```

This is how the syntax of both `describeParameterized` and `scenarioOutline` work. You provide a consuming block to take the values for each example and define specs with those values, and you provide the values as examples.

For examples of this in action, see [ParameterizedSpecs.java](src/test/java/specs/ParameterizedSpecs.java).

### Common Variable Initialization

The `let` helper function makes it easy to initialize common variables that are used in multiple specs. This also helps work around Java's restriction that closures can only reference `final` variables in the containing scope. Values are cached within a spec, and lazily re-initialized between specs as in [RSpec #let](http://rspec.info/documentation/3.5/rspec-core/RSpec/Core/MemoizedHelpers/ClassMethods.html#let-instance_method).

> from [LetSpecs.java](src/test/java/specs/LetSpecs.java)

```java
describe("The `let` helper function", () -> {

  final Supplier<List<String>> items = let(() -> new ArrayList<>(asList("foo", "bar")));

  it("is a way to supply a value for specs", () -> {
    assertThat(items.get(), contains("foo", "bar"));
  });

  it("caches the value so it doesn't get created multiple times for the same spec", () -> {
    assertThat(items.get(), is(sameInstance(items.get())));

    items.get().add("baz");
    items.get().add("blah");
    assertThat(items.get(), contains("foo", "bar", "baz", "blah"));
  });

  it("creates a fresh value for every spec", () -> {
    assertThat(items.get(), contains("foo", "bar"));
  });
});
```

For cases where you need to access a shared variable across specs or steps, the `Variable` helper class provides a simple `get`/`set` interface. This may be required, for example, to initialize shared state in a `beforeAll` that is used across multiple specs in that suite. Of course, you should exercise caution when sharing state across tests

> from [VariableSpecs.java](src/test/java/specs/VariableSpecs.java)

```java
describe("The Variable convenience wrapper", () -> {

  final Variable<Integer> counter = new Variable<>();

  beforeAll(() -> {
    counter.set(0);
  });

  beforeEach(() -> {
    final int previousValue = counter.get();
    counter.set(previousValue + 1);
  });

  it("lets you work around Java's requirement that closures only use `final` variables", () -> {
    assertThat(counter.get(), is(1));
  });

  it("can share values across scopes, so use it carefully", () -> {
    assertThat(counter.get(), is(2));
  });

  it("can optionally have an initial value set", () -> {
    final Variable<String> name = new Variable<>("Alice");
    assertThat(name.get(), is("Alice"));
  });

  it("has a null value if not specified", () -> {
    final Variable<String> name = new Variable<>();
    assertNull(name.get());
  });

});
```

### Gherkin Syntax

Spectrum also provides a Gherkin-style test DSL, accessible from the `GherkinSyntax` interface. In this syntax, tests are declared with `feature`, `scenario`, `given`, `when`, `then`, and ... `and`.

> from [GherkinExampleSpecs.java](src/test/java/specs/GherkinExampleSpecs.java)

```java
feature("Gherkin-like test DSL", () -> {

  scenario("using given-when-then steps", () -> {
    final AtomicInteger integer = new AtomicInteger();
    given("we start with a given", () -> {
      integer.set(12);
    });
    when("we have a when to execute the system", () -> {
      integer.incrementAndGet();
    });
    then("we can assert the outcome", () -> {
      assertThat(integer.get(), is(13));
    });
  });

  scenario("using variables within the scenario to pass data between steps", () -> {
    final Variable<String> theData = new Variable<>();

    given("the data is set", () -> {
      theData.set("Hello");
    });

    when("the data is modified", () -> {
      theData.set(theData.get() + " world!");
    });

    then("the data can be seen with the new value", () -> {
      assertThat(theData.get(), is("Hello world!"));
    });

    and("the data is still available in subsequent steps", () -> {
      assertThat(theData.get(), is("Hello world!"));
    });
  });

});
```

When using the Gherkin syntax, each `given`/`when`/`then` step must pass before the next is run. Note that they must be declared inside a `scenario` block to work correctly. Multiple `scenario` blocks can be defined as part of a `feature`.

Parameterized Gherkin scenarios are achieved with `scenarioOutline` see [ParameterizedSpecs.java](src/test/java/specs/ParameterizedSpecs.java).

## Supported Features

The Spectrum API is designed to be familiar to Jasmine and RSpec users, while remaining compatible with JUnit. The features and behavior of those libraries help guide decisions on how Specturm should work, both for common scenarios and edge cases. (See [the discussion on #41](https://github.com/greghaskins/spectrum/pull/41#issuecomment-238729178) for an example of how this factors into design decisions.)

The main functions for defining a test are:

- `describe` / `describeParamaterized`
- `it`
- `beforeEach` / `afterEach`
- `beforeAll` / `afterAll`
- `fit` / `fdescribe`
- `xit` / `xdescribe`
- `let`
- `feature` / `scenario` / `scenarioOutline`
- `given` / `when` / `then`
- `context` / `fcontext` / `xcontext`

Spectrum also supports:

- Unlimited nesting of suites within suites
- Rigorous error handling and reporting when something unexpected goes wrong
- Compatibility with existing JUnit tools; no configuration required
- Mixing Spectrum tests and normal JUnit tests in the same project suite
- RSpec-style `aroundEach` and `aroundAll` hooks for advanced users and plugin authors

### Non-Features

Unlike some BDD-style frameworks, Spectrum is _only_ a test runner. Assertions, expectations, mocks, and matchers are the purview of other libraries such as [Hamcrest](http://hamcrest.org/JavaHamcrest/), [AssertJ](http://joel-costigliola.github.io/assertj/), [Mockito](http://mockito.org/), or [plain JUnit](https://github.com/junit-team/junit4/wiki/Assertions).

## Getting Started

Spectrum is available as a [package on jCenter](https://bintray.com/greghaskins/maven/Spectrum/view), so make sure you have jCenter declared as a repository in your build config. Future inclusion in Maven Central (see [#12](https://github.com/greghaskins/spectrum/issues/12)) will make this even easier.

### Dependencies

 - JUnit 4
 - Java 8 (for your tests; systems under test can use older versions)

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
  testCompile 'com.greghaskins:spectrum:1.0.0'
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
      <version>1.0.0</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
</project>
```

## Can I Contribute?

Yes please! See [CONTRIBUTING.md](./CONTRIBUTING.md).

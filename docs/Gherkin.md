## Gherkin

Spectrum provides a Gherkin-style test DSL, accessible from the `Gherkin` interface. In this syntax, tests are declared with `feature`, `scenario`, `given`, `when`, `then`, and ... `and`.

When using the Gherkin DSL, each `given`/`when`/`then` step must pass before the next is run. Note that they must be declared inside a `scenario` block to work correctly. Multiple `scenario` blocks can be defined as part of a `feature`.

### Gherkin Examples

> from [GherkinExampleSpecs.java](../src/test/java/specs/GherkinExampleSpecs.java)

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

### Scenario Outline - Parameterized

> from [ParameterizedExampleSpecs.java](src/test/java/specs/ParameterizedExampleSpecs.java)

```java
scenarioOutline("Cucumber eating",
    (start, eat, remaining) -> {

      Variable<CukeEater> me = new Variable<>();

      given("there are " + start + " cucumbers", () -> {
        me.set(new CukeEater(start));
      });

      when("I eat " + eat + " cucumbers", () -> {
        me.get().eatCucumbers(eat);
      });

      then("I should have " + remaining + " cucumbers", () -> {
        assertThat(me.get().remainingCucumbers(), is(remaining));
      });
    },

    withExamples(
        example(12, 5, 7),
        example(20, 5, 15))

);

scenarioOutline("Simple calculations",
    (expression, expectedResult) -> {

      Variable<Calculator> calculator = new Variable<>();
      Variable<Number> result = new Variable<>();

      given("a calculator", () -> {
        calculator.set(new Calculator());
      });
      when("it computes the expression " + expression, () -> {
        result.set(calculator.get().compute(expression));
      });
      then("the result is " + expectedResult, () -> {
        assertThat(result.get(), is(expectedResult));
      });

    },

    withExamples(
        example("1 + 1", 2),
        example("5 * 9", 45),
        example("7 / 2", 3.5)
    )
);
```

Parameterization involves supplying some examples via the `withExamples` function. These examples are objects with between 1 and 8 values. This allows you to:

- Provide a list of objects that parameterize your specs
- Provide a table of values which are used as test inputs

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

This is how `scenarioOutline` works. You provide a consuming block to take the values for each example and define specs with those values, then you provide the values as examples.

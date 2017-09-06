# Test Timeouts

## JUnit Timeouts

In JUnit, you usually specify test timeout in the `@Test` annotation:

```java
@Test(timeout=123)
public void myTest() {
  ...
}

@Test(timeout=123)
public void myOtherTest() {
  ...
}
```

There is also a `Timeout` rule - [see here](https://github.com/junit-team/junit4/wiki/timeout-for-tests) for more information.

## Timeouts in Spectrum

Spectrum's timeout can be applied at the level of each _leaf node_ in the hierachy like the above:

```java
describe("some suite", () -> {
  it("does one thing under timeout", with(timeout(ofMillis(123)), () -> {
    ...
  }));

  it("does another under timeout", with(timeout(ofMillis(123)), () -> {
    ...
  }));

});
```
The timeout comes inside the configuration block using the `with` syntax. The duration of the timeout is a Java 8 `Duration` object, constructed
(as in the above example) using a static method from the `Duration` class like `ofMillis` or `ofSeconds`.

The major difference between JUnit and Spectrum timeouts is the ability to apply timeout rules for the family of tests inside a `describe` or `context`:

```java
// NOTE: the timeout applies to each test, not the sum of all
describe("some suite", with(timeout(ofMillis(123)), () -> {
  it("does one thing under timeout", () -> {
    ...
  });

  it("does another under timeout", () -> {
    ...
  });

}));
```

> See also [Configuration](Configuration.md)

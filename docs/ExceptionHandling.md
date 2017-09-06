# Exception handling in tests

Often in unit testing, we want to check that the system under test throws an error in a certain situation.

In native JUnit there are a couple of ways you can specify the expected exceptions of a test:

```java
// tag the test like this
@Test(expected=SomeException.class)
public void throwingTest() {}

// or use the expected exception rule in the test class
@Rule
public ExpectedException expected = ExpectedException.none();

@Test
public void throwingTest() {
  expected.expect(RuntimeException.class);
}
```

These are great. **They do not work with Spectrum!!!**

Even though Spectrum supports JUnit `@Rule` both in the test class and with the `junitMixin` function, the JUnit `ExpectedException` mechanism does not work. This is because the test has already failed owing to Spectrum's error handling before the JUnit exception rule gets a chance to say it's _ok_.

## Use assertion libraries instead

Your favourite assertion library may already solve this problem. Spectrum does not require or recommend an assertion library, so you can use whatever you with. AssertJ's `assertThatThrownBy` is a good fit for this.

E.g.

```java
it("expects a boom", () -> {
   assertThatThrownBy(() -> { throw new Exception("boom!"); }).isInstanceOf(Exception.class)
                                                             .hasMessageContaining("boom");
}
```

## Spectrum Expected Exceptions

Spectrum provides the following syntax for expecting exceptions in your specs:

```java
describe("Some suite", () -> {
  // in the parent of the test in the hierarchy, you add a call to `expectExceptions()`
  // this gives you an `ExceptionExpectation` object you can set expectations in
  ExceptionExpectation expectation = expectExceptions();
  it("correctly goes bang", () -> {
    // set the expectations here
    expectation.expectMessage("bang");
    throw new RuntimeException("bang");
  });
});
```

The `ExceptionExpectation` object is refreshed for each spec and is hooked into the execution of the specs so that if they end in an unexpected exception, there's a custom failure and if they do not end in an exception when they should, there's also a failure.

If you do not call any of the `expect...` functions on `ExceptionExpectation` then your spec can end without exception and pass. The default is for there to be no expectation whatsoever.

All calls to `expect...` **add** to the existing expectations. There's an `expect(CharSequence, Predicate)` overload so you can add a validating `Predicate` of your own beyond the existing methods provided.

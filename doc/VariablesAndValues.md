## Variables and Values

As `Spectrum` is built using Java 8 lambdas, there are some constraints on the passing of values
from one lambda to another.

Java requires _effectively final_ variables, which means that any value that can be manipulated
within a lambda needs to be boxed.

In general, tests should not share state, though the `Variable` class allows for that, which helps
when the test is broken into separate steps.

The `let` function is used to initialise a fresh, isolated, object for each spec.

### Common Variable Initialization

The `let` helper function makes it easy to initialize common variables that are used in multiple specs. In standard JUnit you might expect to use the initializer list of the class or a `@Before`	method to achieve the same. As there is no easy way for `beforeAll` or `beforeEach` to instantiate a value that will be used in the specs, `let` is the tool of choice.

Values are cached within a spec, and lazily re-initialized between specs as in [RSpec #let](http://rspec.info/documentation/3.5/rspec-core/RSpec/Core/MemoizedHelpers/ClassMethods.html#let-instance_method).

> from [LetSpecs.java](../src/test/java/specs/LetSpecs.java)

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

> from [VariableSpecs.java](../src/test/java/specs/VariableSpecs.java)

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
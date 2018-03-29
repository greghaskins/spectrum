## Variables and Values

As `Spectrum` is built using Java 8 lambdas, there are some constraints on the passing of values
from one lambda to another.

Java requires _effectively final_ variables, which means that any value that can be manipulated
within a lambda needs to be boxed.

In general, tests should not share state, though the `Variable` class allows for that, which helps
when the test is broken into separate steps.

The `let` function is used to initialise a fresh, isolated, object for each spec.

### Common Variable Initialization
#### Let
The `let` helper function makes it easy to initialize common variables that are used in multiple
specs. In standard JUnit you might expect to use the initializer list of the class or a `@Before`
method to achieve the same. As there is no easy way for `beforeAll` or `beforeEach` to instantiate
a value that will be used in the specs, `let` is the tool of choice.

Values are cached within a spec, and lazily re-initialized between specs as in
[RSpec #let](http://rspec.info/documentation/3.5/rspec-core/RSpec/Core/MemoizedHelpers/ClassMethods.html#let-instance_method).

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

#### Eager Let
If you need to ensure that a value is initialized at the start of a test, you can use the `eagerLet`
helper function, which has the same semantics as `let` but is evaluated prior to `beforeEach`. This
is often useful when you need to initialize values you can use in your `beforeEach` block. The value
is still initialized after any `beforeAll` blocks.

This is similar to
[RSpec #let!](http://rspec.info/documentation/3.5/rspec-core/RSpec/Core/MemoizedHelpers/ClassMethods.html#let!-instance_method).

> from [EagerLetSpecs.java](../src/test/java/specs/EagerLetSpecs.java)

```java
describe("The `eagerLet` helper function", () -> {
  final Supplier<List<String>> items = eagerLet(() -> new ArrayList<>(asList("foo", "bar")));

  final Supplier<List<String>> eagerItemsCopy = eagerLet(() -> new ArrayList<>(items.get()));

  context("when `beforeEach`, `let`, and `eagerLet` are used", () -> {
    final Supplier<List<String>> lazyItemsCopy =
      let(() -> new ArrayList<>(items.get()));

    beforeEach(() -> {
      // This would throw a NullPointerException if it ran before eagerItems
      items.get().add("baz");
    });

    it("evaluates all `eagerLet` blocks at once", () -> {
      assertThat(eagerItemsCopy.get(), contains("foo", "bar"));
    });

    it("evaluates `beforeEach` after `eagerLet`", () -> {
      assertThat(items.get(), contains("foo", "bar", "baz"));
    });

    it("evaluates `let` upon first use", () -> {
      assertThat(lazyItemsCopy.get(), contains("foo", "bar", "baz"));
    });
  });

  context("when `beforeAll` and `eagerLet` are used", () -> {
    beforeAll(() -> {
      assertThat(items.get(), is(nullValue()));
      assertThat(eagerItemsCopy.get(), is(nullValue()));
    });

    it("evaluates `beforeAll` prior to `eagerLet`", () -> {
      assertThat(items.get(), is(not(nullValue())));
      assertThat(eagerItemsCopy.get(), is(not(nullValue())));
    });
  });
});
```

#### Variable
For cases where you need to access a shared variable across specs or steps, the `Variable` helper
class provides a simple `get`/`set` interface. This may be required, for example, to initialize
shared state in a `beforeAll` that is used across multiple specs in that suite. Of course, you
should exercise caution when sharing state across tests

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

### When `get` is getting you down

`Variable` objects and the `Supplier` returned by `let` require use of the `get` function to read their contents. This requires you to write 	`get()` throughout
your code, which can lead to a little bloat.

If the object in question can be used via an interface, then you can use the `unbox` function inside `Unboxer` to wrap the supplier with a proxy. This will reduce your boilerplate code.

E.g.

```java
Supplier<List<String>> list = let(ArrayList::new);

it("can use the object", () -> {
  list.get().add("Hello");
  assertThat(list.get().get(0), is("Hello"));
});
```

can be replaced by

```java
List<String> list = unbox(let(ArrayList::new), List.class);

it("can use the object as though it was not in a supplier", () -> {
  list.add("Hello");
  assertThat(list.get(0), is("Hello"));
});
```

The `unbox` method can be used with any `Supplier<>`. There is also an overload of `let` as a short form for this:

```java
List<String> list = let(ArrayList::new, List.class);

it("can use the object as though it was not in a supplier", () -> {
  list.add("Hello");
  assertThat(list.get(0), is("Hello"));
});
```

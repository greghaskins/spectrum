## Configuration

Metadata to influence the execution can be added anywhere in the test hierarchy. Wherever you can add a block containing suites, tests, or a test body, you can also tag that block with configuration to apply from that point in the hierarchy downwards.

The common use case for this would be selective running, where tagging with `ignore` or `tags` would control which parts of the hierarchy are executed in a given test run. See [selective running](FocusingAndIgnoring.md) for more information.

### Adding configuration to blocks

The basic format is to take the block inside a `describe` or `it` block (and others) and wrap it inside a `with`. E.g.

```java
describe("Some parent", () -> {
  it("tests something", () -> {});
});

// might be tagged at parent level and become
describe("Some parent", with(focus(), () -> {
  it("tests something", () -> {});
}));
```

Configurations can be chained by using the `and` function, for example this spec has both tags and a timeout:

```java
describe("A suite", with(tags("someTag").and(timeout(10, TimeUnit.SECONDS)), () -> {
  it("will test something in time", () -> {
    ...
  });
}));
```

`and` can be chained further:

```java
describe("A suite", with(
  tags("someTag")
    .and(timeout(ofSeconds(10)))
    .and(focus()),
    () -> {
      ...
    }
));
```

### Scope of configuration

A configuration applies to the current node in the hierarchy and its children. With `ignore`, the effect of ignoring a parent is to ignore all children. With `tag`, the tagging is cumulative. With `timeout` the timeout settings in a parent propagate down to all descendants but can be superseded by a child's own configuration.

The general rule is that the configuration applies to the whole hierarchy and can only be added to or superseded by children that are allowed to execute.

### Configurations available

In addition to:

- `ignore()` - ignore this test
- `ignore(String reason)` - ignore this test with a reason
- `focus()` - run focused specs only, especially this
- `tags(String ... tags)` - tag the spec with labels

there is also:

- `timeout(Duration timeout)` - make the test fail if it takes too long - see [Timeout](Timeout.md)

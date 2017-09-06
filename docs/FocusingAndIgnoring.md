## Focusing, Ignoring and Tagging

Use cases include:

- Marking a single part of the spec as focused during development to re-run it quickly
- Marking individual specs as ignored while they're incomplete or not working
- Executing different combinations of specs in different builds - perhaps for CI

Spectrum supports the `f` and `x` prefix of `RSpec` and also provides its own tagging mechanism - `with` - which tags specs with metadata.

### Focused Specs

You can set the focus on particular spec with `fit` or a suite with `fdescribe` so that only those specs get executed.

> from [FocusedSpecs.java](../src/test/java/specs/FocusedSpecs.java)

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

You can ignore a spec with `xit` or ignore all the specs in a suite with `xdescribe`.

The `pending` function is another option. Unlike declaring a spec as ignored as part of its set up, `pending` will
abort the execution of a spec as a JUnit assumption failure.

> from [IgnoredSpecs.java](../src/test/java/specs/IgnoredSpecs.java)

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

      // never executed
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

Spectrum allows you to specify preconditions for execution as part of the configuration of a block. These can include tagging.
The `with(BlockConfigurationChain,Block)` function is used to annotate a block, for example:

```java
describe("an ignored suite", with(ignore(), () -> {
  ...
}));
```

For the full capabilities of adding configuration to blocks see [Configuration](Configuration.md).

Available tagging for ignoring:

- `ignore()` - ignore this test
- `ignore(String reason)` - ignore this test and provide a reason to the reader
- `focus()` - exclude specs not marked with `focus` and run focused specs only
- `tags(String ... tags)` - tag the spec with labels so it can be run selectively according to tag selection

As ignoring is a common case, you can use the `ignore` function around a block, which has the same effect as using `with(ignore(), ...)`.
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

Example:

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

* System property (See [Configure.java](../src/main/java/com/greghaskins/spectrum/Configure.java))
  * This will be the common use case for CI Builds
  * Set `spectrum.tags.include` and `spectrum.tags.exclude` to be a comma separated list of tags
  * This is likely done using a `-D` option on the java invocation
* Function call (See [Configure.java](../src/main/java/com/greghaskins/spectrum/Configure.java))
  * `filterRun(includeTags("foo"))` and `filterRun(excludeTags("bar"))` allow the rules to vary based on where they are called in the declaration block.

Tags allow you run different categories of specs in different test runs, either through the
configuration of your build - usually with system property - or with hard coding in the test class or specs themselves.

Example: temporarily making only WIP tests run in a test class

```java
  @RunWith(Spectrum.class)
  public TestClass {
     {
        filterRun(includeTags("wip"));

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

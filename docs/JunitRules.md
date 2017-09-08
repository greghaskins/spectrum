# Mixing JUnit's rules with Spectrum

## Overview

There are two drivers for using Spectrum's JUnit rule support:

- Saving you a lot of boilerplate code for trivial cases - e.g. just adding a `Mockito` rule or `TemporaryFolder` rule to save doing things the long way.
- Adapting large test ecosystems for use with Spectrum - e.g. `Spring`

In the trivial case, you will probably aim to add standard `JUnit` `@Rule` annotations to your test class, and everything should work great. In the second case, you should expect to create a separate class to represent the rules/configuration you want to attach to your Spectrum tests, and then use Spectrum's `junitMixin` function to attach those rules to an object, which Spectrum initialises for you before each spec that can use it.

If in doubt, use the `junitMixin` as, like JUnit itself, it creates a fresh object for each test.

> There are still limitations of this implementation, so please raise issues if you encounter any problems. Please ensure that any `JUnit` rule related issues come with easy to replicate code examples.


### JUnit style

With many of the JUnit rules, you can pretend that Spectrum works like JUnit and put `@Rule` and `@ClassRule` members in the test class.
When things stop working, move to Spectrum style.

```java
@RunWith(Spectrum.class)
public class SpectrumSpec {
  @Rule
  public TemporaryFolder tempFolderRule = new TemporaryFolder();

{
  describe("a set of test specs", () -> {
    it("has a freshly prepared tempFolderRule", () -> {
      // tempFolderRule gives us one folder here having been set up by the junit.rule
    });
    it("has a different fresh copy of the test object here", () -> {
      // tempFolderRule gives us another folder here too
    });
  });
}}
```

### Spectrum style

#### Step 1 - create a class with your JUnit rules in it.

> In Spectrum's own test cases, the mix-in class is a `public static class` inside the test class. This is one option. It does not matter whether the rules class is an inner class, or whether it's external, so long as it is public and has a default constructor. Making these mix-in classes as external reusable objects may be a useful way to modularise testing.

It is up to you whether you make the fields accessible, or put getters on them. For simplicity here is an example with accessible fields:

```java
public class TestRuleMixin {
  @Rule
  public TemporaryFolder tempFolderRule = new TemporaryFolder();
}
```

#### Step 2 - use that junit.rule within your tests with `junitMixin`

The `junitMixin` function returns a `Supplier`. That supplier's `get` function will allow you to access the current instance of the mix-in object during your tests/specs. The rules mentioned will have been executed already.

```java
@RunWith(Spectrum.class)
public class SpectrumSpec {{
  Supplier<TestRuleMixin> testObject = junitMixin(TestRuleMixin.class);
  describe("a set of test specs", () -> {
    it("has a fresh copy of the test object here", () -> {
      // testObject.get() gives us one instance here having run
    });
    it("has a different fresh copy of the test object here", () -> {
      // testObject.get() gives us another instance here too
    });
  });
}}
```
The rules are applied and the test object created just in time for each atomic test within the describe blocks etc. An atomic test is either an `it` level test or a `compositeTest` for example a `GherkinSyntax` `scenario`.

### Examples

> See: [JUnitRuleExample](../src/test/java/specs/JUnitRuleExample.java),
[MockitoSpecJUnitStyle](../src/test/java/specs/MockitoSpecJUnitStyle.java),
[MockitoSpecWithRuleClasses](../src/test/java/specs/MockitoSpecWithRuleClasses.java),
[SpringSpecJUnitStyle](../src/test/java/specs/SpringSpecJUnitStyle.java) and
[SpringSpecWithRuleClasses](../src/test/java/specs/SpringSpecWithRuleClasses.java)

### What is Supported

* `@ClassRule` is applied
* `@BeforeClass` is applied
* `@AfterClass` is applied
* `@Rule` objects:
  * `TestRule`s are applied at the level of each atomic test
  * `MethodRule`s are applied at the level of each atomic test
* `junitMixin` is implemented to be thread-safe
* `junitMixin` provides an overload for unboxing the supplier - `junitMixin(SomeClass.class, SomeInterface.class)` - if your mixin has an interface to it. See also [`let` - when get is getting you down.](VariablesAndValues.md)

### What is not supported

Native JUnit allows you to put annotations on specific test methods for the rules to pick up.

E.g.

```java
@Test
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
public void willDoSomethingDestructiveToSpring() throws Exception {
    myBean.deleteAll();
}
```

This is not yet supported in Spectrum. You can work around it by using different mix-in classes with different class-level annotations to control this sort of behaviour, and segmenting your test suite so that specs that need certain behaviour are within one `describe` block etc.

> Please raise feature requests if something you're trying to do is not yet supported.


## Comparing Regular JUnit and Spectrum Runners

Spectrum's runner works differently to the normal JUnit `ParentRunner` and `BlockJUnitRunner` derived test runners. In JUnit you normally have a new instance of the test class for every single test method to work with. As Spectrum uses the test class to write functional definitions of tests, there is only a single instance of the test object used throughout.

In JUnit, there is NO instance of the test class during the test discovery phase, which means that only static members of the test class have been touched. This allows the test execution phase to prepare the static members of the test class **before** any instance members might be constructed.

In Spectrum, the test class is instantiated in order to perform test discovery - this brings all instance members into existence at around the same time as static members: before tests start. Furthermore, there's only ever going to be one initialisation of both the static and instance members of a Spectrum test class.

If your JUnit rules have something like this in them:

```java
@ClassRule
public static SomeClassRule classRule = ...;

@Rule
public SomeMethodRule methodRule = new SomeMethodRule(classRule.getSomething());
```

then there is a chance that putting these line of code into the Test Class **will not work**. It varies. It's fine for Spring, but not for DropWizard. It all depends on what happens when the `@ClassRule` is executed. If the `@ClassRule` completes the initialization of the static object, then you need to make sure that the class rule executes completely before the instance-level object is created.

## How close can Spectrum get?

The `junitMixin` function causes a fresh initialisation of the mix-in object for every atomic child anywhere in the hierarchy following where `junitMixin` is called. The junit features are implemented in the same order as JUnit and there is no unexpected re-use of objects. Static initialization happens before any instance member initialization.

This is _almost_ the same as native JUnit.

## How does it work?

To enable users to mix in features from across the JUnit ecosystem, there are two ways you can add JUnit behaviour to Spectrum tests.

* You _can_ use the Java class within which you have declared the Spectrum tests. This can contain local variables and `@Rule` annotated objects. They will be reused over the course of the test.
* You can wire in Rules classes using `junitMixin` - these provide multiple instances of the test object of that rules class and execute JUnit `@Rule` directives within it along the way.

The Spectrum native mixin approach is the safest and cleanest. Not all JUnit rules are compatible with the other approach, so use it with care.

# Mixing JUnit's rules with Spectrum

## Overview

There are two ideal ways to use Spectrum's JUnit rule support:

- Saving you a lot of boilerplate code for trivial cases - e.g. just adding a `Mockito` rule or `TemporaryFolder` rule to save doing things the long way.
- Adapting large test ecosystems for use with Spectrum - e.g. `Spring`

In the trivial case, you will probably aim to add standard `JUnit` `@Rule` annotations to your test class, and everything should work great. In the second case, you should expect to create a separate class to represent the rules/configuration you want to attach to your Spectrum tests, and then use Spectrum's `junitMixin` function to attach those rules to an object, which Spectrum initialises for you before each spec that can use it.

If in doubt, use the `junitMixin` - it will behave the best. There are still limitations of this implementation, so please raise issues if you encounter any problems. Please ensure that any `JUnit` rule related issues come with easy to replicate code examples.

#### What is Supported

* `@ClassRule` is applied
* `@BeforeAll` is applied
* `@AfterAll` is applied
* `@Rule` objects:
  * `TestRule`s are applied at the level of each atomic test
  * `MethodRule`s are applied at the level of each atomic test
* `junitMixin` is implemented to be thread-safe

#### What is not supported

Native JUnit allows you to put annotations on specific test methods for the rules to pick up.

E.g.

```java
@Test
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
public void willDoSomethingDestructiveToSpring() throws Exception {
    myBean.deleteAll();
}
```

This is not yet supported in Spectrum. You can work around it by using different mix-in classes with different class-level annotations to control this sort of behaviour, and segmenting your test suite so that specs that need certain behaviour are within one `describe` block etc. Please raise feature requests if something you're trying to do is not yet supported.


## Comparing Regular JUnit and Spectrum Runners

Spectrum's runner works differently to the normal JUnit `ParentRunner` and `BlockJUnitRunner` derived test runners. In JUnit you normally have a new instance of the test class for every single test method to work with. As Spectrum uses the test class to write functional definitions of tests, there is only a single instance of the test object used throughout.

In JUnit, there is NO instance of the test class during the test discovery phase, which means that only static members of the test class have been touched. This allows the test execution phase to prepare the static members of the test class **before** any instance members might be constructed.

In Spectrum, the test class is instantiated as a way of doing test discovery - this brings all instance members into existence at around the same time as static members, before tests start. Furthermore, there's only ever going to be one initialisation of both the static and instance members of a Spectrum test class.

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

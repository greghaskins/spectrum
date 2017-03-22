# Quickstart Walkthrough
<!---freshmark main
output = input.replace(/\b\d+\.\d+\.\d+\b/g, '{{stableVersion}}');
-->
To write your first Spectrum test, you will need:

- Java 8 (for your tests; systems under test can use older versions)
- The Gradle/Maven dependency for Spectrum (see below)

### Gradle

Add the Spectrum dependency to your `testCompile` configuration in `build.gradle`:

```groovy
dependencies {
  testCompile 'com.greghaskins:spectrum:1.1.0'
}
```

### Maven

Add Spectrum as a dependency with `test` scope in your `pom.xml`:

```xml
<dependency>
    <groupId>com.greghaskins</groupId>
    <artifactId>spectrum</artifactId>
    <version>1.1.0</version>
    <scope>test</scope>
</dependency>
```

## The Basics

A Spectrum test class uses the Spectrum test runner and has an anonymous constructor within which the suites and specs will be written:

```java
@RunWith(Spectrum.class)
public class MySpecs {{
    // note the extra braces - the Java anonymous constructor
}}
```

The above class is marked to run with the `Spectrum` class, so is executed by JUnit using Spectrum to find and execute all tests.

When Spectrum is asked to find the tests (specs in this case), it makes an instance of the class, and the body of the anonymous constructor will contain calls to Spectrum methods that describe the suites and specs.

## Adding a suite

To add a suite, we use the `describe` function which takes a `Block` which is a void lambda expression.

```java
@RunWith(Spectrum.class)
public class MySpecs {{
    describe("The quick start", () -> {
       // inside this block we can put other suites, or individual specs
    });
}}
```

As the `describe` function refers to a suite, its lambda is executed at test definition time to discover any nested suites or nested specs.

## Adding a spec

Specs are meant to describe the behaviour of the system, with bodies that verify that behaviour. They use the `it` keyword and it is conventional to make the description of the spec read as though _it_ were the first word.

`it` specs also take a block, which represents the execution code for testing that spec.

```java
@RunWith(Spectrum.class)
public class MySpecs {{
    describe("The quick start", () -> {
       it("can make assertions", () -> {
           // here is where you exercise your system-under-test
            assertTrue(true);
       });
    });
}}
```

## Set-up

If you want to start with a fresh object for every run of your test, then the easiest set-up technique is to use `let`. The `let` function returns a `Supplier` which will contain a freshly built object (according to your definition) within each `it` that uses it:

```java
@RunWith(Spectrum.class)
public class MySpecs {{
    describe("The quick start", () -> {
       // have a fresh foo for each run
       Supplier<Foo> foo = let(() -> FooFactory.makeNewFoo());

       it("can make assertions", () -> {
            // using get on the supplier, will give you
            // this spec's copy of the object
            assertTrue(foo.get().isFighter());
       });
    });
}}
```

## Where next?

For more information see the examples and features described the [full documentation](README.md).

<!---freshmark /main -->

# Spectrum Documentation

Specrtrum is a BDD-style test runner for Java 8. It leverages lambda functions to provide a test-writing experience that is familiar to users of BDD tools from other platforms like Ruby and JavaScript. The library is implemented as a custom runner for JUnit 4, allowing Spectrum tests to integrate (mostly) seamlessly with existing IDEs and tooling.

## Supported Features

Spectrum provides two ways of writing tests:

- [Specification-style test DSL](SpecificationDSL.md) with `describe` / `it` / `beforeEach` / etc.
- [Gherkin-style test DSL](Gherkin.md) with `feature` / `scenario` / `given` / `when` / `then` / etc.

Spectrum also supports:

- Unlimited nesting of suites within suites
- Rigorous error handling and reporting when something unexpected goes wrong
- Compatibility with most existing JUnit tools; no configuration required
- Plugging in familiar JUnit-friendly libraries like `MockitoJUnit` or `SpringJUnit` [via JUnit `@Rule`s handling](JunitRules.md).
- Tagging specs for [selective running](FocusingAndIgnoring.md)
- Mixing Spectrum tests and normal JUnit tests in the same project suite
- RSpec-style `aroundEach` and `aroundAll` hooks for advanced users and plugin authors

## Non-Features

Unlike some BDD-style frameworks, Spectrum is _only_ a test runner. Assertions, expectations, mocks, and matchers are the purview of other libraries such as [Hamcrest](http://hamcrest.org/JavaHamcrest/), [AssertJ](http://joel-costigliola.github.io/assertj/), [Mockito](http://mockito.org/), or [plain JUnit](https://github.com/junit-team/junit4/wiki/Assertions).

## Getting Started

See the [quickstart waklthrough](QuickstartWalkthrough.md) and the docs for writing [Specification-style](SpecificationDSL.md) or [Gherkin-style](Gherkin.md) tests.

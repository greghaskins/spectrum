# How to contribute to Spectrum

_Awesome_. Thanks the help – we can use it! This document has a few notes for getting started contributing to the project.

## Communication

Bugs, enhancements, and discussions are all tracked via [GitHub issues](https://github.com/greghaskins/spectrum/issues). Check there to start a conversation or join one in progress. We don't have a mailing list or anything like that yet, so please [search the existing issues](https://github.com/greghaskins/spectrum/issues?utf8=%E2%9C%93&q=) before [creating a new one](https://github.com/greghaskins/spectrum/issues/new).

The current project maintainer is [@greghaskins](https://github.com/greghaskins).

## Project Goals

Overall, this project seeks to mirror functionality found in BDD-style test runners on other platforms. Specifically:
- Mirror the core [Jasmine](http://jasmine.github.io/) API and terminology as much as possible to make Spectrum easy to grok for polyglots.
- Document features via easy-to-understand examples, written as specs. These example specs serve as a regression suite, but do not replace lower-level unit tests.
- Integrate as nicely as possible with existing JUnit tooling (reports, CI, IDEs, etc.) so developers can add Spectrum tests to an existing codebase and it "just works."
- In contrast to Jasmine, Spectrum is _only_ a test runner. Things like mocks, spies, and assertions need to be provided by other libraries.

## Development Workflow

This project essentially follows the GitHub Flow. See [this overview](https://guides.github.com/introduction/flow/) and check out the [detailed docs](https://help.github.com/categories/collaborating-on-projects-using-issues-and-pull-requests/) if any of the steps below don't make sense.

1. [Fork the repository](https://github.com/greghaskins/spectrum/fork)
2. Clone your repository locally:

   ```
   git clone git@github.com:your-username-here/spectrum.git
   cd spectrum/
   ```
3. Add the `upstream` remote to get the latest changes

   ```
   git remote add upstream https://github.com/greghaskins/spectrum.git
   git pull upstream/master
   ```
4. Create a feature branch

   ```
   git checkout -b my-descriptive-branch-name
   ```
5. Write some code (see [some guidelines below](#code-guidelines))
6. Run the build

   ```sh
   ./gradlew build   # on Linux/Mac
   gradlew.bat build # on Windows
   ```
7. Commit your changes ([with a good message](http://chris.beams.io/posts/git-commit/))
8. Publish your branch

   ```
   git push origin my-descriptive-branch-name
   ```
9. Create a [Pull Request](https://help.github.com/articles/using-pull-requests/)

## Code Guidelines

- Use the `gradlew` build script before committing. The command line is the source of truth on [Travis-CI](https://travis-ci.org/greghaskins/spectrum). Each commit should run green.
- You'll need `java` (version 8) and `git` on your system `PATH`
- Write tests for Spectrum using Spectrum. [Dogfooding](https://en.wikipedia.org/wiki/Eating_your_own_dog_food) helps find bugs and reveal missing features. Put your specs in `src/test/java/specs`.
- All functional and bugfix changes should be [test-driven](https://en.wikipedia.org/wiki/Test-driven_development).
- [Write good commit messages](http://chris.beams.io/posts/git-commit/)
- This project follows [semantic versioning](http://semver.org/). If your change will break backward-compatibility, please clearly indicate that in your pull request.
- Don't add any external dependencies (especially `compile` dependencies). The production code should depend only on `junit` to make integration as easy as possible.
- Use the code formatting and Checkstyle rules in the `config/` folder with your IDE to catch style issues as you go. These are enforced by the Gradle build.

package com.greghaskins.spectrum;

public class Configuration {

  private final Suite suite;

  Configuration(Suite suite) {
    this.suite = suite;
  }

  /**
   * Set the test filter to require certain tags for all following specs.
   *
   * @param tagsToRequire specs (or their parent suite) must have at least one of these
   * @return Configuration instance for chaining further calls
   */
  public Configuration requireTags(String... tagsToRequire) {
    this.suite.requireTags(tagsToRequire);

    return this;
  }

  /**
   * Set the test filter to exclude certain tags for all following specs.
   *
   * @param tagsToExclude specs and their parent suite must not have any of these
   * @return Configuration instance for chaining further calls
   */
  public Configuration excludeTags(String... tagsToExclude) {
    this.suite.excludeTags(tagsToExclude);

    return this;
  }

}

package com.greghaskins.spectrum;

public class Configuration {

  private final Suite suite;

  Configuration(Suite suite) {
    this.suite = suite;
  }

  /**
   * Set the test filter to require at least one of these tags for all following specs.
   *
   * @param tagsToInclude specs (or their parent suite) must have at least one of these
   * @return Configuration instance for chaining further calls
   */
  public Configuration includeTags(String... tagsToInclude) {
    this.suite.includeTags(tagsToInclude);

    return this;
  }

  /**
   * Set the test filter to exclude any following specs that have one of these tags.
   *
   * @param tagsToExclude specs and their parent suite must not have any of these
   * @return Configuration instance for chaining further calls
   */
  public Configuration excludeTags(String... tagsToExclude) {
    this.suite.excludeTags(tagsToExclude);

    return this;
  }

}

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
   */
  public void requireTags(String... tagsToRequire) {
    this.suite.requireTags(tagsToRequire);
  }

  /**
   * Set the test filter to exclude certain tags for all following specs.
   *
   * @param tagsToExclude specs and their parent suite must not have any of these
   */
  public void excludeTags(String... tagsToExclude) {
    this.suite.excludeTags(tagsToExclude);
  }

}

package com.greghaskins.spectrum;

/**
 * Allows the injection of suite configuration during test definition. A wrapper for the
 * suite which exposes configurables.
 */
public class Configuration {

  private final Suite suite;
  public static final String EXCLUDE_TAGS_PROPERTY = "spectrum.exclude.tags";
  public static final String INCLUDE_TAGS_PROPERTY = "spectrum.include.tags";

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

package com.greghaskins.spectrum.internal;

import com.greghaskins.spectrum.Configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Represents the state of tagging for Spectrum - what it presently means.
 */
public class TaggingFilterCriteria {
  private Set<String> included = new HashSet<>();
  private Set<String> excluded = new HashSet<>();
  private static final String TAGS_SEPARATOR = ",";

  public TaggingFilterCriteria() {
    include(fromSystemProperty(Configuration.INCLUDE_TAGS_PROPERTY));
    exclude(fromSystemProperty(Configuration.EXCLUDE_TAGS_PROPERTY));
  }

  public void include(String... tags) {
    include(Arrays.stream(tags));
  }

  private void include(Stream<String> tags) {
    this.included.clear();
    tags.forEach(this.included::add);
  }

  public void exclude(String... tags) {
    exclude(Arrays.stream(tags));
  }

  private void exclude(Stream<String> tags) {
    this.excluded.clear();
    tags.forEach(this.excluded::add);
  }

  @Override
  public TaggingFilterCriteria clone() {
    TaggingFilterCriteria copy = new TaggingFilterCriteria();
    copy.include(this.included.stream());
    copy.exclude(this.excluded.stream());

    return copy;
  }

  public boolean isAllowedToRun(Collection<String> tags) {
    return !isExcluded(tags) && compliesWithRequired(tags);
  }

  private boolean isExcluded(Collection<String> tags) {
    return tags.stream()
        .filter(this.excluded::contains)
        .findFirst()
        .isPresent();
  }

  private boolean compliesWithRequired(Collection<String> tags) {
    return this.included.isEmpty()
        || tags.stream()
            .filter(this.included::contains)
            .findFirst()
            .isPresent();
  }

  private String[] fromSystemProperty(final String property) {
    return Optional.ofNullable(System.getProperty(property))
        .map(string -> string.split(TaggingFilterCriteria.TAGS_SEPARATOR))
        .filter(TaggingFilterCriteria::notArrayWithEmptyValue)
        .orElse(new String[0]);
  }

  private static boolean notArrayWithEmptyValue(final String[] array) {
    return !(array.length == 1 && array[0].isEmpty());
  }

}

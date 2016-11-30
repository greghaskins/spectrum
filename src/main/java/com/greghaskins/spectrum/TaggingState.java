package com.greghaskins.spectrum;

import static com.greghaskins.spectrum.SpectrumOptions.EXCLUDE_TAGS_PROPERTY;
import static com.greghaskins.spectrum.SpectrumOptions.INCLUDE_TAGS_PROPERTY;
import static com.greghaskins.spectrum.SpectrumOptions.TAGS_SEPARATOR;

import org.junit.runners.model.TestClass;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Represents the state of tagging for Spectrum - what it presently means.
 */
class TaggingState {
  private Set<String> included = new HashSet<>();
  private Set<String> excluded = new HashSet<>();

  void include(String... tags) {
    include(Arrays.stream(tags));
  }

  private void include(Stream<String> tags) {
    this.included.clear();
    tags.forEach(this.included::add);
  }

  void exclude(String... tags) {
    exclude(Arrays.stream(tags));
  }

  private void exclude(Stream<String> tags) {
    this.excluded.clear();
    tags.forEach(this.excluded::add);
  }

  @Override
  public TaggingState clone() {
    TaggingState copy = new TaggingState();
    copy.include(this.included.stream());
    copy.exclude(this.excluded.stream());

    return copy;
  }

  boolean isAllowedToRun(Collection<String> tags) {
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

  void read(final Class<?> klazz) {
    SpectrumOptions options = new TestClass(klazz).getAnnotation(SpectrumOptions.class);

    final String[] systemIncludes = fromSystemProperty(INCLUDE_TAGS_PROPERTY);
    final String[] systemExcludes = fromSystemProperty(EXCLUDE_TAGS_PROPERTY);

    final String[] annotationIncludes = readAnnotationIncludes(options);
    final String[] annotationExcludes = readAnnotationExcludes(options);

    // the annotation can provide nothing and so we drop back to
    // the system properties - this is done separately
    // for includes and excludes
    include(firstNonBlank(annotationIncludes, systemIncludes));
    exclude(firstNonBlank(annotationExcludes, systemExcludes));
  }

  private String[] firstNonBlank(final String[]... arrays) {
    return Arrays.stream(arrays)
        .filter(array -> array != null)
        .filter(array -> array.length > 0)
        .findFirst()
        .orElse(new String[] {});
  }

  private String[] fromSystemProperty(final String property) {
    return Optional.ofNullable(System.getProperty(property))
        .map(string -> string.split(TAGS_SEPARATOR))
        .filter(TaggingState::notArrayWithEmptyValue)
        .orElse(null);
  }

  private static boolean notArrayWithEmptyValue(final String[] array) {
    return !(array.length == 1 && array[0].isEmpty());
  }

  private String[] readAnnotationIncludes(SpectrumOptions options) {
    return Optional.ofNullable(options)
        .map(SpectrumOptions::includeTags)
        .orElse(null);
  }

  private String[] readAnnotationExcludes(SpectrumOptions options) {
    return Optional.ofNullable(options)
        .map(SpectrumOptions::excludeTags)
        .orElse(null);
  }
}

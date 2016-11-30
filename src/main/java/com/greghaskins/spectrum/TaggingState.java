package com.greghaskins.spectrum;

import static com.greghaskins.spectrum.SpectrumOptions.EXCLUDE_TAGS_PROPERTY;
import static com.greghaskins.spectrum.SpectrumOptions.REQUIRE_TAGS_PROPERTY;
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
  private Set<String> required = new HashSet<>();
  private Set<String> excluded = new HashSet<>();

  void require(String... tags) {
    require(Arrays.stream(tags));
  }

  private void require(Stream<String> tags) {
    required.clear();
    tags.forEach(required::add);
  }

  void exclude(String... tags) {
    exclude(Arrays.stream(tags));
  }

  private void exclude(Stream<String> tags) {
    excluded.clear();
    tags.forEach(excluded::add);
  }

  public TaggingState clone() {
    TaggingState copy = new TaggingState();
    copy.require(required.stream());
    copy.exclude(excluded.stream());

    return copy;
  }

  boolean isSuiteAllowedToRun(Collection<String> tags) {
    return !isExcluded(tags) && compliesWithRequired(tags);
  }

  boolean isSpecAllowedToRun(Collection<String> tags) {
    return isSuiteAllowedToRun(tags);
  }

  private boolean isExcluded(Collection<String> tags) {
    return tags.stream()
        .filter(excluded::contains)
        .findFirst()
        .isPresent();
  }

  private boolean compliesWithRequired(Collection<String> tags) {
    return required.isEmpty()
        || tags.stream()
            .filter(required::contains)
            .findFirst()
            .isPresent();
  }

  void read(final Class<?> klazz) {
    SpectrumOptions options = new TestClass(klazz).getAnnotation(SpectrumOptions.class);

    final String[] systemIncludes = readSystemPropertyIncludes(options);
    final String[] systemExcludes = readSystemPropertyExcludes(options);

    final String[] annotationIncludes = readAnnotationIncludes(options);
    final String[] annotationExcludes = readAnnotationExcludes(options);

    // the annotation can provide nothing and so we drop back to
    // the system properties - this is done separately
    // for includes and excludes
    require(firstNonBlank(annotationIncludes, systemIncludes));
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

  private String[] readSystemPropertyIncludes(SpectrumOptions options) {
    return fromSystemProperty(Optional.ofNullable(options)
        .map(SpectrumOptions::requireTagsSystemProperty)
        .orElse(REQUIRE_TAGS_PROPERTY));
  }

  private String[] readSystemPropertyExcludes(SpectrumOptions options) {
    return fromSystemProperty(Optional.ofNullable(options)
        .map(SpectrumOptions::excludeTagsSystemProperty)
        .orElse(EXCLUDE_TAGS_PROPERTY));
  }

  private String[] readAnnotationIncludes(SpectrumOptions options) {
    return Optional.ofNullable(options)
        .map(SpectrumOptions::requireTags)
        .orElse(null);
  }

  private String[] readAnnotationExcludes(SpectrumOptions options) {
    return Optional.ofNullable(options)
        .map(SpectrumOptions::excludeTags)
        .orElse(null);
  }
}

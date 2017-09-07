package com.greghaskins.spectrum.internal.configuration;

import com.greghaskins.spectrum.internal.Child;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Configurations that apply to a {@link ConfiguredBlock}.
 */
public class BlockConfiguration {
  /**
   * Combine provided configuration objects together.
   *
   * @param conditions to combine
   * @return a combination of all configurations as a new object
   */
  public static BlockConfiguration merge(BlockConfiguration... conditions) {
    BlockConfiguration merged = new BlockConfiguration();
    Arrays.stream(conditions).forEach(merged::mergeWith);

    return merged;
  }

  /**
   * Configurations stored by type of configurable.
   */
  private Map<Class<?>, BlockConfigurable<?>> configurations = new HashMap<>();

  /**
   * Children should inherit tags and ignore status, but not focus.
   *
   * @return a new BlockConfiguration that would apply for a Child
   */
  public BlockConfiguration forChild() {
    BlockConfiguration conditions = new BlockConfiguration();
    configurations.values()
        .stream()
        .filter(BlockConfigurable::inheritedByChild)
        .forEach(conditions::add);

    return conditions;
  }

  /**
   * Add a configurable to the configuration.
   * @param configurable to add
   */
  public void add(BlockConfigurable<?> configurable) {
    // merge this configurable into what we already have
    Class<?> configurableClass = configurable.getClass();
    configurations.put(configurableClass, configurable.merge(configurations.get(configurableClass)));
  }

  private void mergeWith(BlockConfiguration other) {
    other.configurations
        .values()
        .forEach(this::add);
  }

  private BlockConfiguration() {
    // there must be default tagging of blank for tagging to work
    add(new BlockTagging());
  }

  /**
   * Construct an empty block configuration.
   * @return a block configuration with no special configuration in it
   */
  public static BlockConfiguration defaultConfiguration() {
    return new BlockConfiguration();
  }

  /**
   * Construct BlockConfiguration from a single configurable.
   * @param configurable to start with
   * @return a block configuration with the configurable in it
   */
  public static BlockConfiguration of(BlockConfigurable<?> configurable) {
    BlockConfiguration configuration = new BlockConfiguration();
    configuration.add(configurable);

    return configuration;
  }

  /**
   * Visitor pattern - when necessary, the child gets the configurations to apply to it.
   *
   * @param child to be pre-processed according to the configurations.
   * @param state the tagging state within which the child is operating
   */
  public void applyTo(final Child child, final TaggingFilterCriteria state) {
    configurations.values().forEach(configurable -> configurable.applyTo(child, state));
  }

  public Stream<BlockConfigurable<?>> getConfigurables() {
    return configurations.values().stream();
  }
}

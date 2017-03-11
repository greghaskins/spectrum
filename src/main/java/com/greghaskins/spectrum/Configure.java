package com.greghaskins.spectrum;

import com.greghaskins.spectrum.internal.DeclarationState;
import com.greghaskins.spectrum.internal.configuration.BlockFocused;
import com.greghaskins.spectrum.internal.configuration.BlockIgnore;
import com.greghaskins.spectrum.internal.configuration.BlockTagging;
import com.greghaskins.spectrum.internal.configuration.ConfiguredBlock;
import com.greghaskins.spectrum.internal.configuration.ExcludeTags;
import com.greghaskins.spectrum.internal.configuration.IncludeTags;

public interface Configure {

  String EXCLUDE_TAGS_PROPERTY = "spectrum.exclude.tags";
  String INCLUDE_TAGS_PROPERTY = "spectrum.include.tags";

  /**
   * Surround a {@link com.greghaskins.spectrum.Block} with the {@code with} statement to add
   * preconditions and metadata to it. E.g. <code>with(tags("foo"), () -&gt; {})</code>.<br>
   * Note: preconditions and metadata can be chained using the
   * {@link BlockConfigurationChain#and(BlockConfigurationChain)} method. E.g.
   * <code>with(tags("foo").and(ignore()), () -&gt; {})</code>
   *
   * @param configuration the chainable block configuration
   * @param block the enclosed block
   * @return a wrapped block with the given configuration
   * @see #ignore(String)
   * @see #ignore()
   * @see #focus()
   * @see #tags(String...)
   */
  static com.greghaskins.spectrum.Block with(final BlockConfigurationChain configuration,
      final com.greghaskins.spectrum.Block block) {

    return ConfiguredBlock.with(configuration.getBlockConfiguration(), block);
  }

  /**
   * Mark a block as ignored by surrounding it with the ignore method.
   *
   * @param why explanation of why this block is being ignored
   * @param block the block to ignore
   * @return a wrapped block which will be ignored
   */
  static com.greghaskins.spectrum.Block ignore(final String why,
      final com.greghaskins.spectrum.Block block) {
    return with(ignore(why), block);
  }

  /**
   * Mark a block as ignored by surrounding it with the ignore method.
   *
   * @param block the block to ignore
   * @return a wrapped block which will be ignored
   */
  static com.greghaskins.spectrum.Block ignore(final com.greghaskins.spectrum.Block block) {
    return with(ignore(), block);
  }

  /**
   * Ignore the suite or spec.
   *
   * @return a chainable configuration that will ignore the block within a {@link #with}
   */
  static BlockConfigurationChain ignore() {
    return new BlockConfigurationChain().with(new BlockIgnore());
  }

  /**
   * Ignore the suite or spec.
   *
   * @param reason why this block is ignored
   * @return a chainable configuration that will ignore the block within a {@link #with}
   */
  static BlockConfigurationChain ignore(final String reason) {
    return new BlockConfigurationChain().with(new BlockIgnore(reason));
  }

  /**
   * Tags the suite or spec that is being declared with the given strings. Depending on the current
   * filter criteria, this may lead to the item being ignored during test execution.
   *
   * @param tags tags that relate to the suite or spec
   * @return a chainable configuration that has these tags set for the block in {@link #with}
   */
  static BlockConfigurationChain tags(final String... tags) {
    return new BlockConfigurationChain().with(new BlockTagging(tags));
  }

  /**
   * Marks the suite or spec to be focused.
   *
   * @return a chainable configuration that will focus the suite or spec in the {@link #with}
   */
  static BlockConfigurationChain focus() {
    return new BlockConfigurationChain().with(new BlockFocused());
  }

  /**
   * Filter which tests in the current suite will run.
   *
   * <br><br>
   * {@code filterRun(includeTags("foo").and(excludeTags("bar")));}
   *
   * @param configuration chainable filter configuration
   * @see #includeTags(String...)
   * @see #excludeTags(String...)
   */
  static void filterRun(FilterConfigurationChain configuration) {
    configuration.applyTo(DeclarationState.instance().getCurrentSuiteBeingDeclared());
  }

  /**
   * Set the test filter to require at least one of these tags for all following specs.
   *
   * @param tagsToInclude specs (or their parent suite) must have at least one of these
   * @return FilterConfigurationChain instance for chaining further calls
   */
  static FilterConfigurationChain includeTags(String... tagsToInclude) {
    return new FilterConfigurationChain(new IncludeTags(tagsToInclude));
  }

  /**
   * Set the test filter to exclude any following specs that have one of these tags.
   *
   * @param tagsToExclude specs and their parent suite must not have any of these
   * @return FilterConfigurationChain instance for chaining further calls
   */
  static FilterConfigurationChain excludeTags(String... tagsToExclude) {
    return new FilterConfigurationChain(new ExcludeTags(tagsToExclude));
  }

}

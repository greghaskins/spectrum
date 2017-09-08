package com.greghaskins.spectrum;

import static com.greghaskins.spectrum.Unboxer.unbox;

import com.greghaskins.spectrum.internal.DeclarationState;
import com.greghaskins.spectrum.internal.configuration.BlockFocused;
import com.greghaskins.spectrum.internal.configuration.BlockIgnore;
import com.greghaskins.spectrum.internal.configuration.BlockTagging;
import com.greghaskins.spectrum.internal.configuration.BlockTimeout;
import com.greghaskins.spectrum.internal.configuration.ConfiguredBlock;
import com.greghaskins.spectrum.internal.configuration.ExcludeTags;
import com.greghaskins.spectrum.internal.configuration.IncludeTags;
import com.greghaskins.spectrum.internal.junit.Rules;

import java.time.Duration;
import java.util.function.Supplier;

public interface Configure {

  String EXCLUDE_TAGS_PROPERTY = "spectrum.exclude.tags";
  String INCLUDE_TAGS_PROPERTY = "spectrum.include.tags";

  /**
   * Surround a {@link Block} with the {@code with} statement to add
   * configuration and metadata to it. E.g. <code>with(tags("foo"), () -&gt; {})</code>.<br>
   * Note: configuration metadata can be chained using the
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
   * @see #timeout(Duration)
   */
  static Block with(final BlockConfigurationChain configuration, final Block block) {
    return ConfiguredBlock.with(configuration.getBlockConfiguration(), block);
  }

  /**
   * Mark a block as ignored by surrounding it with the ignore method.
   *
   * @param why explanation of why this block is being ignored
   * @param block the block to ignore
   * @return a wrapped block which will be ignored
   */
  static Block ignore(final String why, final Block block) {
    return with(ignore(why), block);
  }

  /**
   * Mark a block as ignored by surrounding it with the ignore method.
   *
   * @param block the block to ignore
   * @return a wrapped block which will be ignored
   */
  static Block ignore(final Block block) {
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
   * Apply timeout to all leaf nodes from this level down. Can be superseded by a lower level having its
   * own timeout.
   * @param timeout the amount of the timeout
   * @return a chainable configuration that will apply a timeout to all leaf nodes below
   */
  static BlockConfigurationChain timeout(Duration timeout) {
    return new BlockConfigurationChain().with(new BlockTimeout(timeout));
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

  /**
   * Uses the given class as a mix-in for JUnit rules to be applied. These rules will cascade down
   * and be applied at the level of specs or atomic specs.
   *
   * @param classWithRules Class to create and apply rules to for each spec.
   * @param <T>            type of the object
   * @return a supplier of the rules object
   */
  static <T> Supplier<T> junitMixin(final Class<T> classWithRules) {
    return Rules.applyRules(classWithRules, DeclarationState.instance()::addHook);
  }

  /**
   * Uses the given class as a mix-in for JUnit rules to be applied. These rules will cascade down
   * and be applied at the level of specs or atomic specs. Provide a proxy to the eventual mix-in
   * via the interface given.
   *
   * @param classWithRules Class to create and apply rules to for each spec.
   * @param interfaceToReturn the type of interface the caller requires the proxy object to be
   * @param <T>            type of the mixin object
   * @param <R>            the required type at the consumer - allowing for generic interfaces
   *                       (e.g. <code>List&lt;String&gt;</code>)
   * @param <S>            type of the interface common to the rules object and the proxy
   * @return a proxy to the rules object
   */
  static <T extends S, R extends S, S> R junitMixin(final Class<T> classWithRules,
      final Class<S> interfaceToReturn) {
    return unbox(junitMixin(classWithRules), interfaceToReturn);
  }
}

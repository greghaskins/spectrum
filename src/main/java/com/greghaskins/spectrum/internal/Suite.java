package com.greghaskins.spectrum.internal;

import static com.greghaskins.spectrum.internal.configuration.BlockConfiguration.merge;
import static com.greghaskins.spectrum.internal.configuration.ConfiguredBlock.configurationFromBlock;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.internal.configuration.BlockConfiguration;
import com.greghaskins.spectrum.internal.configuration.ConfiguredBlock;
import com.greghaskins.spectrum.internal.configuration.TaggingFilterCriteria;
import com.greghaskins.spectrum.internal.hooks.Hook;
import com.greghaskins.spectrum.internal.hooks.HookContext;
import com.greghaskins.spectrum.internal.hooks.Hooks;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Suite implements Parent, Child {
  private Hooks hooks = new Hooks();

  protected final List<Child> children = new ArrayList<>();
  private final Set<Child> focusedChildren = new HashSet<>();

  private final ChildRunner childRunner;

  private final Description description;
  private final Parent parent;
  private boolean ignored;

  private final TaggingFilterCriteria tagging;
  private BlockConfiguration configuration = BlockConfiguration.defaultConfiguration();
  private NameSanitiser nameSanitiser = new NameSanitiser();

  /**
   * The strategy for running the children within the suite.
   */
  @FunctionalInterface
  interface ChildRunner {
    void runChildren(final Suite suite, final RunReporting<Description, Failure> reporting);
  }

  public static Suite rootSuite(final Description description) {
    return new Suite(description, Parent.NONE, Suite::defaultChildRunner,
        new TaggingFilterCriteria());
  }

  /**
   * Constructs a suite.
   *
   * @param description the JUnit description
   * @param parent parent item
   * @param childRunner which child running strategy to use - this will normally be
   *        {@link #defaultChildRunner(Suite, RunReporting)} which runs them all but can be
   *        substituted.
   * @param taggingFilterCriteria the state of tagging inherited from the parent
   */
  protected Suite(final Description description, final Parent parent, final ChildRunner childRunner,
      final TaggingFilterCriteria taggingFilterCriteria) {
    this.description = description;
    this.parent = parent;
    this.ignored = parent.isIgnored();
    this.childRunner = childRunner;
    this.tagging = taggingFilterCriteria;
  }

  public Suite addSuite(final String name) {
    return addSuite(name, Suite::defaultChildRunner);
  }

  private Suite addSuite(final String name, final ChildRunner childRunner) {
    final Suite suite = new Suite(Description.createSuiteDescription(sanitise(name)), this, childRunner,
        this.tagging.clone());

    suite.inheritConfigurationFromParent(configuration.forChild());

    return addedToThis(suite);
  }

  private Suite addedToThis(Suite suite) {
    addChild(suite);

    return suite;
  }

  public Suite addCompositeSuite(final String name) {
    final Suite suite =
        new CompositeTest(Description.createSuiteDescription(sanitise(name)), this,
            this.tagging.clone());

    return addedToThis(suite);
  }

  public Child addSpec(final String name, final Block block) {
    final Child spec = createSpec(name, block);
    addChild(spec);

    return spec;
  }

  private Child createSpec(final String name, final Block block) {
    final Description specDescription =
        Description.createTestDescription(this.description.getClassName(), sanitise(name));

    return configuredChild(new Spec(specDescription, block, this), block);
  }

  private void inheritConfigurationFromParent(final BlockConfiguration fromParent) {
    configuration = merge(fromParent, configuration);
  }

  private Child configuredChild(final Child child, final Block block) {
    merge(this.configuration.forChild(), configurationFromBlock(block))
        .applyTo(child, this.tagging);

    return child;
  }

  /**
   * Attach any configuration attached to the given block to the configuration of my suite.
   * Block configuration is provided when a {@link ConfiguredBlock} is created using the
   * {@link ConfiguredBlock#with(BlockConfiguration, Block)} function to wrap a normal
   * {@link Block}
   * @param block the block with a configuration
   */
  public void applyConfigurationFromBlock(Block block) {
    applyConfiguration(configurationFromBlock(block));
  }

  /**
   * Attach an explicitly created configuration to the current suite.
   * @param configuration to attach to this suite and its descendents
   */
  public void applyConfiguration(BlockConfiguration configuration) {
    this.configuration = merge(this.configuration, configuration);
    this.configuration.applyTo(this, this.tagging);
  }

  private void addChild(final Child child) {
    this.children.add(child);
  }

  /**
   * Adds a hook to be the first one executed before the block. This is the default. Hooks should be
   * executed in the order they are declared in the test.
   *
   * @param hook to add
   */
  public void addHook(final HookContext hook) {
    this.hooks.add(hook);
  }

  private Hooks getHooksFor(final Child child) {
    Hooks allHooks = this.parent.getInheritableHooks().plus(this.hooks);

    return child.isAtomic() ? allHooks.forAtomic() : allHooks.forNonAtomic();
  }

  @Override
  public Hooks getInheritableHooks() {
    // only the atomic hooks can be used by the children of this suite,
    // all other hooks would be executed at suite level only - either for
    // each child of the suite, or once

    return this.parent.getInheritableHooks().plus(this.hooks.forAtomic());
  }

  /**
   * Set the suite to require certain tags of all tests below.
   *
   * @param tags required tags - suites must have at least one of these if any are specified
   */
  public void includeTags(final String... tags) {
    this.tagging.include(tags);
  }

  /**
   * Set the suite to exclude certain tags of all tests below.
   *
   * @param tags excluded tags - suites and specs must not have any of these if any are specified
   */
  public void excludeTags(final String... tags) {
    this.tagging.exclude(tags);
  }

  @Override
  public void focus(final Child child) {
    this.focusedChildren.add(child);
    focus();
  }

  @Override
  public void focus() {
    if (this.ignored) {
      return;
    }

    this.parent.focus(this);
  }

  @Override
  public void ignore() {
    this.ignored = true;
  }

  @Override
  public boolean isIgnored() {
    return this.ignored;
  }

  @Override
  public void run(final RunReporting<Description, Failure> reporting) {
    if (testCount() == 0) {
      reporting.fireTestIgnored(this.description);
      runChildren(reporting);
    } else {
      runSuite(reporting);
    }
  }

  private void runSuite(final RunReporting<Description, Failure> reporting) {
    if (isEffectivelyIgnored()) {
      runChildren(reporting);
    } else {
      this.hooks.once().sorted()
          .runAround(this.description, reporting, () -> runChildren(reporting));
    }
  }

  private void runChildren(final RunReporting<Description, Failure> reporting) {
    this.childRunner.runChildren(this, reporting);
  }

  protected void runChild(final Child child, final RunReporting<Description, Failure> reporting) {
    if (child.isEffectivelyIgnored()) {
      // running the child will make it act ignored
      child.run(reporting);
    } else if (childIsNotInFocus(child)) {
      reporting.fireTestIgnored(child.getDescription());
    } else {
      addLeafHook(this.hooks.forThisLevel().sorted(), child).runAround(child.getDescription(), reporting,
          () -> runChildWithHooks(child, reporting));
    }
  }

  private boolean childIsNotInFocus(Child child) {
    return !this.focusedChildren.isEmpty() && !this.focusedChildren.contains(child);
  }

  private void runChildWithHooks(final Child child, final RunReporting<Description, Failure> reporting) {
    getHooksFor(child).sorted().runAround(child.getDescription(), reporting,
        () -> child.run(reporting));

  }

  private Hooks addLeafHook(final Hooks hooks, final Child child) {
    if (child.isLeaf()) {
      hooks.add(testNotifier());
    }

    return hooks;
  }

  private HookContext testNotifier() {
    return new HookContext(testNotificationHook(), 0, HookContext.AppliesTo.ONCE,
        HookContext.Precedence.ROOT);
  }

  private Hook testNotificationHook() {
    return (description, notifier, block) -> {
      notifier.fireTestStarted(description);
      try {
        block.run();
      } finally {
        notifier.fireTestFinished(description);
      }
    };
  }

  @Override
  public Description getDescription() {
    final Description copy = this.description.childlessCopy();
    this.children.forEach((child) -> copy.addChild(child.getDescription()));

    return copy;
  }

  @Override
  public int testCount() {
    return this.children.stream().mapToInt(Child::testCount).sum();
  }

  public void removeAllChildren() {
    this.children.clear();
  }

  private static void defaultChildRunner(final Suite suite,
      final RunReporting<Description, Failure> reporting) {
    suite.children.forEach((child) -> suite.runChild(child, reporting));
  }

  private String sanitise(final String name) {
    return this.nameSanitiser.sanitise(name);
  }

  @Override
  public boolean isEffectivelyIgnored() {
    return this.ignored || !hasANonIgnoredChild();
  }

  private boolean hasANonIgnoredChild() {
    return this.children.stream()
        .anyMatch(child -> !child.isEffectivelyIgnored());
  }
}

package com.greghaskins.spectrum.internal;

import com.greghaskins.spectrum.Block;
import com.greghaskins.spectrum.internal.blocks.ConfiguredBlock;
import com.greghaskins.spectrum.internal.blocks.NotifyingBlock;
import com.greghaskins.spectrum.internal.hooks.HookContext;
import com.greghaskins.spectrum.internal.hooks.Hooks;
import com.greghaskins.spectrum.model.BlockConfiguration;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

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
  private BlockConfiguration preconditions = BlockConfiguration.Factory.defaultPreConditions();
  private NameSanitiser nameSanitiser = new NameSanitiser();

  /**
   * The strategy for running the children within the suite.
   */
  @FunctionalInterface
  interface ChildRunner {
    void runChildren(final Suite suite, final RunNotifier notifier);
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
   *        {@link #defaultChildRunner(Suite, RunNotifier)} which runs them all but can be
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
    final Suite suite =
        new Suite(Description.createSuiteDescription(sanitise(name)), this, childRunner,
            this.tagging.clone());

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

    final NotifyingBlock specBlockInContext = NotifyingBlock.wrap(block);

    ConfiguredBlock configuredBlock =
        ConfiguredBlock.with(this.preconditions.forChild(), block);

    return new Spec(specDescription, specBlockInContext, this).applyPreConditions(configuredBlock,
        this.tagging);
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

  public void applyPreConditions(Block block) {
    this.preconditions = ConfiguredBlock.findApplicablePreconditions(block);
    applyPreConditions(block, this.tagging);
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
  public void run(final RunNotifier notifier) {
    if (testCount() == 0) {
      notifier.fireTestIgnored(this.description);
      runChildren(notifier);
    } else {
      runSuite(notifier);
    }
  }

  private void runSuite(final RunNotifier notifier) {
    if (isEffectivelyIgnored()) {
      runChildren(notifier);
    } else {
      this.hooks.once().sorted()
          .runAround(this.description, notifier, () -> runChildren(notifier));
    }
  }

  private void runChildren(final RunNotifier notifier) {
    this.childRunner.runChildren(this, notifier);
  }

  protected void runChild(final Child child, final RunNotifier notifier) {
    if (child.isEffectivelyIgnored()) {
      // running the child will make it act ignored
      child.run(notifier);
    } else if (childIsNotInFocus(child)) {
      notifier.fireTestIgnored(child.getDescription());
    } else {
      this.hooks.forThisLevel().sorted().runAround(child.getDescription(), notifier,
          () -> runChildWithHooks(child, notifier));
    }
  }

  private boolean childIsNotInFocus(Child child) {
    return !this.focusedChildren.isEmpty() && !this.focusedChildren.contains(child);
  }

  private void runChildWithHooks(final Child child, final RunNotifier notifier) {
    getHooksFor(child).sorted().runAround(child.getDescription(), notifier,
        () -> child.run(notifier));
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

  private static void defaultChildRunner(final Suite suite, final RunNotifier runNotifier) {
    suite.children.forEach((child) -> suite.runChild(child, runNotifier));
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
        .filter(child -> !child.isEffectivelyIgnored())
        .findFirst()
        .isPresent();
  }
}

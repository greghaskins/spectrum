package com.greghaskins.spectrum;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class Suite implements Parent, Child {

  private final SetupBlock beforeAll = new SetupBlock();
  private final TeardownBlock afterAll = new TeardownBlock();

  private final SetupBlock beforeEach = new SetupBlock();
  private final TeardownBlock afterEach = new TeardownBlock();

  private final List<Child> children = new ArrayList<>();
  private final Set<Child> focusedChildren = new HashSet<>();

  private final ChildRunner childRunner;

  private final Description description;
  private final Parent parent;
  private boolean ignored;

  private final TaggingState tagging;
  private PreConditions preconditions = PreConditions.Factory.defaultPreConditions();

  /**
   * The strategy for running the children within the suite.
   */
  @FunctionalInterface
  interface ChildRunner {
    void runChildren(final Suite suite, final RunNotifier notifier);
  }

  static Suite rootSuite(final Description description) {
    return new Suite(description, Parent.NONE, Suite::defaultChildRunner, new TaggingState());
  }

  /**
   * Constructs a suite.
   * @param description the JUnit description
   * @param parent parent item
   * @param childRunner which child running strategy to use - this will normally be
   *             {@link #defaultChildRunner(Suite, RunNotifier)} which runs them all
   *             but can be substituted for a strategy that ignores all specs
   *             after a test failure  {@link #abortOnFailureChildRunner(Suite, RunNotifier)}
   * @param taggingState the state of tagging inherited from the parent
   */
  private Suite(final Description description, final Parent parent, final ChildRunner childRunner,
      final TaggingState taggingState) {
    this.description = description;
    this.parent = parent;
    this.ignored = parent.isIgnored();
    this.childRunner = childRunner;
    this.tagging = taggingState;
  }

  Suite addSuite(final String name) {
    return addSuite(name, Suite::defaultChildRunner);
  }

  Suite addSuite(final String name, final ChildRunner childRunner) {
    final Suite suite =
        new Suite(Description.createSuiteDescription(name), this, childRunner, tagging.clone());
    suite.beforeAll.addBlock(this.beforeAll);
    suite.beforeEach.addBlock(this.beforeEach);
    suite.afterEach.addBlock(this.afterEach);
    addChild(suite);

    return suite;
  }

  Suite addAbortingSuite(final String name) {
    return addSuite(name, Suite::abortOnFailureChildRunner);
  }

  Child addSpec(final String name, final Block block) {
    final Child spec = createSpec(name, block);
    addChild(spec);

    return spec;
  }

  private Child createSpec(final String name, final Block block) {
    final Description specDescription =
        Description.createTestDescription(this.description.getClassName(), name);

    final NotifyingBlock specBlockInContext = (description, notifier) -> {
      try {
        this.beforeAll.run();
      } catch (final Throwable exception) {
        notifier.fireTestFailure(new Failure(description, exception));
        return;
      }

      NotifyingBlock.wrap(() -> {
        this.beforeEach.run();
        block.run();
      }).run(description, notifier);

      this.afterEach.run(description, notifier);
    };

    PreConditionBlock preConditionBlock =
        PreConditionBlock.with(this.preconditions.forChild(), block);

    return new Spec(specDescription, specBlockInContext, this)
        .applyPreConditions(preConditionBlock, this.tagging);
  }

  private void addChild(final Child child) {
    this.children.add(child);
  }

  void beforeAll(final Block block) {
    this.beforeAll.addBlock(new IdempotentBlock(block));
  }

  void afterAll(final Block block) {
    this.afterAll.addBlock(block);
  }

  void beforeEach(final Block block) {
    this.beforeEach.addBlock(block);
  }

  void afterEach(final Block block) {
    this.afterEach.addBlock(block);
  }

  /**
   * Set the suite to require certain tags of all tests below.
   * @param tags required tags - suites must have at least one of these if any are specified
   */
  void requireTags(final String... tags) {
    tagging.require(tags);
  }

  /**
   * Set the suite to exclude certain tags of all tests below.
   * @param tags excluded tags - suites and specs must not have any of these if any are specified
   */
  void excludeTags(final String... tags) {
    tagging.exclude(tags);
  }

  /**
   * Read the tagging configuration.
   * @param testClass the test class within which there's tagging configuration - or defaults
   */
  void readTagging(Class<?> testClass) {
    tagging.read(testClass);
  }

  void applyPreConditions(Block block) {
    this.preconditions = Child.findApplicablePreconditions(block);
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
      runChildren(notifier);
      runAfterAll(notifier);
    }
  }

  private void runChildren(final RunNotifier notifier) {
    childRunner.runChildren(this, notifier);
  }

  private void runChild(final Child child, final RunNotifier notifier) {
    if (this.focusedChildren.isEmpty() || this.focusedChildren.contains(child)) {
      child.run(notifier);
    } else {
      notifier.fireTestIgnored(child.getDescription());
    }
  }

  private void runAfterAll(final RunNotifier notifier) {
    this.afterAll.run(this.description, notifier);
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

  void removeAllChildren() {
    this.children.clear();
  }

  private static void defaultChildRunner(final Suite suite, final RunNotifier runNotifier) {
    suite.children.forEach((child) -> suite.runChild(child, runNotifier));
  }

  private static void abortOnFailureChildRunner(final Suite suite, final RunNotifier runNotifier) {
    FailureDetectingRunListener listener = new FailureDetectingRunListener();
    runNotifier.addListener(listener);
    try {
      for (Child child : suite.children) {
        if (listener.hasFailedYet()) {
          child.ignore();
        }
        suite.runChild(child, runNotifier);
      }
    } finally {
      runNotifier.removeListener(listener);
    }
  }
}

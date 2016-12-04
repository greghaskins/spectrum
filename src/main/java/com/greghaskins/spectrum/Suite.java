package com.greghaskins.spectrum;

import com.greghaskins.spectrum.internal.Atomic;
import com.greghaskins.spectrum.internal.Child;
import com.greghaskins.spectrum.internal.NotifyingBlock;
import com.greghaskins.spectrum.internal.Parent;
import com.greghaskins.spectrum.internal.PreConditionBlock;
import com.greghaskins.spectrum.model.HookContext;
import com.greghaskins.spectrum.model.Hooks;
import com.greghaskins.spectrum.model.IdempotentBlock;
import com.greghaskins.spectrum.model.PreConditions;
import com.greghaskins.spectrum.model.SetupBlock;
import com.greghaskins.spectrum.model.TaggingState;
import com.greghaskins.spectrum.model.TeardownBlock;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Suite implements Parent, Child {

  private final SetupBlock beforeAll = new SetupBlock();
  private final TeardownBlock afterAll = new TeardownBlock();

  private final SetupBlock beforeEach = new SetupBlock();
  private final TeardownBlock afterEach = new TeardownBlock();

  private ThrowingConsumer<Block> aroundEach = Block::run;
  private ThrowingConsumer<Block> aroundAll = Block::run;

  // the hooks - they will be turned into a chain of responsibility
  // so the first one will be executed last as the chain is built up
  // from first to last.
  private Hooks hooks = new Hooks();

  protected final List<Child> children = new ArrayList<>();
  private final Set<Child> focusedChildren = new HashSet<>();

  private final ChildRunner childRunner;

  private final Description description;
  private final Parent parent;
  private boolean ignored;

  private final TaggingState tagging;
  private PreConditions preconditions = PreConditions.Factory.defaultPreConditions();
  private Set<String> namesUsed = new HashSet<>();

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
   *
   * @param description the JUnit description
   * @param parent parent item
   * @param childRunner which child running strategy to use - this will normally be
   *        {@link #defaultChildRunner(Suite, RunNotifier)} which runs them all but can be
   *        substituted.
   * @param taggingState the state of tagging inherited from the parent
   */
  protected Suite(final Description description, final Parent parent, final ChildRunner childRunner,
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
        new Suite(Description.createSuiteDescription(sanitise(name)), this, childRunner,
            this.tagging.clone());

    return withExtraValues(suite);
  }

  private Suite withExtraValues(Suite suite) {
    suite.beforeAll.addBlock(this.beforeAll);
    suite.beforeEach.addBlock(this.beforeEach);
    suite.afterEach.addBlock(this.afterEach);
    suite.aroundEach(this.aroundEach);
    addChild(suite);

    return suite;
  }

  Suite addAbortingSuite(final String name) {
    final Suite suite =
        new CompositeTest(Description.createSuiteDescription(sanitise(name)), this,
            this.tagging.clone());

    return withExtraValues(suite);
  }

  Child addSpec(final String name, final Block block) {
    final Child spec = createSpec(name, block);
    addChild(spec);

    return spec;
  }

  private Child createSpec(final String name, final Block block) {
    final Description specDescription =
        Description.createTestDescription(this.description.getClassName(), sanitise(name));

    final NotifyingBlock specBlockInContext = (description, notifier) -> {
      try {
        this.beforeAll.run();
      } catch (final Throwable exception) {
        notifier.fireTestFailure(new Failure(description, exception));
        return;
      }

      NotifyingBlock.wrap(() -> {

        Variable<Boolean> blockWasRun = new Variable<>(false);
        this.aroundEach.accept(() -> {
          blockWasRun.set(true);

          NotifyingBlock.wrap(() -> {
            this.beforeEach.run();
            block.run();
          }).run(description, notifier);

          this.afterEach.run(description, notifier);
        });


        if (!blockWasRun.get()) {
          throw new RuntimeException("aroundEach did not run the block");
        }

      }).run(description, notifier);
    };

    PreConditionBlock preConditionBlock =
        PreConditionBlock.with(this.preconditions.forChild(), block);

    return new Spec(specDescription, specBlockInContext, this).applyPreConditions(preConditionBlock,
        this.tagging);
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
   * Adds a hook to be the last one executed before the block.
   * This is the default. Hooks should be executed in the order they
   * are declared in the test.
   * @param hook to add
   */
  void addHook(final HookContext hook) {
    this.hooks.addFirst(hook);
  }

  /**
   * Insert a hook at the front - this is for situations where a hook is only creatable
   * after test definition, but is still to be run first.
   * @param hook to add
   */
  void insertHook(final HookContext hook) {
    this.hooks.add(hook);
  }

  Hooks getHooksFor(final Child child) {
    Hooks allHooks = getInheritedHooks().plus(hooks);

    return child instanceof Atomic ? allHooks.forAtomic() : allHooks.forNonAtomic();
  }

  @Override
  public Hooks getInheritedHooks() {
    // only the atomic hooks can be used by the children of this suite,
    // all other hooks would be executed at suite level only - either for
    // each child of the suite, or once

    return parent.getInheritedHooks().plus(hooks.forAtomic());
  }

  /**
   * Set the suite to require certain tags of all tests below.
   *
   * @param tags required tags - suites must have at least one of these if any are specified
   */
  void includeTags(final String... tags) {
    this.tagging.include(tags);
  }

  /**
   * Set the suite to exclude certain tags of all tests below.
   *
   * @param tags excluded tags - suites and specs must not have any of these if any are specified
   */
  void excludeTags(final String... tags) {
    this.tagging.exclude(tags);
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
      runSuite(notifier);
    }
  }

  private void runSuite(final RunNotifier notifier) {
    hooks.once().runAround(() -> doRunSuite(notifier));
  }

  private void doRunSuite(final RunNotifier notifier) {
    Variable<Boolean> blockWasCalled = new Variable<>(false);

    NotifyingBlock.wrap(() -> this.aroundAll.accept(() -> {
      blockWasCalled.set(true);
      runChildren(notifier);
      runAfterAll(notifier);
    })).run(this.description, notifier);

    if (!blockWasCalled.get()) {
      RuntimeException exception = new RuntimeException("aroundAll did not run the block");
      notifier.fireTestFailure(new Failure(this.description, exception));
    }
  }

  private void runChildren(final RunNotifier notifier) {
    this.childRunner.runChildren(this, notifier);
  }

  protected void runChild(final Child child, final RunNotifier notifier) {
    if (this.focusedChildren.isEmpty() || this.focusedChildren.contains(child)) {
      hooks.forThisLevel().runAround(
          () -> getHooksFor(child).runAround(() -> child.run(notifier)));
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

  private String sanitise(final String name) {
    String sanitised = name.replaceAll("\\(", "[")
        .replaceAll("\\)", "]");

    int suffix = 1;
    String deDuplicated = sanitised;
    while (this.namesUsed.contains(deDuplicated)) {
      deDuplicated = sanitised + "_" + suffix++;
    }
    this.namesUsed.add(deDuplicated);

    return deDuplicated;
  }

  public void aroundEach(ThrowingConsumer<Block> consumer) {
    ThrowingConsumer<Block> outerAroundEach = this.aroundEach;
    this.aroundEach = block -> {
      outerAroundEach.accept(() -> {
        consumer.accept(block);
      });

    };
  }

  public void aroundAll(ThrowingConsumer<Block> consumer) {
    this.aroundAll = consumer;

  }
}

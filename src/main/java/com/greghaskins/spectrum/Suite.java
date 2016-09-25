package com.greghaskins.spectrum;

import com.greghaskins.spectrum.Spectrum.Block;

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

  private final Description description;
  private final Parent parent;
  private boolean ignored;

  static Suite rootSuite(final Description description) {
    return new Suite(description, Parent.NONE);
  }

  private Suite(final Description description, final Parent parent) {
    this.description = description;
    this.parent = parent;
    this.ignored = parent.isIgnored();
  }

  Suite addSuite(final String name) {
    final Suite suite = new Suite(Description.createSuiteDescription(name), this);
    suite.beforeAll.addBlock(this.beforeAll);
    suite.beforeEach.addBlock(this.beforeEach);
    suite.afterEach.addBlock(this.afterEach);
    addChild(suite);

    return suite;
  }

  Spec addSpec(final String name, final Block block) {
    final Spec spec = createSpec(name, block);
    addChild(spec);

    return spec;
  }

  private Spec createSpec(final String name, final Block block) {
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

    return new Spec(specDescription, specBlockInContext, this);
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
    this.children.forEach((child) -> runChild(child, notifier));
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
    this.children.stream().forEach((child) -> copy.addChild(child.getDescription()));

    return copy;
  }

  @Override
  public int testCount() {
    return this.children.stream().mapToInt((child) -> child.testCount()).sum();
  }

  void removeAllChildren() {
    this.children.clear();
  }

}

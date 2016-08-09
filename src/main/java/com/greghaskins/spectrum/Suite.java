package com.greghaskins.spectrum;

import com.greghaskins.spectrum.Spectrum.Block;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Suite implements Parent, Child {

  private final CompositeBlock beforeAll = new CompositeBlock();
  private final CompositeBlock afterAll = new CompositeBlock();

  private final CompositeBlock beforeEach = new CompositeBlock();
  private final AfterEachBlock afterEach = new AfterEachBlock();

  private final List<Child> children = new ArrayList<>();
  private final Set<Child> focusedChildren = new HashSet<>();

  private final Description description;
  private final Parent parent;
  private boolean ignored;

  public static Suite rootSuite(final Description description) {
    return new Suite(description, Parent.NONE);
  }

  private Suite(final Description description, final Parent parent) {
    this.description = description;
    this.parent = parent;
    this.ignored = parent.isIgnored();
  }

  public Suite addSuite(final String name) {
    final Suite suite = new Suite(Description.createSuiteDescription(name), this);
    suite.beforeAll(this.beforeAll);
    suite.beforeEach(this.beforeEach);
    suite.afterEach(this.afterEach);
    addChild(suite);

    return suite;
  }

  public Spec addSpec(final String name, final Block block) {
    final Spec spec = createSpec(name, block);
    addChild(spec);

    return spec;
  }

  private Spec createSpec(final String name, final Block block) {
    final Description specDescription =
        Description.createTestDescription(this.description.getClassName(), name);

    final Block specBlockInContext = () -> {
      this.beforeAll.run();
      try {
        this.beforeEach.run();
        block.run();
      } catch (Throwable throwable) {
        try {
          this.afterEach.run();
        } catch (Throwable ignored) {
          // ignored
        }
        throw throwable;
      }
      this.afterEach.run();
    };

    return new Spec(specDescription, specBlockInContext, this);
  }

  private void addChild(final Child child) {
    this.children.add(child);
  }

  public void beforeAll(final Block block) {
    this.beforeAll.addBlock(new IdempotentBlock(block));
  }

  public void afterAll(final Block block) {
    this.afterAll.addBlock(block);
  }

  public void beforeEach(final Block block) {
    this.beforeEach.addBlock(block);
  }

  public void afterEach(final Block block) {
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
    try {
      this.afterAll.run();
    } catch (final Throwable error) {
      notifier.fireTestFailure(new Failure(this.description, error));
    }
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

  public void removeAllChildren() {
    this.children.clear();
  }

}

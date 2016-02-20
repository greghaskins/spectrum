package com.greghaskins.spectrum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import com.greghaskins.spectrum.Spectrum.Block;

class Suite implements Parent, Child {

	private final CompositeBlock beforeAll = new CompositeBlock();
	private final CompositeBlock afterAll = new CompositeBlock();

	private final CompositeBlock beforeEach = new CompositeBlock();
	private final CompositeBlock afterEach = new CompositeBlock();

	private final List<Child> children = new ArrayList<Child>();
	private final Set<Child> focusedChildren = new HashSet<Child>();

	private final Description description;
	private final Parent parent;

	public static Suite rootSuite(final Description description) {
		return new Suite(description, Parent.NONE);
	}

	private Suite(final Description description, final Parent parent) {
		this.description = description;
		this.parent = parent;
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
		final Description specDescription = Description.createTestDescription(this.description.getClassName(), name);

		final Block specBlockInContext = new TryFinallyBlock(new CompositeBlock(
				Arrays.asList(this.beforeAll, this.beforeEach, block)), this.afterEach);

		final Spec spec = new Spec(specDescription, specBlockInContext, this);
		addChild(spec);
		return spec;
	}

	private void addChild(final Child child) {
		this.description.addChild(child.getDescription());
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
	public void run(final RunNotifier notifier) {
		if (this.testCount() == 0) {
			notifier.fireTestIgnored(this.description);
			runChildren(notifier);
		} else {
			runChildren(notifier);
			runAfterAll(notifier);
		}
	}

	private void runChildren(final RunNotifier notifier) {
		for (final Child child : this.children) {
			runChild(child, notifier);
		}
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
		} catch (final Throwable e) {
			final Description failureDescription = Description.createTestDescription(this.description.getClassName(),
					"error in afterAll");
			this.description.addChild(failureDescription);
			notifier.fireTestFailure(new Failure(failureDescription, e));
		}
	}

	@Override
	public Description getDescription() {
		return this.description;
	}

	@Override
	public int testCount() {
		int count = 0;
		for (final Child child : this.children) {
			count += child.testCount();
		}
		return count;
	}

	@Override
	public void focus() {
		this.parent.focus(this);
	}

}

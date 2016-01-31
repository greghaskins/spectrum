package com.greghaskins.spectrum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import com.greghaskins.spectrum.Spectrum.Block;

class Suite extends Runner {

	private final CompositeBlock beforeAll = new CompositeBlock();
	private final CompositeBlock afterAll = new CompositeBlock();

	private final CompositeBlock beforeEach = new CompositeBlock();
	private final CompositeBlock afterEach = new CompositeBlock();

	private final List<Runner> children = new ArrayList<>();

	private final Description description;

	public Suite(final Description description) {
		this.description = description;
	}

	public Suite addSuite(final String name, final Block block) {
		final Suite suite = new Suite(Description.createSuiteDescription(name));
		addChild(suite);
		return suite;
	}

	public Spec addSpec(final String name, final Block block) {
		final CompositeBlock specBlockInContext = new CompositeBlock(Arrays.asList(this.beforeEach, block, this.afterEach));
		final Spec spec = new Spec(Description.createTestDescription(this.description.getClassName(), name), specBlockInContext);
		addChild(spec);
		return spec;
	}

	private void addChild(final Runner runner) {
		this.description.addChild(runner.getDescription());
		this.children.add(runner);
	}

	public void beforeAll(final Block block) {
		this.beforeAll.addBlock(block);
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
	public void run(final RunNotifier notifier) {
		if (this.testCount() == 0) {
			notifier.fireTestIgnored(this.description);
			return;
		}

		runOrFail(this.beforeAll, notifier, "error in beforeAll");
		this.children.stream().forEach((child) -> child.run(notifier));
		runOrFail(this.afterAll, notifier, "error in afterAll");
	}

	private void runOrFail(final Block block, final RunNotifier notifier, final String failureMessage) {
		try {
			block.run();
		} catch (final Throwable e) {
			final Description failureDescription = Description.createTestDescription(this.description.getClassName(), failureMessage);
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
		return this.children.stream().mapToInt((child) -> { return child.testCount(); }).sum();
	}



}

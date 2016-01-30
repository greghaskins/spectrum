package com.greghaskins.spectrum;

import java.util.ArrayList;
import java.util.List;

import com.greghaskins.spectrum.Spectrum.Block;

class Suite implements Trilogy {

	private final List<Block> beforeAll = new ArrayList<>();
	private final List<Block> afterAll = new ArrayList<>();

	private final List<Block> beforeEach = new ArrayList<>();
	private final List<Block> afterEach = new ArrayList<>();

	private final List<Trilogy> children = new ArrayList<>();

	@Override
	public void setUp() throws Throwable {
		for (final Block block : this.beforeAll) {
			block.run();
		}
	}

	@Override
	public void run() throws Throwable {
		for (final Trilogy trilogy : this.children) {
			trilogy.setUp();
			try {
				trilogy.run();
			} finally {
				trilogy.tearDown();
			}
		}
	}

	@Override
	public void tearDown() throws Throwable {
		for (final Block block : this.afterAll) {
			block.run();
		}
	}

	public void addSuite(final Suite child) {
		this.children.add(child);
	}

	public void addSpec(final String behavior, final Block block) {
		this.children.add(new Spec(
				new CompositeBlock(this.beforeEach),
				block,
				new CompositeBlock(this.afterEach)
				));
	}

}

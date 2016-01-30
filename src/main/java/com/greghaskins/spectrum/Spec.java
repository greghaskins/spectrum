package com.greghaskins.spectrum;

import com.greghaskins.spectrum.Spectrum.Block;

class Spec implements Trilogy {

	private final Block setup;
	private final Block teardown;
	private final Block test;

	public Spec(final Block setup, final Block test, final Block teardown) {
		this.setup = setup;
		this.test = test;
		this.teardown = teardown;
	}

	@Override
	public void setUp() throws Throwable {
		this.setup.run();
	}

	@Override
	public void run() throws Throwable {
		this.test.run();
	}

	@Override
	public void tearDown() throws Throwable {
		this.teardown.run();
	}


}

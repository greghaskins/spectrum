package com.greghaskins.spectrum;

import com.greghaskins.spectrum.Spectrum.Block;

class RunOnceBlock implements Block {

    private Block block;

    public RunOnceBlock(final Block block) {
        this.block = block;
    }

    @Override
    public void run() throws Throwable {
        try {
            block.run();
        } catch (final Throwable e) {
            block = new FailingBlock(e);
            throw e;
        }
        block = new EmptyBlock();
    }

}

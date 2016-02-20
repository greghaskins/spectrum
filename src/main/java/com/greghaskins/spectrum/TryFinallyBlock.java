package com.greghaskins.spectrum;

import com.greghaskins.spectrum.Spectrum.Block;

class TryFinallyBlock implements Block {

    private final Block tryBlock;
    private final Block finallyBlock;

    public TryFinallyBlock(final Block tryBlock, final Block finallyBlock) {
        this.tryBlock = tryBlock;
        this.finallyBlock = finallyBlock;
    }

    @Override
    public void run() throws Throwable {
        try {
            tryBlock.run();
        } finally {
            finallyBlock.run();
        }

    }

}

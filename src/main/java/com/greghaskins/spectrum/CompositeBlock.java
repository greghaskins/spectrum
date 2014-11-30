package com.greghaskins.spectrum;

import java.util.Arrays;

import com.greghaskins.spectrum.Spectrum.Block;

class CompositeBlock implements Block {

    private final Iterable<? extends Block> blocks;

    public CompositeBlock(final Iterable<? extends Block> blocks) {
        this.blocks = blocks;
    }

    public CompositeBlock(final Block... blocks) {
        this(Arrays.asList(blocks));
    }

    @Override
    public void run() throws Throwable {
        for (final Block block : blocks) {
            block.run();
        }
    }

}

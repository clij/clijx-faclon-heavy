package net.haesleinhuepf.clijx.faclonheavy;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;

import java.util.function.BiConsumer;

public class DummyFilter extends AbstractTileProcessor {

    @Override
    public void accept(ClearCLBuffer clearCLBuffer, ClearCLBuffer clearCLBuffer2) {
        clijx.differenceOfGaussian(clearCLBuffer, clearCLBuffer2, 1, 2, 3, 4, 5, 6);
    }
}

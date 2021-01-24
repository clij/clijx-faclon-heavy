package net.haesleinhuepf.clijx.faclonheavy;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;

import java.util.function.BiConsumer;

public interface TileProcessor extends BiConsumer<ClearCLBuffer, ClearCLBuffer> {
    void setCLIJx(CLIJx clijx);
    CLIJx getCLIJx();
}

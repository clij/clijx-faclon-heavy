package net.haesleinhuepf.clijx.faclonheavy;

import net.haesleinhuepf.clijx.CLIJx;

public abstract class AbstractTileProcessor implements TileProcessor {
    protected CLIJx clijx;

    @Override
    public void setCLIJx(CLIJx clijx) {
        this.clijx = clijx;
    }
}

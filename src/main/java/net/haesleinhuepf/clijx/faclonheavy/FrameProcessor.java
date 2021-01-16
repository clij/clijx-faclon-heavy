package net.haesleinhuepf.clijx.faclonheavy;

import ij.ImagePlus;
import net.haesleinhuepf.clij2.CLIJ2;

public interface FrameProcessor {
    void setCLIJ2(CLIJ2 clij2);
    CLIJ2 getCLIJ2();
    ImagePlus process(ImagePlus input);
    FrameProcessor duplicate();

    void setFrame(int frame);
    int getFrame();

    long getMemoryNeedInBytes(ImagePlus imp);
}

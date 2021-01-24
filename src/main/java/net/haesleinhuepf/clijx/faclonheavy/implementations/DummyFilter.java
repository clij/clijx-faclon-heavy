package net.haesleinhuepf.clijx.faclonheavy.implementations;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.faclonheavy.AbstractTileProcessor;

import java.util.function.BiConsumer;

/**
 * An example implementation of an image processing workflow that can be executed on tiles of images.
 *
 * @author: Robert Haase
 */
public class DummyFilter extends AbstractTileProcessor {

    @Override
    public void accept(ClearCLBuffer input, ClearCLBuffer output) {
        // allocated temporary memory
        ClearCLBuffer temp = clijx.create(input);

        // process the image
        clijx.differenceOfGaussian(input, temp, 1, 2, 3, 4, 5, 6);
        clijx.thresholdOtsu(temp, output);

        // clean up
        temp.close();
    }
}

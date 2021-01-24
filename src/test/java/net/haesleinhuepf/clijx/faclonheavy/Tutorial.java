package net.haesleinhuepf.clijx.faclonheavy;

import java.io.IOException;

import net.haesleinhuepf.clijx.faclonheavy.implementations.DummyFilter;
import org.janelia.saalfeldlab.i2k2020.ops.CLIJxFilterOp;
import org.janelia.saalfeldlab.i2k2020.util.Lazy;
import org.janelia.saalfeldlab.i2k2020.util.N5Factory;
import org.janelia.saalfeldlab.n5.N5Reader;
import org.janelia.saalfeldlab.n5.imglib2.N5Utils;

import bdv.util.BdvFunctions;
import bdv.util.BdvOptions;
import bdv.util.BdvStackSource;
import bdv.util.volatiles.SharedQueue;
import bdv.util.volatiles.VolatileViews;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converters;
import net.imglib2.img.basictypeaccess.AccessFlags;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * Process an image tile-by-tile on multiple GPUs
 *
 * This code originates from https://github.com/saalfeldlab/i2k2020-imglib2-advanced/blob/main/src/main/java/org/janelia/saalfeldlab/i2k2020/LazyTutorial3.java
 *
 * @author Stephan Saalfeld, Robert Haase
 */
public class Tutorial {

    public static final void main(final String... args) throws IOException {

        // define data location. If you don't have this dataset, create it using the MakeBigData class in the same folder
        final N5Reader n5 = N5Factory.openReader("C:/structure/data/n5/example.n5");
        final RandomAccessibleInterval<FloatType> img = N5Utils.openVolatile(n5, "/volumes/raw");
        // convert the input image from any type to FloatType
        final RandomAccessibleInterval<FloatType> floats = Converters.convert(img, (a, b) -> b.set(a.getRealFloat()), new FloatType());

        // create a pool of clijx instances representing OpenCL contexts exeucted on different GPUs
        CLIJxPool pool = CLIJxPool.fromDeviceNames(
                new String[]{"Intel(R) UHD Graphics 620", "RTX"},
                new int[]{   1,     4});
        System.out.println("CLIJ pool size: " + pool.size());
        System.out.println("CLIJ pool: \n" + pool.log());

        // create a processor representing the workflow we want to apply to the image
        final CLIJxFilterOp<FloatType, FloatType> clijxFilter =
                new CLIJxFilterOp<FloatType, FloatType>(Views.extendMirrorSingle(floats), pool, DummyFilter.class, 20, 20, 20);

        // open a BigDataViewer
        final SharedQueue queue = new SharedQueue(Math.max(1, pool.size()));
        BdvStackSource<?> bdv = BdvFunctions.show(VolatileViews.wrapAsVolatile(img, queue), "source");
        bdv.setDisplayRange(24000, 32000);
        // show result image lazily in the BigDataViewer
        final RandomAccessibleInterval<FloatType> filtered = Lazy.generate(
                img,
                new int[] {256, 256, 256},
                new FloatType(),
                AccessFlags.setOf(AccessFlags.VOLATILE),
                clijxFilter);
        bdv = BdvFunctions.show(VolatileViews.wrapAsVolatile(filtered, queue), "DoG", BdvOptions.options().addTo(bdv));
        bdv.setDisplayRange(-1000, 1000);
    }
}

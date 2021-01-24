package net.haesleinhuepf.clijx.faclonheavy;

import java.io.IOException;

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
 * @author Stephan Saalfeld, Robert Haase
 */
public class LazyTutorial3 {

    public static final void main(final String... args) throws IOException {


        final N5Reader n5 = N5Factory.openReader("C:/structure/data/n5/example.n5");
        final RandomAccessibleInterval<FloatType> img = N5Utils.openVolatile(n5, "/volumes/raw");

        //final N5Reader n5 = N5Factory.openReader("C:/structure/data/n5/example.n5");
        //final RandomAccessibleInterval<FloatType> img = N5Utils.openVolatile(n5, "/volumes/raw");

        CLIJxPool pool = CLIJxPool.fromDeviceNames(
                new String[]{"620", "520", "RTX", "gfx9"},
                new int[]{   1,     2,     4,     2});
        System.out.println("CLIJ pool size: " + pool.size());

        final SharedQueue queue = new SharedQueue(Math.max(1, pool.size()));
        BdvStackSource<?> bdv = BdvFunctions.show(VolatileViews.wrapAsVolatile(img, queue), "source");
        bdv.setDisplayRange(24000, 32000);

        final RandomAccessibleInterval<FloatType> floats =
                Converters.convert(
                        img,
                        (a, b) -> b.set(a.getRealFloat()),
                        new FloatType());

        final CLIJxFilterOp<FloatType, FloatType> clijxFilter =
                new CLIJxFilterOp<>(Views.extendMirrorSingle(floats), pool, new DummyFilter(), 20, 20, 20);

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

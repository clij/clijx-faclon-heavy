package net.haesleinhuepf.clijx.faclonheavy;

import ij.IJ;
import ij.ImagePlus;
import net.imglib2.RealRandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.realtransform.*;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;
import org.janelia.saalfeldlab.n5.GzipCompression;
import org.janelia.saalfeldlab.n5.N5FSWriter;
import org.janelia.saalfeldlab.n5.imglib2.N5Utils;

import java.io.IOException;

public class MakeBigData {
    public static void main(String[] args) throws IOException {
        String source_file = "data/Lund_000500_resampled-cropped.tif";
        String target_folder = "data/n5/";

        long scale_factor = 100;

        ImagePlus imp = IJ.openImage(source_file);

        Img img = ImageJFunctions.convertFloat(imp);

        AffineTransform3D at = new AffineTransform3D();
        at.scale(scale_factor);

        final RealRandomAccessible<FloatType> field = Views.interpolate( Views.extendZero( img ) , new NLinearInterpolatorFactory<FloatType>());

        AffineRandomAccessible<FloatType, AffineGet> transformed = RealViews.affine(field, at);

        final IntervalView< FloatType > new_img = Views.interval(transformed, new long[]{0,0,0}, new long[]{imp.getWidth() * scale_factor, imp.getHeight() * scale_factor, imp.getNSlices() * scale_factor});

        N5Utils.save(
                new_img,
                new N5FSWriter(target_folder + "example.n5"),
                "/volumes/raw",
                new int[] {128, 128, 64},
                new GzipCompression()
        );
    }
}

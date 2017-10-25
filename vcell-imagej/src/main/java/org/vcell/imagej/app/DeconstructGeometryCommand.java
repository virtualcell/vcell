package org.vcell.imagej.app;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.display.DisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.axis.Axes;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Intervals;
import net.imglib2.view.IntervalView;

/**
 * Created by kevingaffney on 6/20/17.
 */
@Plugin(type = Command.class, menuPath = "Plugins>VCell>Deconstruct Geometry")
public class DeconstructGeometryCommand<T extends RealType<T>> implements Command {

    @Parameter(label = "Fluorescence results")
    private Dataset fluorData;

    @Parameter(label = "Geometry definition")
    private Dataset geomData;

    @Parameter(label = "Wavelength (nm)")
    private Double lambda;

    @Parameter(label = "Angle of incidence (˚):")
    private Double theta;

    @Parameter(label = "Z-spacing for geometry (nm/slice)")
    private Double zSpacing;

    @Parameter
    private OpService ops;

    @Parameter
    private DisplayService displayService;

    @Parameter
    private DatasetService datasetService;

    @Parameter(type = ItemIO.OUTPUT)
    private Dataset output;

    @Override
    public void run() {

        // Crop to get a z-stack over time (remove channel dimension)
        long maxX = fluorData.max(fluorData.dimensionIndex(Axes.X));
        long maxY = fluorData.max(fluorData.dimensionIndex(Axes.Y));
        long maxZ = fluorData.max(fluorData.dimensionIndex(Axes.Z));
        long maxTime = fluorData.max(fluorData.dimensionIndex(Axes.TIME));
        Img fluorImg = fluorData.getImgPlus().getImg();
        FinalInterval intervals = Intervals.createMinMax(0, 0, 0, 0, 0, maxX, maxY, maxZ, 0, maxTime);
        RandomAccessibleInterval fluorImgCropped = ops.transform().crop(fluorImg, intervals, true);

        // Calculate scale factors
        double[] scaleFactors = {1, 1, 1, 1};
        for (int i = 0; i < geomData.numDimensions(); i++) {
            scaleFactors[i] = geomData.dimension(i) / (double) fluorImgCropped.dimension(i);
        }

        // Scale the fluorescence dataset to match the geometry
        NLinearInterpolatorFactory interpolatorFactory = new NLinearInterpolatorFactory();
        RandomAccessibleInterval fluorScaled = ops.transform().scale(fluorImgCropped, scaleFactors, interpolatorFactory);

        // Crop out the first slice of each z-stack in time series
        intervals = Intervals.createMinMax(0, 0, 0, 0, fluorScaled.dimension(0) - 1,
                fluorScaled.dimension(1) - 1, 0, fluorScaled.dimension(3) - 1);
        IntervalView fluorXYT = (IntervalView) ops.transform().crop(fluorScaled, intervals, true);

        // Create a blank image of the same X-Y-Time dimensions
        long[] dimensions = {fluorXYT.dimension(0), fluorXYT.dimension(1), fluorXYT.dimension(2)};
        Img<DoubleType> result = ops.create().img(dimensions);

        // Calculate constant d in TIRF exponential decay function
        theta = theta * 2 * Math.PI / 360;
        double n1 = 1.52;
        double n2 = 1.38;
        double d = lambda * Math.pow((Math.pow(n1, 2) * Math.pow(Math.sin(theta), 2) - Math.pow(n2,2)), -0.5) / (4 * Math.PI);

        // Iterate through each time point, using 3D geometry to generate 2D intensities
        Cursor<DoubleType> cursor = fluorXYT.localizingCursor();
        RandomAccess fluorRA = fluorScaled.randomAccess();
        RandomAccess<RealType<?>> geomRA = geomData.randomAccess();
        RandomAccess<DoubleType> resultRA = result.randomAccess();
        maxZ = geomData.dimension(2) - 1;
        while (cursor.hasNext()) {
            cursor.fwd();
            int[] positionXYZ = {cursor.getIntPosition(0), cursor.getIntPosition(1), (int) maxZ - 1};
            int[] positionXYZT = {cursor.getIntPosition(0), cursor.getIntPosition(1), (int) maxZ - 1,
                    cursor.getIntPosition(2)};
            resultRA.setPosition(cursor);
            geomRA.setPosition(positionXYZ);
            double sum = 0.0;
            while (positionXYZ[2] >= 0 && geomRA.get().getRealDouble() != 0.0) {
                fluorRA.setPosition(positionXYZT);
                geomRA.setPosition(positionXYZ);
                sum += geomRA.get().getRealDouble() * Math.exp(-zSpacing * positionXYZ[2] / d);
                positionXYZ[2]--;
            }
            resultRA.get().set(sum);
        }
        System.out.println("done");
        displayService.createDisplay(result);
    }
}

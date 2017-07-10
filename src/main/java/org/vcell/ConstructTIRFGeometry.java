package org.vcell;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ops.AbstractOp;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imglib2.*;
import net.imglib2.algorithm.labeling.ConnectedComponents;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.roi.labeling.*;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;
import org.scijava.ItemIO;
import org.scijava.display.DisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.Iterator;

/**
 * Created by kevingaffney on 6/28/17.
 */
@Plugin(type = Op.class, name = "constructTIRFGeometry")
public class ConstructTIRFGeometry<T extends RealType<T>> extends AbstractOp {

    @Parameter
    private RandomAccessibleInterval<T> data;

    @Parameter
    private int sliceIndex;

    @Parameter(label = "Wavelength (nm):")
    private double lambda;

    @Parameter(label = "Angle of incidence (˚):")
    private double theta;

    @Parameter(label = "Z-resolution (nm/slice):")
    private double zRes;

    @Parameter
    private OpService ops;

    @Parameter
    private DatasetService datasetService;

    @Parameter
    private DisplayService displayService;

    @Parameter(type = ItemIO.OUTPUT)
    private Dataset output;

    @Override
    public void run() {

        // Get frame of interest to define geometry
        long maxX = data.dimension(0) - 1;
        long maxY = data.dimension(1) - 1;
        Interval interval = Intervals.createMinMax(0, 0, sliceIndex, maxX, maxY, sliceIndex);
        RandomAccessibleInterval<T> croppedRAI = ops.transform().crop(data, interval, true);

        // Subtract lowest pixel value
        IterableInterval<T> dataII = Views.iterable(croppedRAI);
        double min = ops.stats().min(dataII).getRealDouble();
        Cursor<T> dataCursor = dataII.cursor();
        while (dataCursor.hasNext()) {
            double val = dataCursor.next().getRealDouble();
            dataCursor.get().setReal(val - min);
        }

        // Perform Gaussian blur
        RandomAccessibleInterval<T> blurredRAI = ops.filter().gauss(croppedRAI, 2);
        IterableInterval<T> blurredII = Views.iterable(blurredRAI);

        // Segment slice by threshold and fill holes
        IterableInterval<BitType> thresholded = ops.threshold().huang(blurredII);
        Img<BitType> thresholdedImg = ops.convert().bit(thresholded);
        RandomAccessibleInterval<BitType> thresholdedRAI = ops.morphology().fillHoles(thresholdedImg);

        // Get the largest region
        RandomAccessibleInterval<LabelingType<ByteType>> labeling = ops.labeling().cca(thresholdedRAI,
                ConnectedComponents.StructuringElement.EIGHT_CONNECTED);
        LabelRegions<ByteType> labelRegions = new LabelRegions<>(labeling);
        Iterator<LabelRegion<ByteType>> iterator = labelRegions.iterator();
        LabelRegion<ByteType> maxRegion = iterator.next();
        while (iterator.hasNext()) {
            LabelRegion<ByteType> currRegion = iterator.next();
            if (currRegion.size() > maxRegion.size()) {
                maxRegion = currRegion;
            }
        }

        // Generate z index map
        double iMax = ops.stats().max(dataII).getRealDouble();
        Img<UnsignedShortType> dataImg = ops.convert().uint16(dataII);
        Img<UnsignedShortType> zMap = ops.convert().uint16(ops.create().img(dataII));
        LabelRegionCursor cursor = maxRegion.localizingCursor();
        RandomAccess<UnsignedShortType> zMapRA = zMap.randomAccess();
        RandomAccess<UnsignedShortType> dataRA = dataImg.randomAccess();

        theta = theta * 2 * Math.PI / 360; // Angle of incidence in radians
        double n1 = 1.52; // Refractive index of glass
        double n2 = 1.38; // Refractive index of cytosol
        double d = lambda * Math.pow((Math.pow(n1, 2) * Math.pow(Math.sin(theta), 2) - Math.pow(n2, 2)), -0.5) / (4 * Math.PI);

        while (cursor.hasNext()) {
            cursor.fwd();
            zMapRA.setPosition(cursor);
            dataRA.setPosition(cursor);
            double val = dataRA.get().getRealDouble();
            int z = (int) Math.round(-d * Math.log(val / iMax) / zRes);
            zMapRA.get().set(z);
        }

        // Use map to construct 3D geometry
        int maxZ = (int) ops.stats().max(zMap).getRealDouble() + 5; // Add 5 slices of padding on top
        long[] resultDimensions = {maxX, maxY, maxZ};

        Img<BitType> result = new ArrayImgFactory<BitType>()
                .create(resultDimensions, new BitType());
        RandomAccess<BitType> resultRA = result.randomAccess();

        cursor.reset();
        while (cursor.hasNext()) {
            cursor.fwd();
            zMapRA.setPosition(cursor);
            int zIndex = zMapRA.get().get();
            int[] position = {cursor.getIntPosition(0), cursor.getIntPosition(1), zIndex};
            while (position[2] < maxZ) {
                resultRA.setPosition(position);
                resultRA.get().set(true);
                position[2]++;
            }
        }

        output = datasetService.create(result);
        System.out.println("Done constructing geometry");
    }
}

package org.vcell;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ops.AbstractOp;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imglib2.*;
import net.imglib2.algorithm.labeling.ConnectedComponents;
import net.imglib2.img.Img;
import net.imglib2.roi.labeling.*;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Intervals;
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
    private Img<T> data;

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
    private CopyConvert<T> copyConvert;

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

        // Copy RAI to II to find minimum pixel value
        IterableInterval<T> subtractedII = copyConvert.raiToIi(croppedRAI);
        double min = ops.stats().min(subtractedII).getRealDouble();

        // Subtract lowest pixel value
        Cursor<T> croppedCursor = subtractedII.cursor();
        while (croppedCursor.hasNext()) {
            double val = croppedCursor.next().getRealDouble();
            croppedCursor.get().setReal(val - min);
        }

        // Segment slice
        RandomAccessibleInterval<T> blurredRAI = ops.filter().gauss(croppedRAI, 2);
        IterableInterval<T> blurredII = copyConvert.raiToIi(blurredRAI);
        IterableInterval<BitType> thresholded = ops.threshold().huang(blurredII);

        Img<BitType> thresholdedImg = ops.create().img(thresholded);
        Cursor<BitType> thresholdedCursor = thresholded.localizingCursor();
        RandomAccess<BitType> thresholdedImgRA = thresholdedImg.randomAccess();
        while (thresholdedCursor.hasNext()) {
            thresholdedCursor.fwd();
            thresholdedImgRA.setPosition(thresholdedCursor);
            thresholdedImgRA.get().set(thresholdedCursor.get());
        }
        RandomAccessibleInterval<BitType> thresholdedRAI = ops.morphology().fillHoles(thresholdedImg);


        // Get the largest region
        RandomAccessibleInterval<LabelingType<ByteType>> labeling = ops.labeling().cca(thresholdedRAI, ConnectedComponents.StructuringElement.EIGHT_CONNECTED);
        LabelRegions<ByteType> labelRegions = new LabelRegions<>(labeling);
        Iterator<LabelRegion<ByteType>> iterator = labelRegions.iterator();
        System.out.println(labelRegions.getExistingLabels().size());
        LabelRegion<ByteType> maxRegion = iterator.next();
        while (iterator.hasNext()) {
            LabelRegion<ByteType> currRegion = iterator.next();
            if (currRegion.size() > maxRegion.size()) {
                maxRegion = currRegion;
            }
        }

        // Generate z index map
        double iMax = ops.stats().max(subtractedII).getRealDouble();
        Img<T> subtractedImg = copyConvert.iiToImg(subtractedII);
        Img<UnsignedShortType> zMap = ops.convert().uint16(ops.create().img(subtractedII));
        LabelRegionCursor cursor = maxRegion.localizingCursor();
        RandomAccess<UnsignedShortType> zMapRA = zMap.randomAccess();
        RandomAccess<T> dataRA = subtractedImg.randomAccess();

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
        int maxZ = (int) ops.stats().max(zMap).getRealDouble();
        long[] resultDimensions = {maxX, maxY, maxZ};
        Img<DoubleType> result = ops.create().img(resultDimensions);
        RandomAccess<DoubleType> resultRA = result.randomAccess();

        cursor.reset();
        while (cursor.hasNext()) {
            cursor.fwd();
            zMapRA.setPosition(cursor);
            int zIndex = zMapRA.get().get();
            int[] position = {cursor.getIntPosition(0), cursor.getIntPosition(1), zIndex};
            while (position[2] < maxZ) {
                resultRA.setPosition(position);
                resultRA.get().set(1);
                position[2]++;
            }
        }

        output = datasetService.create(result);
        System.out.println("Done constructing :)");
    }
}

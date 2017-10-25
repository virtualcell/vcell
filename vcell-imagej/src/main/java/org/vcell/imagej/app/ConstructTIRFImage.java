package org.vcell.imagej.app;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.axis.Axes;
import net.imagej.axis.CalibratedAxis;
import net.imagej.axis.DefaultLinearAxis;
import net.imagej.ops.AbstractOp;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedShortType;

/**
 * Created by kevingaffney on 7/5/17.
 */
@Plugin(type = Op.class, name = "constructTIRFImage")
public class ConstructTIRFImage<T extends RealType<T>> extends AbstractOp {

    /**
     * Geometry definition.
     */
    @Parameter
    private RandomAccessibleInterval<BitType> geometry;

    /**
     * Results from Virtual Cell. Only dimension is time.
     */
    @Parameter
    private RandomAccessibleInterval<T> membraneResults;

    /**
     * Results from Virtual Cell. First dimension is z, second dimension is time.
     */
    @Parameter
    private RandomAccessibleInterval<T> volumeResults;

    @Parameter(label = "Wavelength (nm)")
    private Double lambda;

    @Parameter(label = "Angle of incidence (˚):")
    private Double theta;

    @Parameter(label = "Z-spacing for geometry (nm/slice)")
    private Double zSpacing;

    @Parameter(label = "XY-spacing for geometry (um/pixel")
    private Double pixelSpacing;

    @Parameter
    private OpService ops;

    @Parameter
    private DatasetService datasetService;

    @Parameter(type = ItemIO.OUTPUT)
    private Dataset output;

    @Override
    public void run() {

        // Calculate constant d in TIRF exponential decay function
        theta = theta * 2 * Math.PI / 360;
        final double n1 = 1.52;
        final double n2 = 1.38;
        final double d = lambda * Math.pow((Math.pow(n1, 2) * Math.pow(Math.sin(theta), 2) - Math.pow(n2,2)), -0.5) / (4 * Math.PI);

        // Calculate pixel area and voxel volume and conversion factors
        final double pixelArea = pixelSpacing * pixelSpacing; // um2
        final double voxelVolume = pixelArea * zSpacing / 1000; // um3
        final double kmole = 1.0 / 602.0; // uM.um3.molecules-1
        final double fluorPerMolecule = 270;

        // Create empty image of X-Y-Time dimensions
        long[] dimensions = {geometry.dimension(0), geometry.dimension(1), volumeResults.dimension(1)};
        Img<UnsignedShortType> img = ops.convert().uint16(ops.create().img(dimensions));

        Cursor<UnsignedShortType> imgCursor = img.localizingCursor();
        RandomAccess<BitType> geomRA = geometry.randomAccess();
        RandomAccess<T> volumeResultsRA = volumeResults.randomAccess();
        RandomAccess<T> membraneResultsRA = membraneResults.randomAccess();
        long zMax = geometry.dimension(2) - 1;
        int time = 0;

        while (imgCursor.hasNext()) {

            if (imgCursor.getIntPosition(2) != time) {
                time = imgCursor.getIntPosition(2);
            }

            imgCursor.fwd();
            long[] posXYZ = {imgCursor.getLongPosition(0), imgCursor.getLongPosition(1), zMax};
            geomRA.setPosition(posXYZ);

            // Move down z-axis until bottom mask is reached
            if (geomRA.get().get()) {
                while (geomRA.get().get() && posXYZ[2] > 0) {
                    posXYZ[2]--;
                    geomRA.setPosition(posXYZ);
                }

                // Reset the results random access
                membraneResultsRA.setPosition(imgCursor.getIntPosition(2), 0);
                int resultsPosZ = 0;
                volumeResultsRA.setPosition(resultsPosZ, 0);
                volumeResultsRA.setPosition(imgCursor.getIntPosition(2), 1);

                double sum = 0.0;

                // Add membrane component
                final double membraneMolecules = membraneResultsRA.get().getRealDouble() * pixelArea;
                sum += membraneMolecules * Math.exp(-zSpacing * posXYZ[2] / d) * fluorPerMolecule;

                // Integrate  over z for cytosol component
                while (posXYZ[2] < zMax) {
                    final double volumeMolecules = volumeResultsRA.get().getRealDouble() * voxelVolume / kmole;
                    sum += volumeMolecules * Math.exp(-zSpacing * posXYZ[2] / d) * fluorPerMolecule;
                    posXYZ[2]++;
                    resultsPosZ++;
                    volumeResultsRA.setPosition(resultsPosZ, 0);
                }
                imgCursor.get().set((int) sum);
            }
        }

        output = datasetService.create(img);
        
        CalibratedAxis[] axes = new DefaultLinearAxis[] {
        		new DefaultLinearAxis(Axes.X),
        		new DefaultLinearAxis(Axes.Y),
        		new DefaultLinearAxis(Axes.TIME)
        };
        
        output.setAxes(axes);
    }
}

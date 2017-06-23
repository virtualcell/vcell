package org.vcell;

import net.imagej.Dataset;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import org.scijava.command.Command;
import org.scijava.display.DisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Created by kevingaffney on 6/22/17.
 */
@Plugin(type = Command.class, menuPath = "Plugins>VCell>Ratio Map")
public class RatioMap<T extends RealType<T>> implements Command {

    @Parameter
    private Img img1;

    @Parameter
    private Img img2;

    @Parameter
    private OpService ops;

    @Parameter
    private DisplayService displayService;

    @Override
    public void run() {

        long[] dimensions = new long[img1.numDimensions()];
        for (int i = 0; i < dimensions.length; i++) {
            dimensions[i] = img1.dimension(i);
        }

        Img<DoubleType> result = ops.create().img(dimensions);

        Cursor<DoubleType> resultCursor = result.localizingCursor();
        RandomAccess<RealType> img1RA = img1.randomAccess();
        RandomAccess<RealType> img2RA = img2.randomAccess();
        while (resultCursor.hasNext()) {
            resultCursor.fwd();
            img1RA.setPosition(resultCursor);
            img2RA.setPosition(resultCursor);
            resultCursor.get().set(img1RA.get().getRealDouble() / img2RA.get().getRealDouble() - 1.0);
        }

        displayService.createDisplay(result);
    }
}

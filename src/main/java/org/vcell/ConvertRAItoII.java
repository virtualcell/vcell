package org.vcell;

import net.imagej.ops.AbstractOp;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.view.Views;
import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Created by kevingaffney on 6/29/17.
 */
@Plugin(type = CopyConvert.RAItoII.class, name = CopyConvert.RAItoII.NAME)
public class ConvertRAItoII<T extends RealType<T>> extends AbstractOp {

    @Parameter
    private RandomAccessibleInterval<T> rai;

    @Parameter(type = ItemIO.OUTPUT)
    private IterableInterval<T> ii;

    @Override
    public void run() {
        ii = Views.iterable(rai);
        Cursor<T> targetCursor = ii.localizingCursor();
        RandomAccess<T> sourceRA = rai.randomAccess();
        while (targetCursor.hasNext()) {
            targetCursor.fwd();
            sourceRA.setPosition(targetCursor);
            targetCursor.get().set(sourceRA.get());
        }
    }
}

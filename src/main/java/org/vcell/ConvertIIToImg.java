package org.vcell;

import net.imagej.ops.AbstractOp;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Created by kevingaffney on 6/29/17.
 */
@Plugin(type = Op.class, name = CopyConvert.IItoImg.NAME)
public class ConvertIIToImg<T extends RealType<T>> extends AbstractOp {

    @Parameter
    private IterableInterval<T> ii;

    @Parameter(type = ItemIO.OUTPUT)
    private Img<UnsignedShortType> img;

    @Parameter
    private OpService ops;

    @Override
    public void run() {
        img = ops.convert().uint16(ops.create().img(ii));
        Cursor<T> iiCursor = ii.localizingCursor();
        RandomAccess<UnsignedShortType> imgRA = img.randomAccess();
        while (iiCursor.hasNext()) {
            iiCursor.fwd();
            imgRA.setPosition(iiCursor);
            imgRA.get().set((int) iiCursor.get().getRealDouble());
        }
    }
}

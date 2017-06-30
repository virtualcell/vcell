package org.vcell;

import net.imagej.ops.AbstractNamespace;
import net.imagej.ops.Namespace;
import net.imagej.ops.Op;
import net.imagej.ops.OpMethod;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import org.scijava.plugin.Plugin;

/**
 * Created by kevingaffney on 6/29/17.
 */
@Plugin(type = Namespace.class)
public class CopyConvert<T> extends AbstractNamespace {

    @Override
    public String getName() {
        return "name";
    }

    public interface RAItoII extends Op {
        String NAME = "copyConvert.raiToIi";
    }

    @OpMethod(op = CopyConvert.RAItoII.class)
    public IterableInterval<T> raiToIi(final RandomAccessibleInterval<T> rai) {
        return (IterableInterval<T>) ops().run(ConvertRAItoII.class, rai);
    }

    public interface IItoImg extends Op {
        String NAME = "copyConvert.iiToImg";
    }

    public Img<T> iiToImg(final IterableInterval<T> ii) {
        return (Img<T>) ops().run(ConvertIIToImg.class, ii);
    }


}

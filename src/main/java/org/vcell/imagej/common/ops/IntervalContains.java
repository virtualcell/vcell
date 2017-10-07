package org.vcell.imagej.common.ops;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ops.AbstractOp;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;

@Plugin(type = Op.class, name = "intervalContains")
public class IntervalContains<T extends RealType<T>> extends AbstractOp {
	
	@Parameter
	private IterableInterval<T> in;
	
	@Parameter
	private double value;
	
	@Parameter(required = false)
	private RandomAccessibleInterval<BitType> mask;
	
	@Parameter(type = ItemIO.OUTPUT)
	private boolean contains;
	
	@Parameter
	private OpService opService;
	
	@Override
	public void run() {
		
		contains = false;
		
		RandomAccess<BitType> maskRA = null;
		if (mask != null) {
			maskRA = mask.randomAccess();
		}
		
		Cursor<T> cursor = in.cursor();
		while (cursor.hasNext()) {
			cursor.fwd();
			
			if (maskRA != null) {
				maskRA.setPosition(cursor);
				if (!maskRA.get().get()) continue;
			}
			
			if (cursor.get().getRealDouble() == value) {
				contains = true;
				break;
			}
		}
	}
}

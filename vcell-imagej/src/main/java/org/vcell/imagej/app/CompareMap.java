package org.vcell.imagej.app;

import org.scijava.display.Display;
import org.scijava.display.DisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ops.AbstractOp;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.DoubleType;

@Plugin(type = Op.class, name = "compareMap")
public class CompareMap<T extends RealType<T>> extends AbstractOp {
	
	public static final int DELTA = 0;
	public static final int RATIO = 1;
	public static final int PERCENT_DIFFERENCE = 2;
	
	@Parameter
	private IterableInterval<T> data0;
	
	@Parameter
	private IterableInterval<T> data1;
	
	@Parameter
	private int comparisonType;
	
	@Parameter(required = false)
	private RandomAccessibleInterval<BitType> mask;
	
	@Parameter
	private OpService ops;
	
	@Parameter
	private DisplayService displayService;
	
	@Override
	public void run() {
		
		Img<DoubleType> result = ops.create().img(data0);
		Img<IntType> resultConverted = ops.convert().int32(result);
		
		Img<IntType> data0Converted = ops.convert().int32(data0);
		Img<IntType> data1Converted = ops.convert().int32(data1);
		
		Cursor<IntType> data0Cursor = data0Converted.localizingCursor();
		RandomAccess<IntType> data1RA = data1Converted.randomAccess();
		RandomAccess<IntType> resultRA = resultConverted.randomAccess();
		
		RandomAccess<BitType> maskRA = null;
		if (mask != null) {
			maskRA = mask.randomAccess();
		}
		
		while(data0Cursor.hasNext()) {
			
			data0Cursor.fwd();
			data1RA.setPosition(data0Cursor);
			resultRA.setPosition(data0Cursor);
			
			if (maskRA != null) {
				maskRA.setPosition(data0Cursor);
				if (!maskRA.get().get()) continue;
			}
			
			
			int val0 = data0Cursor.get().get();
			int val1 = data1RA.get().get();
			
			switch (comparisonType) {
			case DELTA:
				resultRA.get().set(val0 - val1);
				break;
			case RATIO:
				resultRA.get().set(val0 / val1);
				break;
			case PERCENT_DIFFERENCE:
				resultRA.get().set((val0 - val1) * 100 / val0);
				break;
			default:
				break;
			}
			
		}
		
		Display<?> display = displayService.createDisplay(resultConverted);
	}
}

package org.vcell;

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
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;

@Plugin(type = Op.class, name = "deltaMap")
public class DeltaMap<T extends RealType<T>> extends AbstractOp {
	
	@Parameter
	private IterableInterval<T> data0;
	
	@Parameter
	private IterableInterval<T> data1;
	
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
		
		while(data0Cursor.hasNext()) {
			data0Cursor.fwd();
			data1RA.setPosition(data0Cursor);
			resultRA.setPosition(data0Cursor);
			resultRA.get().set(data0Cursor.get().get() - data1RA.get().get());
		}
		
		displayService.createDisplay(resultConverted);
	}
}

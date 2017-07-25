package org.vcell;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ops.AbstractOp;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

@Plugin(type = Op.class, name = "largestRegionSlice")
public class LargestRegionSlice<T extends RealType<T>> extends AbstractOp {
	
	@Parameter
	private RandomAccessibleInterval<BitType> data;
	
	@Parameter(type = ItemIO.OUTPUT)
	private RandomAccessibleInterval<BitType> output;
	
	@Parameter
	private OpService ops;
	
	@Override
	public void run() {
		
		int maxArea = 0;
		int zOfMaxArea = 0;
		
		for (int z = 0; z < data.dimension(2); z++) {
			
			FinalInterval interval = Intervals.createMinMax(0, 0, z, 
					data.dimension(0) - 1, data.dimension(1) - 1, z);
			RandomAccessibleInterval<BitType> croppedRAI = ops.transform().crop(data, interval);
			IterableInterval<BitType> croppedII = Views.iterable(croppedRAI);
			Cursor<BitType> cursor = croppedII.cursor();
			
			int area = 0;
			while (cursor.hasNext()) {
				if (cursor.next().get()) {
					area++;
				}
			}
			
			if (area > maxArea) {
				maxArea = area;
				zOfMaxArea = z;
			}
		}
		
		FinalInterval interval = Intervals.createMinMax(0, 0, zOfMaxArea,
				data.dimension(0) - 1, data.dimension(1) - 1, zOfMaxArea);
		
		output = ops.transform().crop(data, interval);
	}
}

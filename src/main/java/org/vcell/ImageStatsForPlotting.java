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
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Pair;
import net.imglib2.util.RealSum;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;

@Plugin(type = Op.class, name = "imageStatsForPlotting")
public class ImageStatsForPlotting<T extends RealType<T>> extends AbstractOp {
	
	public static final int MEAN = 0;
	
	@Parameter
	private int stat;
	
	@Parameter
	private RandomAccessibleInterval<T> data;
	
	@Parameter(required = false)
	private IterableInterval<BitType> mask;
	
	@Parameter
	private OpService ops;
	
	@Parameter(type = ItemIO.OUTPUT)
	private Pair<double[], double[]> result;
	
	@Override
	public void run() {
		switch (stat) {
		case MEAN:
			result = computeMean(data, mask);
			break;
		default:
			break;
		}
	}
	
	/**
	 * Computes the mean of each XY slice along the 3rd dimension
	 * TODO: Currently assumes only 3 dimensions, must handle time series of z stacks and multiple channels
	 * @param data
	 * @return Pair containing A) the 3rd dimension index, and B) the mean value of the XY slice
	 */
	private Pair<double[], double[]> computeMean(RandomAccessibleInterval<T> data, IterableInterval<BitType> mask) {
		
		double[] indices = new double[(int) data.dimension(2)];
		double[] means = new double[indices.length];
		
		for (int z = 0; z < indices.length; z++) {
			FinalInterval interval = Intervals.createMinMax(
					0, 0, z,
					data.dimension(0) - 1, data.dimension(1) - 1, z);
			
			double mean = 0.0;
			RandomAccessibleInterval<T> cropped = ops.transform().crop(data, interval);
			
			if (mask == null) {
				mean = ops.stats().mean(Views.iterable(cropped)).getRealDouble();
			} else {
				Cursor<BitType> maskCursor = mask.localizingCursor();
				RandomAccess<T> dataRA = cropped.randomAccess();
				RealSum sum = new RealSum();
				int size = 0;
				maskCursor.reset();
				while (maskCursor.hasNext()) {
					maskCursor.fwd();
					if (maskCursor.get().get()) {
						dataRA.setPosition(maskCursor);
						sum.add(dataRA.get().getRealDouble());
						size++;
					}
				}
				mean = sum.getSum() / size;
			}
			
			indices[z] = z;
			means[z] = mean;
		}
		
		return new ValuePair<double[], double[]>(indices, means);
	}
}

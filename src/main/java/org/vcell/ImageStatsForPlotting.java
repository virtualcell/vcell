package org.vcell;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import javassist.tools.rmi.ObjectImporter;
import net.imagej.ops.AbstractOp;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;

@Plugin(type = Op.class, name = "imageStatsForPlotting")
public class ImageStatsForPlotting<T extends RealType<T>> extends AbstractOp {
	
	public static final int MEAN = 0;
	
	@Parameter
	private int stat;
	
	@Parameter
	private RandomAccessibleInterval<T> data;
	
	@Parameter
	private OpService ops;
	
	@Parameter(type = ItemIO.OUTPUT)
	private Pair<double[], double[]> result;
	
	@Override
	public void run() {
		switch (stat) {
		case MEAN:
			result = computeMean(data);
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
	private Pair<double[], double[]> computeMean(RandomAccessibleInterval<T> data) {
		
		double[] indices = new double[(int) data.dimension(2)];
		double[] means = new double[indices.length];
		
		for (int i = 0; i < indices.length; i++) {
			FinalInterval interval = Intervals.createMinMax(
					0, 0, i,
					data.dimension(0) - 1, data.dimension(1) - 1, i);
			RandomAccessibleInterval<T> cropped = ops.transform().crop(data, interval);
			double mean = ops.stats().mean(Views.iterable(cropped)).getRealDouble();
			indices[i] = i;
			means[i] = mean;
		}
		
		return new ValuePair<double[], double[]>(indices, means);
	}
}

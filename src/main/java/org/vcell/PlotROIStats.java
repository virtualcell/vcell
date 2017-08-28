package org.vcell;

import java.util.HashMap;
import java.util.List;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.gui.Plot;
import net.imagej.Dataset;
import net.imagej.ops.AbstractOp;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imagej.overlay.Overlay;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Pair;

@Plugin(type = Op.class, name = "plotROIStats")
public class PlotROIStats<T extends RealType<T>> extends AbstractOp {
	
	@Parameter
	private HashMap<RandomAccessibleInterval<T>, List<Overlay>> datasetROIsMap;
	
	@Parameter
	private OpService ops;

	@Override
	public void run() {
		
		Plot plot = new ColorPlot("ROI Mean Intensity", "Time", "Mean Intensity");
		StringBuilder legendLabels = new StringBuilder();
		
		for (RandomAccessibleInterval<T> data : datasetROIsMap.keySet()) {
			
			if (data instanceof Dataset) {
				legendLabels.append(((Dataset) data).getName());
				legendLabels.append(": ");
			}
			
			List<Overlay> overlays = datasetROIsMap.get(data);
			for (int i = 0; i < overlays.size(); i++) {
				Overlay overlay = overlays.get(i);
				RandomAccessibleInterval<T> cropped = crop(data, overlay);
				Pair<double[], double[]> xyPair = (Pair<double[], double[]>) ops.run(
						"imageStatsForPlotting", 
						ImageStatsForPlotting.MEAN,
						cropped);
				
				plot.addPoints(xyPair.getA(), xyPair.getB(), Plot.LINE);
				
				legendLabels.append("ROI ");
				legendLabels.append(i + 1);
				legendLabels.append("\n");
			}
		}
		
		plot.addLegend(legendLabels.toString());
		plot.show();
	}
	
	private RandomAccessibleInterval<T> crop(RandomAccessibleInterval<T> data, Overlay overlay) {
		
		int numDimensions = data.numDimensions();
		
		long[] minDimensions = new long[numDimensions];
		long[] maxDimensions = new long[numDimensions];
		
		if (numDimensions > 0) {
			minDimensions[0] = (long) overlay.realMin(0);
			maxDimensions[0] = (long) overlay.realMax(0);
		}
		
		if (numDimensions > 1) {
			minDimensions[1] = (long) overlay.realMin(0);
			maxDimensions[1] = (long) overlay.realMax(0);
		}
		
		for (int i = 2; i < numDimensions; i++) {
			minDimensions[i] = 0;
			maxDimensions[i] = data.dimension(i) - 1;
		}
		
		FinalInterval interval = new FinalInterval(minDimensions, maxDimensions);
		RandomAccessibleInterval<T> cropped = ops.transform().crop(data, interval);
		return cropped;
	}
	

}

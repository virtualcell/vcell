package org.vcell.imagej.app;

import java.util.List;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.vcell.imagej.common.gui.ColorPlot;
import org.vcell.imagej.common.ops.ImageStatsForPlotting;

import ij.gui.Plot;
import net.imagej.Dataset;
import net.imagej.ops.AbstractOp;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Pair;


@Plugin(type = Op.class, name = "plotImageStats")
public class PlotImageStats<T extends RealType<T>> extends AbstractOp {
	
	@Parameter
	private List<RandomAccessibleInterval<T>> datasets;
	
	@Parameter(required = false)
	private IterableInterval<T> mask;
	
	@Parameter
	private OpService ops;
	
	@Override
	public void run() {
		
		Plot plot = new ColorPlot("Frame mean intensity", "Time", "Mean intensity");
		StringBuilder legendLabels = new StringBuilder();
		
		for (int i = 0; i < datasets.size(); i++) {
			
			RandomAccessibleInterval<T> data = datasets.get(i);
			
			if (data instanceof Dataset) {
				legendLabels.append(((Dataset) data).getName());
				legendLabels.append(": ");
			}
			
			Pair<double[], double[]> xyPair = (Pair<double[], double[]>) ops.run(
					"imageStatsForPlotting", 
					ImageStatsForPlotting.MEAN, 
					data,
					mask);
			 
			plot.addPoints(xyPair.getA(), xyPair.getB(), Plot.LINE);
			
			legendLabels.append("ROI ");
			legendLabels.append(i + 1);
			legendLabels.append("\n");
		}
		
		plot.addLegend(legendLabels.toString());
		plot.show();
	}
}

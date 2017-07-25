package org.vcell;

import java.util.List;

import org.scijava.display.DisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.gui.Plot;
import net.imagej.Dataset;
import net.imagej.ops.AbstractOp;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

@Plugin(type = Op.class, name = "plotCellRegionStats")
public class PlotCellRegionStats<T extends RealType<T>> extends AbstractOp {
	
	@Parameter
	private List<RandomAccessibleInterval<T>> datasets;
	
	@Parameter
	private IterableInterval<T> mask;
	
	@Parameter
	private OpService ops;
	
	@Parameter
	private DisplayService displayService;
	
	@Override
	public void run() {
		
		Plot plot = new ColorPlot("Cell region mean intensity", "Time", "Mean intensity");
		StringBuilder legendLabels = new StringBuilder();
		
		for (int i = 0; i < datasets.size(); i++) {
			
			RandomAccessibleInterval<T> data = datasets.get(i);
			
			if (data instanceof Dataset) {
				legendLabels.append(((Dataset) data).getName());
				legendLabels.append(": ");
			}
			
			RandomAccessibleInterval<T> cropped = ops.transform().crop(data, mask);
			
			displayService.createDisplay(cropped);
		}
		
	}
}

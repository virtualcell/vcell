package org.vcell;

import java.util.HashMap;
import java.util.List;

import org.scijava.Context;

import net.imagej.Dataset;
import net.imagej.display.OverlayService;
import net.imagej.ops.OpService;
import net.imagej.overlay.Overlay;

public class CompareController {
	
	private OverlayService overlayService;
	private OpService opService;
	
	public CompareController(CompareView view, Context context) {
		this.overlayService = context.getService(OverlayService.class);
		this.opService = context.getService(OpService.class);
		addActionListenersToView(view);
	}
	
	private void addActionListenersToView(CompareView view) {
		
		view.addPlotEntireFrameListener(event -> {
			List<Dataset> datasets = view.getDatasets();
			opService.run("plotImageStats", datasets);
		});
		
		view.addPlotROIListener(event -> {
			HashMap<Dataset, List<Overlay>> datasetOverlayMap = view.getDatasetOverlayMap(overlayService);
			opService.run("plotROIStats", datasetOverlayMap);
		});
	}
}

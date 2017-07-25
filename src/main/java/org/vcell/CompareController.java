package org.vcell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import org.scijava.Context;

import ij.plugin.frame.RoiManager;
import net.imagej.Dataset;
import net.imagej.display.ImageDisplay;
import net.imagej.display.OverlayService;
import net.imagej.ops.OpService;
import net.imagej.overlay.AbstractOverlay;
import net.imagej.overlay.AbstractROIOverlay;
import net.imagej.overlay.Overlay;
import net.imagej.overlay.PolygonOverlay;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.logic.BitType;

public class CompareController {
	
	private OverlayService overlayService;
	private OpService opService;
	
	public CompareController(CompareView view, MainModel model, Context context) {
		this.overlayService = context.getService(OverlayService.class);
		this.opService = context.getService(OpService.class);
		addActionListenersToView(view, model, context);
	}
	
	private void addActionListenersToView(CompareView view, MainModel model, Context context) {
		
		view.addPlotEntireFrameListener(event -> {
			List<Dataset> datasets = view.getDatasets();
			opService.run("plotImageStats", datasets);
		});
		
		view.addPlotCellRegionListener(event -> {
			DatasetSelectionPanel panel = new DatasetSelectionPanel();
			List<Dataset> geometryList = model.getProject().getGeometry();
			final String description = "Geometry: ";
			panel.addComboBox(geometryList.toArray(new Dataset[geometryList.size()]), description);
			
			int returnVal = JOptionPane.showConfirmDialog(
					view, 
					panel,
					"Select cell geometry",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);
			
			if (returnVal == JOptionPane.OK_OPTION) {
				Dataset geometry = panel.getSelectedDatasetForDescription(description);
				
				RandomAccessibleInterval<BitType> mask = 
						(RandomAccessibleInterval<BitType>) opService.run("largestRegionSlice", geometry);
				
				opService.run("plotImageStats", view.getDatasets(), mask);
			}
		});
		
		view.addPlotROIListener(event -> {
			HashMap<Dataset, List<Overlay>> datasetOverlayMap = view.getDatasetOverlayMap(overlayService);
			opService.run("plotROIStats", datasetOverlayMap);
		});
		
		view.addDeltaMapListener(event -> {
			List<Dataset> datasets = view.getDatasets();
			if (datasets.size() != 2) {
				System.err.println("Cannot construct delta map with number of images not equal to 2");
				return;
			}
			
			opService.run("deltaMap", datasets.get(0), datasets.get(1));
		});
	}
}

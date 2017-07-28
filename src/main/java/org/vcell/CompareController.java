package org.vcell;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import org.scijava.Context;

import net.imagej.Dataset;
import net.imagej.display.OverlayService;
import net.imagej.ops.OpService;
import net.imagej.overlay.Overlay;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.logic.BitType;

public class CompareController {
	
	private MainModel model;
	private CompareView view;
	private OverlayService overlayService;
	private OpService opService;
	
	public CompareController(CompareView view, MainModel model, Context context) {
		this.model = model;
		this.view = view;
		this.overlayService = context.getService(OverlayService.class);
		this.opService = context.getService(OpService.class);
		addActionListenersToView();
	}
	
	private void addActionListenersToView() {
		
		view.addPlotEntireFrameListener(event -> {
			List<Dataset> datasets = view.getDatasets();
			opService.run("plotImageStats", datasets);
		});
		
		view.addPlotCellRegionListener(event -> {
			RandomAccessibleInterval<BitType> mask = getMaskFromGeometry();
			if (mask == null) return;
			opService.run("plotImageStats", view.getDatasets(), mask);
		});
		
		view.addPlotROIListener(event -> {
			if (!isROIPresent()) {
				showNoROIPresentDialog();
				return;
			}
			HashMap<Dataset, List<Overlay>> datasetOverlayMap = view.getDatasetOverlayMap(overlayService);
			opService.run("plotROIStats", datasetOverlayMap);
		});
		
		view.addDeltaEntireFrameListener(event -> {
			compareMap(CompareMap.DELTA, null);
		});
		
		view.addDeltaCellRegionListener(event -> {
			compareMapCellRegion(CompareMap.DELTA);
		});
		
		view.addRatioEntireFrameListener(event -> {
			compareMap(CompareMap.RATIO, null);
		});
		
		view.addRatioCellRegionListener(event -> {
			compareMapCellRegion(CompareMap.RATIO);
		});
		
		view.addPercentDifferenceEntireFrameListener(event -> {
			compareMap(CompareMap.PERCENT_DIFFERENCE, null);
		});
		
		view.addPercentDifferenceCellRegionListener(event -> {
			compareMapCellRegion(CompareMap.PERCENT_DIFFERENCE);
		});
	}
	
	private boolean isROIPresent() {
		Collection<List<Overlay>> overlayLists = view.getDatasetOverlayMap(overlayService).values();
		for (List<Overlay> overlayList : overlayLists) {
			if (!overlayList.isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	private void compareMapCellRegion(int comparisonType) {
		RandomAccessibleInterval<BitType> mask = getMaskFromGeometry();
		if (mask == null) return;
		compareMap(comparisonType, mask);
	}
	
	private void compareMap(int comparisonType, RandomAccessibleInterval<BitType> mask) {
		List<Dataset> datasets = view.getDatasets();
		
		if (datasets.size() != 2) {
			System.err.println("Cannot construct comparison map with number of images not equal to 2");
			return;
		}
		
		if (comparisonType == CompareMap.PERCENT_DIFFERENCE || comparisonType == CompareMap.RATIO) {
			Dataset secondDataset = datasets.get(1);
			if ((boolean) opService.run("intervalContains", secondDataset, 0, mask)) {
				showContainsZeroDialog();
				return;
			}
		}
		
		opService.run("compareMap", datasets.get(0), datasets.get(1), comparisonType, mask);
	}
	
	private RandomAccessibleInterval<BitType> getMaskFromGeometry() {
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
			@SuppressWarnings("unchecked")
			RandomAccessibleInterval<BitType> mask = 
					(RandomAccessibleInterval<BitType>) opService.run("largestRegionSlice", geometry);
			return mask;
			
		}
		return null;
	}
	
	private void showContainsZeroDialog() {
		JOptionPane.showMessageDialog(
				view, 
				"The selected map could not be constructed because the second dataset\n"
				+ "contains a zero value and this would require division by zero." ,
				"Image with zero value", 
				JOptionPane.PLAIN_MESSAGE);
	}
	
	private void showNoROIPresentDialog() {
		JOptionPane.showMessageDialog(
				view, 
				"No ROIs are present. Please draw an ROI.", 
				"No ROIs present", 
				JOptionPane.PLAIN_MESSAGE);
	}
}

package org.vcell;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.scijava.ui.swing.viewer.SwingDisplayWindow;
import org.scijava.ui.viewer.DisplayPanel;

import net.imagej.Dataset;
import net.imagej.display.ImageDisplay;
import net.imagej.display.OverlayService;
import net.imagej.overlay.Overlay;
import net.imagej.ui.swing.viewer.image.SwingImageDisplayPanel;

@SuppressWarnings("serial")
public class CompareView extends SwingDisplayWindow {
	
	private List<Dataset> datasets;
	private ArrayList<JPanel> panels;
	private HashMap<Dataset, ImageDisplay> datasetImagePanelMap;
	
	// Menu items under "Plot"
	private JMenuItem mniPlotEntireFrame;
	private JMenuItem mniPlotCellRegion;
	private JMenuItem mniPlotMeanROI;
	
	// Menu items under "Compare"
	private JMenuItem mniDeltaMap;
	private JMenuItem mniRatioMap;
	private JMenuItem mniPercentDifferenceMap;
	
	public CompareView(List<Dataset> datasets) {
		this.datasets = datasets;
		panels = new ArrayList<>();
		datasetImagePanelMap = new HashMap<>();
		initializeContentPane(datasets.size());
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public List<Dataset> getDatasets() {
		return datasets;
	}
	
	public List<ImageDisplay> getImageDisplays() {
		return datasetImagePanelMap.values().stream().collect(Collectors.toList());
	}
	
	public HashMap<Dataset, List<Overlay>> getDatasetOverlayMap(OverlayService overlayService) {
		HashMap<Dataset, List<Overlay>> result = new HashMap<>();
		for (Dataset dataset : datasetImagePanelMap.keySet()) {
			ImageDisplay display = datasetImagePanelMap.get(dataset);
			
			// Bug in ImageJ that returns two of each overlay, so must refrain from adding duplicates
			List<Overlay> overlays = new ArrayList<>();
			for (Overlay overlay : overlayService.getOverlays(display)) {
				if (!overlays.contains(overlay)) {
					overlays.add(overlay);
				}
			}
			result.put(dataset, overlays);
		}
		return result;
	}
	
	@Override
	public void setContent(DisplayPanel displayPanel) {
		for (int i = 0; i < panels.size(); i++) {
			if (i >= datasets.size()) break;
			JPanel panel = panels.get(i);
			if (panel.getComponentCount() == 0) {
				SwingImageDisplayPanel imagePanel = (SwingImageDisplayPanel) displayPanel;
				datasetImagePanelMap.put(datasets.get(i), imagePanel.getDisplay());
				panel.add(imagePanel, BorderLayout.CENTER);
				break;
			}
		}
	}
	
	private void initializeContentPane(int numDatasets) {
		
		final JPanel contentPane = new JPanel(new BorderLayout());
		
		// Top buttons initialization
		final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		// Plot button
		final DropDownButton btnPlot = new DropDownButton("Plot");
		btnPlot.setAlignmentX(LEFT_ALIGNMENT);
		JMenu mnuPlotMean = new JMenu("Mean vs. Time");
		mniPlotEntireFrame = new JMenuItem("Entire Frame");
		mniPlotCellRegion = new JMenuItem("Cell Region");
		mniPlotMeanROI = new JMenuItem("ROI");
		mnuPlotMean.add(mniPlotEntireFrame);
		mnuPlotMean.add(mniPlotCellRegion);
		mnuPlotMean.add(mniPlotMeanROI);
		btnPlot.getMenu().add(mnuPlotMean);
		
		// Compare button
		final DropDownButton btnCompare = new DropDownButton("Compare");
		btnCompare.setAlignmentX(LEFT_ALIGNMENT);
		mniDeltaMap = new JMenuItem("Delta Map");
		mniRatioMap = new JMenuItem("Ratio Map");
		mniPercentDifferenceMap = new JMenuItem("Percent Difference Map");
		
		if (datasets.size() != 2) {
			mniDeltaMap.setEnabled(false);
			mniRatioMap.setEnabled(false);
			mniPercentDifferenceMap.setEnabled(false);
		}
		
		JPopupMenu mnuCompare = btnCompare.getMenu();
		mnuCompare.add(mniDeltaMap);
		mnuCompare.add(mniRatioMap);
		mnuCompare.add(mniPercentDifferenceMap);
		
		topPanel.add(btnPlot);
		topPanel.add(btnCompare);
		
		// Multiple image display initialization
		final JPanel centerPanel = new JPanel(new GridLayout(0, 2));
		
		for (int i = 0; i < numDatasets; i++) {
			JPanel panel = new JPanel(new BorderLayout());
			panel.setPreferredSize(new Dimension(400, 400));
			panels.add(panel);
			centerPanel.add(panel);
		}
		
		contentPane.add(topPanel, BorderLayout.PAGE_START);
		contentPane.add(centerPanel, BorderLayout.CENTER);
		setContentPane(contentPane);
	}
	
	public void addPlotEntireFrameListener(ActionListener l) {
		mniPlotEntireFrame.addActionListener(l);
	}
	
	public void addPlotCellRegionListener(ActionListener l) {
		mniPlotCellRegion.addActionListener(l);
	}
	
	public void addPlotROIListener(ActionListener l) {
		mniPlotMeanROI.addActionListener(l);
	}
	
	public void addDeltaMapListener(ActionListener l) {
		mniDeltaMap.addActionListener(l);
	}
	
	public void addRatioMapListener(ActionListener l) {
		mniRatioMap.addActionListener(l);
	}
	
	public void addPercentDifferenceMapListener(ActionListener l) {
		mniPercentDifferenceMap.addActionListener(l);
	}
}

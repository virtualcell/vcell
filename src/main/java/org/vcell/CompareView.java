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

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
	private List<JPanel> panels;
	private List<JLabel> labels;
	private JCheckBox scrollMirrorCheckBox;
	private HashMap<Dataset, ImageDisplay> datasetImagePanelMap;
	
	// Menu items under "Plot"
	private JMenuItem mniPlotEntireFrame;
	private JMenuItem mniPlotCellRegion;
	private JMenuItem mniPlotMeanROI;
	
	// Menu items under "Compare"
	private JMenuItem mniDeltaEntireFrame;
	private JMenuItem mniDeltaCellRegion;
	
	private JMenuItem mniRatioEntireFrame;
	private JMenuItem mniRatioCellRegion;
	
	private JMenuItem mniPercentDifferenceEntireFrame;
	private JMenuItem mniPercentDifferenceCellRegion;
	
	public CompareView(List<Dataset> datasets) {
		this.datasets = datasets;
		panels = new ArrayList<>();
		labels = new ArrayList<>();
		datasetImagePanelMap = new HashMap<>();
		initializeContentPane();
		pack();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
				Dataset dataset = datasets.get(i);
				datasetImagePanelMap.put(dataset, imagePanel.getDisplay());
				panel.add(imagePanel, BorderLayout.CENTER);
				labels.get(i).setText(dataset.getName());
				break;
			}
		}
	}
	
	private void initializeContentPane() {
		
		final JPanel contentPane = new JPanel(new BorderLayout());
		
		// Top buttons initialization
		final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		// Plot button
		final DropDownButton btnPlot = new DropDownButton("Plot");
		btnPlot.setAlignmentX(LEFT_ALIGNMENT);
		JMenu mnuPlotMean = new JMenu("Mean vs. time");
		mniPlotEntireFrame = new JMenuItem("Entire frame");
		mniPlotCellRegion = new JMenuItem("Cell region");
		mniPlotMeanROI = new JMenuItem("ROI");
		mnuPlotMean.add(mniPlotEntireFrame);
		mnuPlotMean.add(mniPlotCellRegion);
		mnuPlotMean.add(mniPlotMeanROI);
		btnPlot.getMenu().add(mnuPlotMean);
		
		// Compare button
		final DropDownButton btnCompare = new DropDownButton("Compare");
		btnCompare.setAlignmentX(LEFT_ALIGNMENT);
		
		JMenu mnuDeltaMap = new JMenu("Delta map");
		JMenu mnuRatioMap = new JMenu("Ratio map");
		JMenu mnuPercentDifferenceMap = new JMenu("Percent difference map");
		
		mniDeltaEntireFrame = new JMenuItem("Entire frame");
		mniDeltaCellRegion = new JMenuItem("Cell region");
		
		mniRatioEntireFrame = new JMenuItem("Entire frame");
		mniRatioCellRegion = new JMenuItem("Cell region");
		
		mniPercentDifferenceEntireFrame = new JMenuItem("Entire frame");
		mniPercentDifferenceCellRegion = new JMenuItem("Cell region");
		
		mnuDeltaMap.add(mniDeltaEntireFrame);
		mnuDeltaMap.add(mniDeltaCellRegion);
		
		mnuRatioMap.add(mniRatioEntireFrame);
		mnuRatioMap.add(mniRatioCellRegion);
		
		mnuPercentDifferenceMap.add(mniPercentDifferenceEntireFrame);
		mnuPercentDifferenceMap.add(mniPercentDifferenceCellRegion);
		
		if (datasets.size() != 2) {
			btnCompare.setEnabled(false);
		}
		
		JPopupMenu mnuCompare = btnCompare.getMenu();
		mnuCompare.add(mnuDeltaMap);
		mnuCompare.add(mnuRatioMap);
		mnuCompare.add(mnuPercentDifferenceMap);
		
		topPanel.add(btnPlot);
		topPanel.add(btnCompare);
		
		// Multiple image display initialization
		final JPanel centerPanel = new JPanel(new GridLayout(0, 2));
		
		for (int i = 0; i < datasets.size(); i++) {
			JLabel label = new JLabel("label");
			JPanel panel = new JPanel(new BorderLayout());
			panel.setPreferredSize(new Dimension(400, 400));
//			panel.add(label);
			labels.add(label);
			panels.add(panel);
			centerPanel.add(panel);
		}
		
		scrollMirrorCheckBox = new JCheckBox("Scroll mirror");
		
		contentPane.add(topPanel, BorderLayout.PAGE_START);
		contentPane.add(centerPanel, BorderLayout.CENTER);
//		contentPane.add(scrollMirrorCheckBox, BorderLayout.PAGE_END);
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
	
	public void addDeltaEntireFrameListener(ActionListener l) {
		mniDeltaEntireFrame.addActionListener(l);
	}
	
	public void addDeltaCellRegionListener(ActionListener l) {
		mniDeltaCellRegion.addActionListener(l);
	}
	
	public void addRatioEntireFrameListener(ActionListener l) {
		mniRatioEntireFrame.addActionListener(l);
	}
	
	public void addRatioCellRegionListener(ActionListener l) {
		mniRatioCellRegion.addActionListener(l);
	}
	
	public void addPercentDifferenceEntireFrameListener(ActionListener l) {
		mniPercentDifferenceEntireFrame.addActionListener(l);
	}
	
	public void addPercentDifferenceCellRegionListener(ActionListener l) {
		mniPercentDifferenceCellRegion.addActionListener(l);
	}
}

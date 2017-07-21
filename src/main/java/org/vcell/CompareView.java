package org.vcell;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

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
	private JMenuItem mniPlotMeanROI;
	
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
	
	public HashMap<Dataset, List<Overlay>> getDatasetOverlayMap(OverlayService overlayService) {
		HashMap<Dataset, List<Overlay>> result = new HashMap<>();
		for (Dataset dataset : datasetImagePanelMap.keySet()) {
			ImageDisplay display = datasetImagePanelMap.get(dataset);
			
			// Bug in ImageJ that returns two of each overlay, so must remove duplicates
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
		final DropDownButton btnPlot = new DropDownButton("Plot");
		btnPlot.setAlignmentX(LEFT_ALIGNMENT);
		JMenu mnuPlotMean = new JMenu("Mean vs. Time");
		mniPlotEntireFrame = new JMenuItem("Entire Frame");
		mniPlotMeanROI = new JMenuItem("ROI");
		mnuPlotMean.add(mniPlotEntireFrame);
		mnuPlotMean.add(mniPlotMeanROI);
		btnPlot.getMenu().add(mnuPlotMean);
		topPanel.add(btnPlot);
		
		// Multi image display initialization
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
	
	public void addPlotROIListener(ActionListener l) {
		mniPlotMeanROI.addActionListener(l);
	}
}

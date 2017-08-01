package org.vcell;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import org.vcell.vcellij.api.SimulationState;

public class ModelSummaryPanel extends JPanel {
	
	private static final long serialVersionUID = 6730669723729310093L;
	
	private final int headerHeight = 50;

	public ModelSummaryPanel(MainModel model, VCellModel vCellModel) {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setPreferredSize(new Dimension(-1, headerHeight));
		headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		
		JLabel headerLabel = new JLabel(vCellModel.getName());
		Font font = headerLabel.getFont();
		headerLabel.setFont(new Font(font.getFontName(), Font.BOLD, 14));
		
		BufferedImage image = vCellModel.getImage();
		if (image != null) {
			double widthHeightRatio = image.getWidth() / (double) image.getHeight();
			int width = (int) (headerHeight * widthHeightRatio) + 1;
			Image scaledImage = image.getScaledInstance(-1, headerHeight, Image.SCALE_FAST);
	    	JButton button = new JButton(new ImageIcon(scaledImage));
	    	button.setPreferredSize(new Dimension(width, headerHeight));
	    	button.addActionListener(e -> {
	    		displayImageInNewWindow(image);
	    	});
	    	headerPanel.add(button, BorderLayout.LINE_END);
		}
    	
    	headerPanel.add(headerLabel, BorderLayout.CENTER);
    	
    	ModelParameterListPanel parameterListPanel = new ModelParameterListPanel();
    	parameterListPanel.setModel(vCellModel);
    	parameterListPanel.setPreferredSize(new Dimension(getWidth(), -1));
    	
    	JPanel statusPanel = new JPanel(new GridLayout(1, 2));
    	statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
    	
    	updateSimulationState(statusPanel, vCellModel.getSimulationState());
    	
    	add(headerPanel, BorderLayout.PAGE_START);
    	add(new JScrollPane(parameterListPanel), BorderLayout.CENTER);
    	add(statusPanel, BorderLayout.PAGE_END);
    	
    	model.addDataChangeListener(e -> {
    		updateSimulationState(statusPanel, vCellModel.getSimulationState());
    	});
	}
	
	private void displayImageInNewWindow(BufferedImage image) {
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		
		JFrame frame = new JFrame();
		frame.setContentPane(panel);
		frame.pack();
		
		JLabel label = new JLabel();
		label.setIcon(new ImageIcon(image));
		panel.add(label, BorderLayout.CENTER);
		
		frame.setVisible(true);
	}
	
	private void updateSimulationState(JPanel statusPanel, SimulationState simState) {
		statusPanel.removeAll();
		Component simulationStateComponent = getSimulationStateComponent(simState);
    	statusPanel.add(new JLabel("Simulation status:"));
    	statusPanel.add(simulationStateComponent);
		revalidate();
		repaint();
	}
	
	private Component getSimulationStateComponent(SimulationState simState) {
		switch (simState) {
		case notRun:
			return new JLabel("Not run");
		case running:
			JPanel panel = new JPanel(new GridLayout());
			JProgressBar bar = new JProgressBar();
			bar.setIndeterminate(true);
			bar.setStringPainted(true);
			bar.setString("Running");
			panel.add(new JLabel("Running"));
			panel.add(bar);
			return panel;
		case done:
			return new JLabel("Done");
		case failed:
			return new JLabel("Failed");
		default:
			return null;
		}
	}
}

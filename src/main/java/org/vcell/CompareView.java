package org.vcell;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

import org.scijava.ui.swing.viewer.SwingDisplayWindow;
import org.scijava.ui.viewer.DisplayPanel;

import net.imagej.ui.swing.viewer.image.SwingImageDisplayPanel;

public class CompareView extends SwingDisplayWindow {
	
	private JPanel leftPanel;
	private JPanel rightPanel;
	private SwingImageDisplayPanel leftImagePanel;
	private SwingImageDisplayPanel rightImagePanel;
	
	public CompareView() {
		initializeContentPane();
	}
	
	@Override
	public void setContent(DisplayPanel panel) {
		if (leftPanel.getComponentCount() == 0) {
			leftImagePanel = (SwingImageDisplayPanel) panel;
			leftPanel.add(leftImagePanel, BorderLayout.CENTER);
		} else {
			rightImagePanel = (SwingImageDisplayPanel) panel;
			rightPanel.add(rightImagePanel, BorderLayout.CENTER);
		}
	}
	
	private void initializeContentPane() {
		
		JPanel contentPane = new JPanel(new GridLayout(1, 2));
		
		leftPanel = new JPanel(new BorderLayout());
		leftPanel.setPreferredSize(new Dimension(200, 200));
		rightPanel = new JPanel(new BorderLayout());
		rightPanel.setPreferredSize(new Dimension(200, 200));
		
		contentPane.add(leftPanel);
		contentPane.add(rightPanel);
		
		setContentPane(contentPane);
	}
}

package org.vcell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ModelSummaryPanel extends JPanel {
	
	private static final long serialVersionUID = 6730669723729310093L;
	
	private final int headerHeight = 50;

	public ModelSummaryPanel(VCellModel model) {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setPreferredSize(new Dimension(-1, headerHeight));
		headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		
		JLabel headerLabel = new JLabel(model.getName());
		Font font = headerLabel.getFont();
		headerLabel.setFont(new Font(font.getFontName(), Font.BOLD, 14));
		
		BufferedImage image = model.getImage();
		double widthHeightRatio = image.getWidth() / (double) image.getHeight();
		int width = (int) (headerHeight * widthHeightRatio) + 1;
		Image scaledImage = image.getScaledInstance(-1, headerHeight, Image.SCALE_FAST);
    	JButton button = new JButton(new ImageIcon(scaledImage));
    	button.setPreferredSize(new Dimension(width, headerHeight));
    	
    	headerPanel.add(headerLabel, BorderLayout.CENTER);
    	headerPanel.add(button, BorderLayout.LINE_END);
    	
    	ModelParameterListPanel parameterListPanel = new ModelParameterListPanel();
    	parameterListPanel.setParameters(model.getParameters());
    	
    	JPanel statusPanel = new JPanel(new BorderLayout());
    	statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
    	JLabel statusLabel = new JLabel("Not run");
    	statusPanel.add(new JLabel("Simulation status:"), BorderLayout.LINE_START);
    	statusPanel.add(statusLabel, BorderLayout.LINE_END);
    	
    	add(headerPanel, BorderLayout.PAGE_START);
    	add(new JScrollPane(parameterListPanel), BorderLayout.CENTER);
    	add(statusPanel, BorderLayout.PAGE_END);
	}
}

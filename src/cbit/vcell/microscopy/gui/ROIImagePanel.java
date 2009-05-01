package cbit.vcell.microscopy.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.vcell.util.ISize;

import cbit.vcell.microscopy.ROI;
import cbit.vcell.microscopy.ROI.RoiType;

public class ROIImagePanel extends JPanel {
	
	private final JLabel roiImageJLabel;
	private final JPanel roiLabelsJPanel;
	private final JCheckBox bleachToggle = new JCheckBox("Show Bleach ROI");
	private ImageIcon roiImageIcon;
	private ImageIcon bleachImageIcon;
	
	public ROIImagePanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0,7};
		gridBagLayout.rowHeights = new int[] {7};
		setLayout(gridBagLayout);

		roiImageJLabel = new JLabel();
		roiImageJLabel.setText("New JLabel");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.weighty = 0;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		add(roiImageJLabel, gridBagConstraints);

		roiLabelsJPanel = new JPanel();
		final GridLayout gridLayout = new GridLayout(0, 1);
		gridLayout.setVgap(5);
		roiLabelsJPanel.setLayout(gridLayout);
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(0, 20, 0, 0);
		gridBagConstraints_1.gridx = 1;
		gridBagConstraints_1.gridy = 0;
		add(roiLabelsJPanel, gridBagConstraints_1);
	}

	public void init(ROI[] analysisROIs,Color[] rinROIColors,ROI cellROI,Color cellROIColor,ROI bleachROI,Color bleachROIColor){
		ISize roiISize = analysisROIs[0].getISize();
		final int ROISIZE_XYZ = roiISize.getXYZ();
		BufferedImage roiImage = new BufferedImage(roiISize.getX(),roiISize.getY(),BufferedImage.TYPE_INT_RGB);
		BufferedImage bleachImage = new BufferedImage(roiISize.getX(),roiISize.getY(),BufferedImage.TYPE_INT_RGB);
		
		roiLabelsJPanel.removeAll();

//		bleachToggle.setForeground(bleachROIColor);
		bleachToggle.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				showBleachROI();
			}
		});
		roiLabelsJPanel.add(bleachToggle);

//		JLabel roiLabel = new JLabel("Cell Outline"/*RoiType.ROI_CELL.name()*/);
//		roiLabel.setIcon(createColorIcon(cellROIColor));
//		roiLabelsJPanel.add(roiLabel);
		
		//fill ROIs
		for (int i = 0; i < analysisROIs.length; i++) {
			JLabel roiLabel = new JLabel(analysisROIs[i].getROIType().name());
			roiLabel.setIcon(createColorIcon(rinROIColors[i]));
			roiLabelsJPanel.add(roiLabel);
			short[] roiPixels = analysisROIs[i].getPixelsXYZ();
			for (int j = 0; j < ROISIZE_XYZ; j++) {
				if(roiPixels[j] != 0){
					roiImage.setRGB(j%roiISize.getX(), j/roiISize.getX(), rinROIColors[i].getRGB());
				}
			}
		}
		//outline cell
		short[] cellROIPixels = cellROI.getPixelsXYZ();
		for (int y = 0; y < roiISize.getY(); y++) {
			int yindex = y*roiISize.getX();
			for (int x = 0; x < roiISize.getX(); x++) {
				if(cellROIPixels[yindex+x] != 0){
					boolean bEdge = false;
					if(x > 0){if(cellROIPixels[yindex+x-1] == 0){bEdge = true;}}
					if(x < (roiISize.getX()-1)){if(cellROIPixels[yindex+x+1] == 0){bEdge = true;}}
					if(y > 0){if(cellROIPixels[yindex+x-roiISize.getX()] == 0){bEdge = true;}}
					if(y < (roiISize.getY()-1)){if(cellROIPixels[yindex+x+roiISize.getX()] == 0){bEdge = true;}}
					if(bEdge){
						roiImage.setRGB(x,y, cellROIColor.getRGB());
						bleachImage.setRGB(x,y, cellROIColor.getRGB());
					}
				}
			}
		}
//		//outline bleach
//		short[] bleachROIPixels = bleachROI.getPixelsXYZ();
//		for (int y = 0; y < roiISize.getY(); y++) {
//			int yindex = y*roiISize.getX();
//			for (int x = 0; x < roiISize.getX(); x++) {
//				if(bleachROIPixels[yindex+x] != 0){
//					boolean bEdge = false;
//					if(x > 0){if(bleachROIPixels[yindex+x-1] == 0){bEdge = true;}}
//					if(x < (roiISize.getX()-1)){if(bleachROIPixels[yindex+x+1] == 0){bEdge = true;}}
//					if(y > 0){if(bleachROIPixels[yindex+x-roiISize.getX()] == 0){bEdge = true;}}
//					if(y < (roiISize.getY()-1)){if(bleachROIPixels[yindex+x+roiISize.getX()] == 0){bEdge = true;}}
//					if(bEdge){roiImage.setRGB(x,y, bleachROIColor.getRGB());}
//				}
//			}
//		}
		
		short[] bleachROIPixels = bleachROI.getPixelsXYZ();
		for (int y = 0; y < roiISize.getY(); y++) {
			int yindex = y*roiISize.getX();
			for (int x = 0; x < roiISize.getX(); x++) {
				if(bleachROIPixels[yindex+x] != 0){
					bleachImage.setRGB(x, y, bleachROIColor.getRGB());
				}
			}
		}
		Image scaledBleach = bleachImage;
		Image scaledImage = roiImage;
		int largestDimension = Math.max(roiISize.getX(),roiISize.getY());
		if(largestDimension < 512){
			double scale = 512.0/largestDimension;
			scaledImage = roiImage.getScaledInstance(
				(int)(roiISize.getX()*scale), (int)(roiISize.getY()*scale),Image.SCALE_REPLICATE);
			scaledBleach = bleachImage.getScaledInstance(
					(int)(roiISize.getX()*scale), (int)(roiISize.getY()*scale),Image.SCALE_REPLICATE);
		}

		roiImageIcon = new ImageIcon(scaledImage);
		bleachImageIcon = new ImageIcon(scaledBleach);

		roiImageJLabel.setIcon(roiImageIcon);
		roiImageJLabel.setText(null);
		
	}
	
	private ImageIcon createColorIcon(Color iconColor){
		final int COLORSPOT_WIDTH = 24;
		final int COLORSPOT_HEIGHT = 24;
		final int XYSIZE = COLORSPOT_WIDTH*COLORSPOT_HEIGHT;
		BufferedImage colorSpot = new BufferedImage(COLORSPOT_WIDTH,COLORSPOT_HEIGHT,BufferedImage.TYPE_INT_RGB);
		for (int j = 0; j < XYSIZE; j++) {
			colorSpot.setRGB(j%COLORSPOT_WIDTH, j/COLORSPOT_WIDTH,iconColor.getRGB());			
		}
		return new ImageIcon(colorSpot);
	}
	
	private void showBleachROI(){
		if(bleachToggle.isSelected()){
			roiImageJLabel.setIcon(bleachImageIcon);
		}else{
			roiImageJLabel.setIcon(roiImageIcon);
		}
		repaint();
	}
}

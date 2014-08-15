/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.vcell.util.BeanUtils;
import org.vcell.util.Extent;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;

import cbit.image.VCImageUncompressed;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.RegionImage.RegionInfo;
import cbit.vcell.microscopy.VFrap_ROISourceData;
import cbit.vcell.model.gui.ScopedExpressionTableCellRenderer;

@SuppressWarnings("serial")
public class VFrap_ROIAssistPanel extends JPanel {
	
	private static final int MAX_SCALE = 0x0000FFFF;
	
	private JButton fillVoidsButton;
	private JButton applyROIButton;
	private JButton resolveROIButton;
	private JComboBox spatialEnhanceComboBox;
	private JComboBox roiSourceComboBox;
	private JSlider thresholdSlider;
	private ActionListener createROISourceDataActionListener =
		new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				waitCursor(true);
				new Thread(new Runnable(){public void run(){
					try{
						createROISourceData(false);
					}catch(final Exception e2){
						waitCursor(false);
						SwingUtilities.invokeLater(new Runnable(){public void run(){//}});
							DialogUtils.showErrorDialog(VFrap_ROIAssistPanel.this, "Error creating ROI source\n"+e2.getClass().getSimpleName()+"\n"+e2.getMessage());
						}});
					}finally{
						waitCursor(false);
					}
				}}).start();
			}
		};
		
		ChangeListener processTimepointChangeListener = new ChangeListener() {
			public void stateChanged(final ChangeEvent e) {
				if(!thresholdSlider.getValueIsAdjusting()){
					waitCursor(true);
					new Thread(new Runnable(){public void run(){
						try{
							processTimepoint();
						}catch(final Exception e2){
							waitCursor(false);
							SwingUtilities.invokeLater(new Runnable(){public void run(){//}});
								DialogUtils.showErrorDialog(VFrap_ROIAssistPanel.this, "Error resolving ROI\n"+e2.getMessage());
							}});
						}finally{
							waitCursor(false);
						}
					}}).start();
				}
			}
		};
	private static final String ENHANCE_NONE = "None";
	private static final String ENHANCE_AVG_3X3 = "Avg 3x3";
	private static final String ENHANCE_AVG_5x5 = "Avg 5x5";
	private static final String ENHANCE_AVG_7x7 = "Avg 7x7";
	private static final String ENHANCE_MEDIAN_3X3 = "Median 3x3";
	private static final String ENHANCE_MEDIAN_5x5 = "Median 5x5";
	
	private VFrap_ROISourceData frapData;
	private short[] roiTimeAverageDataShort;
	private short[] lastROISourceDataShort;
	private VFrap_OverlayEditorPanelJAI overlayEditorPanelJAI;
	private RegionInfo[] lastRegionInfos;
	private int[] thresholdSliderIntensityLookup;
	
	private ROI originalROI;
	
	public VFrap_ROIAssistPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0,7};
		gridBagLayout.rowHeights = new int[] {7,0,7,0,0,7};
		setLayout(gridBagLayout);

		final JLabel roiSourceLabel = new JLabel();
		roiSourceLabel.setFont(new Font("", Font.PLAIN, 12));
		roiSourceLabel.setText("ROI Threshold Source");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(10, 4, 0, 4);
		gridBagConstraints_1.weightx = 1;
		gridBagConstraints_1.gridx = 0;
		gridBagConstraints_1.gridy = 0;
		add(roiSourceLabel, gridBagConstraints_1);

		final JLabel spatialEnahnceLabel = new JLabel();
		spatialEnahnceLabel.setFont(new Font("", Font.PLAIN, 12));
		spatialEnahnceLabel.setText("Spatial Enhance Threshold");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(10, 4, 0, 4);
		gridBagConstraints_3.weightx = 1;
		gridBagConstraints_3.gridy = 0;
		gridBagConstraints_3.gridx = 1;
		add(spatialEnahnceLabel, gridBagConstraints_3);

		roiSourceComboBox = new JComboBox();
		roiSourceComboBox.addActionListener(createROISourceDataActionListener);
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.weightx = 0;
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 0;
		add(roiSourceComboBox, gridBagConstraints);

		spatialEnhanceComboBox = new JComboBox();
		spatialEnhanceComboBox.insertItemAt(ENHANCE_NONE, 0);
		spatialEnhanceComboBox.insertItemAt(ENHANCE_AVG_3X3, 1);
		spatialEnhanceComboBox.insertItemAt(ENHANCE_AVG_5x5, 2);
		spatialEnhanceComboBox.insertItemAt(ENHANCE_AVG_7x7, 3);
		spatialEnhanceComboBox.insertItemAt(ENHANCE_MEDIAN_3X3, 4);
		spatialEnhanceComboBox.insertItemAt(ENHANCE_MEDIAN_5x5, 5);
		spatialEnhanceComboBox.setSelectedIndex(0);
		spatialEnhanceComboBox.addActionListener(createROISourceDataActionListener);
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.weightx = 0;
		gridBagConstraints_2.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.gridy = 1;
		gridBagConstraints_2.gridx = 1;
		add(spatialEnhanceComboBox, gridBagConstraints_2);

		thresholdSlider = new JSlider();
		thresholdSlider.setMaximum(MAX_SCALE);
		thresholdSlider.addChangeListener(processTimepointChangeListener);
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_5.gridwidth = 2;
		gridBagConstraints_5.gridx = 0;
		gridBagConstraints_5.gridy = 3;
		add(thresholdSlider, gridBagConstraints_5);

		final JLabel thresholdForRoiLabel = new JLabel();
		thresholdForRoiLabel.setFont(new Font("", Font.PLAIN, 12));
		thresholdForRoiLabel.setText("Threshold Adjust ROI");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.gridwidth = 2;
		gridBagConstraints_7.gridy = 2;
		gridBagConstraints_7.gridx = 0;
		add(thresholdForRoiLabel, gridBagConstraints_7);

		final JLabel lowerLabel = new JLabel();
		lowerLabel.setFont(new Font("", Font.PLAIN, 12));
		lowerLabel.setText("ROI Shrink");
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.insets = new Insets(0, 4, 0, 0);
		gridBagConstraints_8.anchor = GridBagConstraints.WEST;
		gridBagConstraints_8.gridy = 4;
		gridBagConstraints_8.gridx = 0;
		add(lowerLabel, gridBagConstraints_8);

		final JLabel higherLabel = new JLabel();
		higherLabel.setFont(new Font("", Font.PLAIN, 12));
		higherLabel.setText("ROI Grow");
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.insets = new Insets(0, 0, 0, 4);
		gridBagConstraints_9.anchor = GridBagConstraints.EAST;
		gridBagConstraints_9.gridy = 4;
		gridBagConstraints_9.gridx = 1;
		add(higherLabel, gridBagConstraints_9);

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 0));
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.insets = new Insets(20, 2, 2, 2);
		gridBagConstraints_6.gridwidth = 2;
		gridBagConstraints_6.gridy = 5;
		gridBagConstraints_6.gridx = 0;
		add(buttonPanel, gridBagConstraints_6);

		applyROIButton = new JButton();
		applyROIButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try{
					overlayEditorPanelJAI.displaySpecialData(null, 0, 0);
				}catch(Exception e2){
					e2.printStackTrace();
				}
				overlayEditorPanelJAI.getRoiAssistButton().setIcon(new ImageIcon(getClass().getResource("/images/roiAssistClose.gif")));
				VFrap_ROIAssistPanel.this.setVisible(false);
				overlayEditorPanelJAI.adjustComponentsForVFRAP(VFrap_OverlayEditorPanelJAI.HIDE_ROI_ASSISTANT);
			}
		});
		applyROIButton.setText("Apply and Hide");
		buttonPanel.add(applyROIButton);

		final JButton cancelButton = new JButton();
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try{
					overlayEditorPanelJAI.displaySpecialData(null, 0, 0);
				}catch(Exception e2){
					e2.printStackTrace();
				}
				frapData.addReplaceRoi(originalROI);
				overlayEditorPanelJAI.getRoiAssistButton().setIcon(new ImageIcon(getClass().getResource("/images/roiAssistClose.gif")));
				VFrap_ROIAssistPanel.this.setVisible(false);
				overlayEditorPanelJAI.adjustComponentsForVFRAP(VFrap_OverlayEditorPanelJAI.HIDE_ROI_ASSISTANT);
			}
		});
		cancelButton.setText("Cancel and Hide");
		buttonPanel.add(cancelButton);

		resolveROIButton = new JButton();
		resolveROIButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try{
					final RegionInfo keepRegionInfo = pickKeepRegionInfoFromCurrentROI();
					if(keepRegionInfo != null){
						waitCursor(true);
						new Thread(new Runnable(){public void run(){
							try{
								resolveCurrentROI(keepRegionInfo);
							}catch(final Exception e2){
								waitCursor(false);
								SwingUtilities.invokeLater(new Runnable(){public void run(){//}});
									DialogUtils.showErrorDialog(VFrap_ROIAssistPanel.this, "Error resolving ROI\n"+e2.getMessage());
								}});
							}finally{
								waitCursor(false);
							}
						}}).start();
					}
				}catch(Exception e2){
					DialogUtils.showErrorDialog(VFrap_ROIAssistPanel.this, "Error Resolving ROI.  "+e2.getMessage());
				}
			}
		});
		resolveROIButton.setText("Resolve...");
		buttonPanel.add(resolveROIButton);

		fillVoidsButton = new JButton();
		fillVoidsButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				waitCursor(true);
				new Thread(new Runnable(){public void run(){
					try{
						short[] filledVoidsPixels = frapData.getCurrentlyDisplayedROI().getPixelsXYZ();
						fillVoids(filledVoidsPixels, false);
						UShortImage ushortImage =
							new UShortImage(filledVoidsPixels,
									frapData.getCurrentlyDisplayedROI().getRoiImages()[0].getOrigin(),
									frapData.getCurrentlyDisplayedROI().getRoiImages()[0].getExtent(),
									frapData.getCurrentlyDisplayedROI().getISize().getX(),
									frapData.getCurrentlyDisplayedROI().getISize().getY(),
									frapData.getCurrentlyDisplayedROI().getISize().getZ());
						final ROI newCellROI = new ROI(ushortImage,frapData.getCurrentlyDisplayedROI().getROIName());
						SwingUtilities.invokeAndWait(new Runnable(){public void run(){//}});
							frapData.addReplaceRoi(newCellROI);
							applyROIButton.setEnabled(true);
							resolveROIButton.setEnabled(false);
							fillVoidsButton.setEnabled(false);
						}});
					}catch(final Exception e2){
						waitCursor(false);
						SwingUtilities.invokeLater(new Runnable(){public void run(){//}});
							DialogUtils.showErrorDialog(VFrap_ROIAssistPanel.this, "Error filling voids in ROI"+
								frapData.getCurrentlyDisplayedROI().getROIName()+"\n"+e2.getMessage());
						}});
					}finally{
						waitCursor(false);
					}
				}}).start();
			}
		});
		fillVoidsButton.setText("Fill Voids");
		buttonPanel.add(fillVoidsButton);
	}
	
	private void createScaledTimeAverageData() throws Exception{
		int numTimes = frapData.getImageDataset().getSizeT();
		long[] timeSum = new long[frapData.getImageDataset().getISize().getXYZ()];
		for (int t = 0; t < numTimes; t++) {
			int pixelIndex = 0;
			for (int z = 0; z < frapData.getImageDataset().getSizeZ(); z++) {
				UShortImage timePointDataImage = frapData.getImageDataset().getImage(z, 0, t);
				for (int y = 0; y < timePointDataImage.getNumY(); y++) {
					for (int x = 0; x < timePointDataImage.getNumX(); x++) {
						timeSum[pixelIndex]+= timePointDataImage.getPixel(x,y,0)&0x0000FFFF;
						pixelIndex++;  
					}
				}
			}
		}
		roiTimeAverageDataShort = new short[timeSum.length];
		for (int i = 0; i < timeSum.length; i++) {
			roiTimeAverageDataShort[i]|= ((int)(timeSum[i]/numTimes))&0x0000FFFF;
		}
		scaleDataInPlace(roiTimeAverageDataShort);
	}
	public static short[] collectAllZAtOneTimepointIntoOneArray(ImageDataset sourceImageDataSet,int timeIndex){
		short[] collectedPixels = new short[sourceImageDataSet.getISize().getXYZ()];
		int pixelIndex = 0;
		for (int z = 0; z < sourceImageDataSet.getSizeZ(); z++) {
			short[] slicePixels = sourceImageDataSet.getImage(z, 0, timeIndex).getPixels();
			System.arraycopy(slicePixels, 0, collectedPixels, pixelIndex, slicePixels.length);
			pixelIndex+= slicePixels.length;
		}
		return collectedPixels;
	}
	private void createROISourceData(boolean bNew) throws Exception{
		final ROI oldROI = frapData.getRoi(VFrap_ROISourceData.VFRAP_ROI_ENUM.ROI_CELL.name());
		short[] roiSourceData = null;//new short[oldROI.getISize().getXYZ()];
		if(roiSourceComboBox.getSelectedIndex() == 0){//timeAverage
			if(roiTimeAverageDataShort == null){
				createScaledTimeAverageData();
			}
			roiSourceData = roiTimeAverageDataShort.clone();
		}else{
			final int timeIndex = roiSourceComboBox.getSelectedIndex()-1;
			
			roiSourceData = collectAllZAtOneTimepointIntoOneArray(frapData.getImageDataset(), timeIndex);
		}
		scaleDataInPlace(roiSourceData);
		
		
		if(spatialEnhanceComboBox.getSelectedIndex() > 0){
			short[] enhacedBytes = new short[roiSourceData.length];
			int radius =
				(spatialEnhanceComboBox.getSelectedItem().equals(ENHANCE_AVG_3X3)?1:0)+
				(spatialEnhanceComboBox.getSelectedItem().equals(ENHANCE_AVG_5x5)?2:0)+
				(spatialEnhanceComboBox.getSelectedItem().equals(ENHANCE_AVG_7x7)?3:0)+
				(spatialEnhanceComboBox.getSelectedItem().equals(ENHANCE_MEDIAN_3X3)?1:0)+
				(spatialEnhanceComboBox.getSelectedItem().equals(ENHANCE_MEDIAN_5x5)?2:0);
			int pixelIndex = 0;
			int accum = 0;
			int convSize = (radius*2+1)*(radius*2+1);
			int[] convPixels = new int[convSize];
			for (int z = 0; z < oldROI.getISize().getZ(); z++) {
				int zOffset = z*oldROI.getISize().getX()*oldROI.getISize().getY();
				for (int y = 0; y < oldROI.getISize().getY(); y++) {
					int yoffset = y*oldROI.getISize().getX();
					for (int x = 0; x < oldROI.getISize().getX(); x++) {
						accum = 0;
						for (int xbox = -radius; xbox <= radius; xbox++) {
							for (int ybox = -radius; ybox <= radius; ybox++) {
								if(x+xbox >= 0 && x+xbox < oldROI.getISize().getX() &&
									y+ybox >= 0 && y+ybox < oldROI.getISize().getY()){
									convPixels[accum]= 0x0000FFFF&
										roiSourceData[zOffset+yoffset+x+xbox+(ybox*oldROI.getISize().getX())];
								}else{
									convPixels[accum]= 0x0000FFFF&roiSourceData[zOffset+yoffset+x];
								}
								accum++;
							}

						}
						if(spatialEnhanceComboBox.getSelectedItem().equals(ENHANCE_AVG_3X3) ||
							spatialEnhanceComboBox.getSelectedItem().equals(ENHANCE_AVG_5x5) ||
							spatialEnhanceComboBox.getSelectedItem().equals(ENHANCE_AVG_7x7)){
							accum = 0;
							for (int i = 0; i < convPixels.length; i++) {
								accum+= convPixels[i];
							}
						}else{
							Arrays.sort(convPixels);
							accum = convPixels[convSize/2]*convSize;
						}
						enhacedBytes[pixelIndex]|= ((accum/(convSize))&0x0000FFFF);
						pixelIndex++;
					}
				}
			}
			roiSourceData = enhacedBytes;
		}
		
		final short[] finalROISourceData = new short[oldROI.getISize().getX()*oldROI.getISize().getY()];
		System.arraycopy(roiSourceData, finalROISourceData.length*overlayEditorPanelJAI.getZ(), finalROISourceData, 0, finalROISourceData.length);
		SwingUtilities.invokeAndWait(new Runnable(){public void run(){//}});
			try{
			overlayEditorPanelJAI.displaySpecialData(finalROISourceData, oldROI.getISize().getX(), oldROI.getISize().getY());
			}catch(Exception e){
				throw new RuntimeException("Error displaying TimeAverage data.  "+e.getMessage());
			}
		}});


		Integer oldThreshold = (bNew?null:thresholdSliderIntensityLookup[thresholdSlider.getValue()]);
		
		lastROISourceDataShort = roiSourceData;
		TreeMap<Integer, Integer> condensedBins= getCondensedBins(lastROISourceDataShort, originalROI.getROIName());
		 Integer[] intensityIntegers = condensedBins.keySet().toArray(new Integer[0]);
		 thresholdSliderIntensityLookup = new int[intensityIntegers.length];
		 for (int i = 0; i < intensityIntegers.length; i++) {
			 thresholdSliderIntensityLookup[i] = intensityIntegers[i];
		}
		
		thresholdSlider.removeChangeListener(processTimepointChangeListener);
		
		SwingUtilities.invokeAndWait(new Runnable(){public void run(){//}});
			thresholdSlider.setMaximum(thresholdSliderIntensityLookup.length-1);
		}});
		int newThresholdIndex = thresholdSliderIntensityLookup.length/2;
		if(bNew){
			newThresholdIndex = getHistogramIntensityAtHalfPixelCount(condensedBins);
			newThresholdIndex =
				(originalROI.getROIName().equals(VFrap_ROISourceData.VFRAP_ROI_ENUM.ROI_CELL.name())
					?thresholdSliderIntensityLookup.length-newThresholdIndex-1
					:newThresholdIndex);

		}else{
			for (int i = 0; i < thresholdSliderIntensityLookup.length; i++) {
				if(thresholdSliderIntensityLookup[i] >= oldThreshold){
					newThresholdIndex = i;
					break;
				}
			}
		}
		final int finalNewThresholdIndex = newThresholdIndex;
		SwingUtilities.invokeAndWait(new Runnable(){public void run(){//}});
			thresholdSlider.setValue(finalNewThresholdIndex);
			processTimepointChangeListener.stateChanged(null);
		}});
		
		thresholdSlider.addChangeListener(processTimepointChangeListener);
		
	}
	public void init(ROI originalROI, VFrap_ROISourceData frapData,VFrap_OverlayEditorPanelJAI overlayEditorPanelJAI)
	{
		resolveROIButton.setEnabled(false);
		applyROIButton.setEnabled(false);
		fillVoidsButton.setEnabled(false);
		
		this.originalROI = originalROI;
		
		this.frapData = frapData;
		this.overlayEditorPanelJAI = overlayEditorPanelJAI;
		
		roiSourceComboBox.removeActionListener(createROISourceDataActionListener);
		roiSourceComboBox.removeAllItems();
		roiSourceComboBox.insertItemAt("Time Average", 0);
		for (int i = 0; i < frapData.getImageDataset().getImageTimeStamps().length; i++) {
			roiSourceComboBox.insertItemAt(frapData.getImageDataset().getImageTimeStamps()[i], i+1);
		}
		int currentTime = overlayEditorPanelJAI.getT();
		roiSourceComboBox.setSelectedIndex(currentTime+1);
//		roiSourceComboBox.setSelectedIndex(0);
		roiSourceComboBox.addActionListener(createROISourceDataActionListener);
		
		thresholdSlider.removeChangeListener(processTimepointChangeListener);
		thresholdSlider.setValue(0);
		
		roiTimeAverageDataShort = null;
		lastROISourceDataShort = null;
		lastRegionInfos = null;
		new Thread(new Runnable(){public void run(){
			try{
				createROISourceData(true);
			}catch(final Exception e){
				e.printStackTrace();
				SwingUtilities.invokeLater(new Runnable(){public void run(){//}});
					DialogUtils.showErrorDialog(VFrap_ROIAssistPanel.this, "Auto ROI error:"+e.getMessage());
				}});
			}
		}}).start();

	}
	
	private TreeMap<Integer, Integer> getCondensedBins(short[] binThis,String roiName){
		boolean bMaskWithCell = !roiName.equals(VFrap_ROISourceData.VFRAP_ROI_ENUM.ROI_CELL.name());
		short[] cellMaskArr = (bMaskWithCell?frapData.getRoi(VFrap_ROISourceData.VFRAP_ROI_ENUM.ROI_CELL.name()).getPixelsXYZ():null);
		int[] bins = new int[MAX_SCALE+1];
		int[] tempLookup = new int[bins.length];
		for (int i = 0; i < bins.length; i++) {
			tempLookup[i] = i;
		}
		int binTotal = 0;
		for (int i = 0; i < binThis.length; i++) {
			int index = (binThis[i]&0x0000FFFF);
			boolean bSet = isSet(roiName, binThis[i], MAX_SCALE, tempLookup,(bMaskWithCell?cellMaskArr[i] != 0:true));
			bins[index]+= (bSet?1:0);
			binTotal+= (bSet?1:0);
		}
		TreeMap<Integer, Integer> condensedBinsMap = new TreeMap<Integer, Integer>();
		for (int i = 0; i < bins.length; i++) {
			if(bins[i] != 0){
				condensedBinsMap.put(i, bins[i]);
			}
		}
		return condensedBinsMap;
	}
	private int getHistogramIntensityAtHalfPixelCount(TreeMap<Integer, Integer> condensedBins){
		int binTotal = 0;
		Set<Integer> intensityKeySet = condensedBins.keySet();
		Iterator<Integer> intensityIter = intensityKeySet.iterator();
		while(intensityIter.hasNext()){
			binTotal+= condensedBins.get(intensityIter.next());
		}
		int accum = binTotal;
		intensityIter = intensityKeySet.iterator();
		int intensityIndex = 0;
		while(intensityIter.hasNext()){
			Integer intensity = intensityIter.next();
			accum-= condensedBins.get(intensity);
			if(accum < binTotal/2){
				if(intensityIter.hasNext()){
					return intensityIndex+1;
				}
				return intensityIndex;
			}
			intensityIndex++;
		}
		return 0;
	}
	private void scaleDataInPlace(short[] originalUnsignedShortData){
		int min = originalUnsignedShortData[0]&0x0000FFFF;
		int max = min;
		for (int i = 0; i < originalUnsignedShortData.length; i++) {
			min = Math.min(min, originalUnsignedShortData[i]&0x0000FFFF);
			max = Math.max(max, originalUnsignedShortData[i]&0x0000FFFF);
		}
		double scale = (double)MAX_SCALE/(double)(max-min);
		double offset = (double)(MAX_SCALE*min)/(double)(min-max);
		for (int i = 0; i < originalUnsignedShortData.length; i++) {
			short tempShort = 0;
			tempShort|= (int)((originalUnsignedShortData[i]&0x0000FFFF)*scale+offset);
			originalUnsignedShortData[i] = tempShort;

		}
	}
	private  void processTimepoint(){
		new Thread(new Runnable(){public void run(){
		try{			
			
			boolean bInvert = !frapData.getCurrentlyDisplayedROI().getROIName().equals(VFrap_ROISourceData.VFRAP_ROI_ENUM.ROI_CELL.name());

			short[] cellMask = null;
			if(bInvert){
				cellMask = frapData.getRoi(VFrap_ROISourceData.VFRAP_ROI_ENUM.ROI_CELL.name()).getPixelsXYZ();
			}
			short[] shortPixels = new short[lastROISourceDataShort.length];
			
			for (int i = 0; i < lastROISourceDataShort.length; i++) {
				shortPixels[i] =(short)
					(isSet(frapData.getCurrentlyDisplayedROI().getROIName(), lastROISourceDataShort[i],
					thresholdSlider.getValue(),thresholdSliderIntensityLookup,
					(cellMask == null?true:cellMask[i] != 0))
						?0xFFFF:0);
			}
			UShortImage ushortImage =
				new UShortImage(shortPixels,originalROI.getRoiImages()[0].getOrigin(),originalROI.getRoiImages()[0].getExtent(),
						originalROI.getISize().getX(),originalROI.getISize().getY(),originalROI.getISize().getZ());
			ROI newCellROI = new ROI(ushortImage,frapData.getCurrentlyDisplayedROI().getROIName());
			final ROI forceValidROI = forceROIValid(newCellROI);
			SwingUtilities.invokeAndWait(new Runnable(){public void run(){//}});
				frapData.addReplaceRoi(forceValidROI);
			}});

		}catch(final Exception e2){
			lastROISourceDataShort = null;
			e2.printStackTrace();
			SwingUtilities.invokeLater(new Runnable(){public void run(){//}});
				DialogUtils.showErrorDialog(VFrap_ROIAssistPanel.this, "Error setting new ROI. "+e2.getMessage());
			}});
		}
		}}).start();
	
	}

	private boolean isSet(String roiName,short roiSourceDataUnsignedShort,int thresholdIndex,int[] thresholdLookupArr,boolean cellMask){
		if(!roiName.equals(VFrap_ROISourceData.VFRAP_ROI_ENUM.ROI_CELL.name())){
			if(((roiSourceDataUnsignedShort&0x0000FFFF)) >/*=*/ thresholdLookupArr[thresholdIndex]){
				return false;//shortPixels[i] = 0;
			}else{
				if(roiName.equals(VFrap_ROISourceData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name())){
					return (cellMask?false:true);//shortPixels[i]|= (cellMask[i] == 0?0xFFFF:0);//0xFFFF;
				}else if(cellMask){
					return true;//shortPixels[i]|= 0xFFFF;
				}else{
					return false;//shortPixels[i] = 0;
				}
			}
		}else{
			if(((roiSourceDataUnsignedShort&0x0000FFFF)) < thresholdLookupArr[thresholdLookupArr.length-1-thresholdIndex]){
				return false;//shortPixels[i] = 0;
			}else{
				return true;//shortPixels[i]|= 0xFFFF;
			}
		}

	}
	
	
	public void paint(Graphics g){
		super.paint(g);

	}

	private ROI forceROIValid(ROI validateROI) throws Exception{
		if(validateROI.getROIName().equals(VFrap_ROISourceData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name())){
			lastRegionInfos = null;
			SwingUtilities.invokeAndWait(new Runnable(){public void run(){//}});
				applyROIButton.setEnabled(true);
				resolveROIButton.setEnabled(false);
				fillVoidsButton.setEnabled(false);
			}});
			return validateROI;
		}
		if(validateROI.getNonzeroPixelsCount() < 1){
			return validateROI;
		}
		//
		//Remove pixels touching only at corners
		//
		short[] validatePixels = validateROI.getPixelsXYZ();
			
		//
		//Remove noise objects
		//
		lastRegionInfos = calculateRegionInfos(validatePixels);
		Vector<RegionInfo> roiRegionInfoV = new Vector<RegionInfo>();
		for (int i = 0; i < lastRegionInfos.length; i++) {
			if(lastRegionInfos[i].getPixelValue() == 1){
				roiRegionInfoV.add(lastRegionInfos[i]);
			}
//			System.out.println(lastRegionInfos[i]);
		}

//		if(validateROI.getROIType().equals(RoiType.ROI_CELL) || validateROI.getROIType().equals(RoiType.ROI_BLEACHED)){
			if(roiRegionInfoV.size()> 1){
				SwingUtilities.invokeAndWait(new Runnable(){public void run(){
					applyROIButton.setEnabled(false);
					resolveROIButton.setEnabled(true);
					fillVoidsButton.setEnabled(false);
				}});
			}else{
				final boolean hasInternalVoids = fillVoids(validatePixels,true);
				SwingUtilities.invokeAndWait(new Runnable(){public void run(){//}});
					lastRegionInfos = null;
					applyROIButton.setEnabled(!hasInternalVoids);
					resolveROIButton.setEnabled(false);
					fillVoidsButton.setEnabled(hasInternalVoids);
				}});
			}

		UShortImage ushortImage =
			new UShortImage(validatePixels,validateROI.getRoiImages()[0].getOrigin(),validateROI.getRoiImages()[0].getExtent(),
					validateROI.getISize().getX(),validateROI.getISize().getY(),validateROI.getISize().getZ());
		ROI newROI = new ROI(ushortImage,validateROI.getROIName());
				
		if(newROI.compareEqual(validateROI)){
			return validateROI;
		}
		return newROI;

	}
	
	private boolean fillVoids(short[] fillvoidPixels,boolean bCheckOnly) throws Exception{
		boolean bHadAnyInternalVoids = false;
		int XYSIZE =
			frapData.getCurrentlyDisplayedROI().getISize().getX()*frapData.getCurrentlyDisplayedROI().getISize().getY();
		boolean bUseZ = frapData.getCurrentlyDisplayedROI().getISize().getZ()>1;
		RegionInfo[] newRegionInfos = calculateRegionInfos(fillvoidPixels);
		for (int i = 0; i < newRegionInfos.length; i++) {
			if(newRegionInfos[i].getPixelValue() == 0){
				boolean bInternalVoid = true;
				for (int z = 0; z < frapData.getCurrentlyDisplayedROI().getISize().getZ(); z++) {
					int zOffset = z*XYSIZE;
					for (int y = 0; y < frapData.getCurrentlyDisplayedROI().getISize().getY(); y++) {
						int yoffset = y*frapData.getCurrentlyDisplayedROI().getISize().getX();
						int zyOffset = zOffset+yoffset;
						for (int x = 0; x < frapData.getCurrentlyDisplayedROI().getISize().getX(); x++) {
							if(newRegionInfos[i].isIndexInRegion(zyOffset+x)){
								if(x==0 || y==0 | (bUseZ && z==0) |
									x==(frapData.getCurrentlyDisplayedROI().getISize().getX()-1) ||
									y==(frapData.getCurrentlyDisplayedROI().getISize().getY()-1) ||
									(bUseZ && z==(frapData.getCurrentlyDisplayedROI().getISize().getZ()-1))){
									bInternalVoid = false;
									break;
								}
							}
						}
						if(!bInternalVoid){break;}
					}
					if(!bInternalVoid){break;}
				}
				if(bInternalVoid){
					bHadAnyInternalVoids = true;
					if(bCheckOnly){
						return bHadAnyInternalVoids;
					}
					for (int j = 0; j < fillvoidPixels.length; j++) {
						if(newRegionInfos[i].isIndexInRegion(j)){
							fillvoidPixels[j]|= 0xFFFF;
						}
					}
				}
			}
		}
		return bHadAnyInternalVoids;
	}
	private RegionInfo[] calculateRegionInfos(short[] validatePixels) throws Exception{
		byte[] roiBytes = new byte[validatePixels.length];
		for (int i = 0; i < roiBytes.length; i++) {
			roiBytes[i] = (byte)(validatePixels[i] == 0?0:1);
		}
		VCImageUncompressed roiVCImage =
			new VCImageUncompressed(null,roiBytes,new Extent(1,1,1),
					originalROI.getISize().getX(),originalROI.getISize().getY(),originalROI.getISize().getZ());
		RegionImage roiRegionImage = new RegionImage(roiVCImage,0,null,null,RegionImage.NO_SMOOTHING);
		return roiRegionImage.getRegionInfos();
	}
	
	private RegionInfo pickKeepRegionInfoFromCurrentROI() throws Exception{
		if(lastRegionInfos == null){
			throw new Exception("No regionInfo to resolve");
		}

		final Vector<RegionInfo> roiRegionInfoV2 = new Vector<RegionInfo>();
		for (int i = 0; i < lastRegionInfos.length; i++) {
			if(lastRegionInfos[i].getPixelValue() == 1){
				roiRegionInfoV2.add(lastRegionInfos[i]);
			}
		}
		if(roiRegionInfoV2.size() <= 1){
			throw new Exception("No regionInfo to resolve");
		}

		final RegionInfo[] regionInfoArr = roiRegionInfoV2.toArray(new RegionInfo[0]);
		Arrays.sort(regionInfoArr,
				new Comparator<RegionInfo>(){
					public int compare(RegionInfo o1, RegionInfo o2) {
						return o2.getNumPixels() - o1.getNumPixels();
					}}
		);
		final Object[][] rowData = new Object[regionInfoArr.length][1];
		for (int i = 0; i < regionInfoArr.length; i++) {
			rowData[i][0] = regionInfoArr[i].getNumPixels()+" pixels";
		}
		
		ROI beforeROI = new ROI(frapData.getCurrentlyDisplayedROI());
		int[] resultArr = null;
//		SwingUtilities.invokeAndWait(new Runnable(){public void run(){//}});
			try{
				resultArr = /*DialogUtils.*/showComponentOKCancelTableList(this, "Select ROI to Keep",
					new String[] {"ROI Size (pixel count)"}, rowData, ListSelectionModel.SINGLE_SELECTION,
					regionInfoArr,beforeROI.getPixelsXYZ());
			}catch(UserCancelException e){
				resultArr = null;
			}
//		}});
		if(resultArr != null && resultArr.length > 0){
			return (resultArr == null?null:regionInfoArr[resultArr[0]]);
		}else{
//			SwingUtilities.invokeAndWait(new Runnable(){public void run(){//}});
				frapData.addReplaceRoi(beforeROI);
				applyROIButton.setEnabled(false);
				resolveROIButton.setEnabled(true);
				fillVoidsButton.setEnabled(false);
//			}});
		}
		return null;
	}
	private void resolveCurrentROI(RegionInfo keepRegion) throws Exception{
					short[] removePixels = frapData.getCurrentlyDisplayedROI().getPixelsXYZ();
					for (int i = 0; i < removePixels.length; i++) {
						if(!keepRegion.isIndexInRegion(i)){
							removePixels[i] = 0;
						}
					}
					final boolean hasInternalVoids = fillVoids(removePixels,true);

					UShortImage ushortImage =
						new UShortImage(removePixels,
								frapData.getCurrentlyDisplayedROI().getRoiImages()[0].getOrigin(),
								frapData.getCurrentlyDisplayedROI().getRoiImages()[0].getExtent(),
								frapData.getCurrentlyDisplayedROI().getISize().getX(),
								frapData.getCurrentlyDisplayedROI().getISize().getY(),
								frapData.getCurrentlyDisplayedROI().getISize().getZ());
					final ROI newCellROI = new ROI(ushortImage,frapData.getCurrentlyDisplayedROI().getROIName());
					SwingUtilities.invokeAndWait(new Runnable(){public void run(){//}});
						frapData.addReplaceRoi(newCellROI);
						applyROIButton.setEnabled(!hasInternalVoids);
						resolveROIButton.setEnabled(false);
						fillVoidsButton.setEnabled(hasInternalVoids);
					}});
	}
		
	public int[] showComponentOKCancelTableList(final Component requester,String title,
			String[] columnNames,Object[][] rowData,int listSelectionModel_SelectMode,
			final RegionInfo[] regionInfoArr,final short[] multiObjectROIPixels)
				throws UserCancelException{
		
		DefaultTableModel tableModel = new DefaultTableModel(){
		    public boolean isCellEditable(int row, int column) {
		        return false;
		    }
		};
		tableModel.setDataVector(rowData, columnNames);
		final JTable table = new JTable(tableModel);
		table.setSelectionMode(listSelectionModel_SelectMode);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setPreferredScrollableViewportSize(new Dimension(500, 250));

		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener(){
					public void valueChanged(ListSelectionEvent e) {
						if(!e.getValueIsAdjusting()){
							try{
								int index = table.getSelectedRow();
//								System.out.println("list index="+index);
								RegionInfo keepRegion = regionInfoArr[index];
								short[] removePixels = multiObjectROIPixels.clone();
								for (int i = 0; i < removePixels.length; i++) {
									if(!keepRegion.isIndexInRegion(i)){
										removePixels[i] = 0;
									}
								}
								UShortImage ushortImage =
									new UShortImage(removePixels,
											originalROI.getRoiImages()[0].getOrigin(),
											originalROI.getRoiImages()[0].getExtent(),
											originalROI.getISize().getX(),
											originalROI.getISize().getY(),
											originalROI.getISize().getZ());
								final ROI newCellROI = new ROI(ushortImage,frapData.getCurrentlyDisplayedROI().getROIName());
								frapData.addReplaceRoi(newCellROI);
							}catch(Exception e2){
								e2.printStackTrace();
								throw new RuntimeException("Error selecting resolved ROI",e2);
							}

						}
					}
				}
		);
		
		ScopedExpressionTableCellRenderer.formatTableCellSizes(table);
		
		int result = DialogUtils.showComponentOKCancelDialog(requester, scrollPane, title);
		if(result != JOptionPane.OK_OPTION){
			throw UserCancelException.CANCEL_GENERIC;
		}

		return table.getSelectedRows();
	}
	
	private void waitCursor(final boolean bOn){
		if(SwingUtilities.isEventDispatchThread()){
			BeanUtils.setCursorThroughout(BeanUtils.findTypeParentOfComponent(VFrap_ROIAssistPanel.this, Window.class),
					(bOn?Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR):Cursor.getDefaultCursor()));
		}else{
			try{
				SwingUtilities.invokeAndWait(new Runnable(){public void run(){//}});
					BeanUtils.setCursorThroughout(BeanUtils.findTypeParentOfComponent(VFrap_ROIAssistPanel.this, Window.class),
							(bOn?Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR):Cursor.getDefaultCursor()));
				}});
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
}


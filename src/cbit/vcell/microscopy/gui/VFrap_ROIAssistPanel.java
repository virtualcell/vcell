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

public class VFrap_ROIAssistPanel extends JPanel {
	
	private static final int MAX_SCALE = 0x0000FFFF;
	
	private JButton fillVoidsButton;
	private JButton applyROIButton;
	private JButton resolveROIButton;
	private JPanel histogramPanel;
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
							DialogUtils.showErrorDialog(VFrap_ROIAssistPanel.this, "Error creating ROI source\n"+e2.getMessage());
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
	
	private Window disposableWindow;
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
		roiSourceLabel.setFont(new Font("", Font.BOLD, 12));
		roiSourceLabel.setText("ROI Threshold Source");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(10, 4, 0, 4);
		gridBagConstraints_1.weightx = 1;
		gridBagConstraints_1.gridx = 0;
		gridBagConstraints_1.gridy = 0;
		add(roiSourceLabel, gridBagConstraints_1);

		final JLabel spatialEnahnceLabel = new JLabel();
		spatialEnahnceLabel.setFont(new Font("", Font.BOLD, 12));
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

		histogramPanel = new JPanel();
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_4.gridwidth = 2;
		gridBagConstraints_4.fill = GridBagConstraints.BOTH;
		gridBagConstraints_4.weighty = 1;
		gridBagConstraints_4.weightx = 1;
		gridBagConstraints_4.gridy = 2;
		gridBagConstraints_4.gridx = 0;
		add(histogramPanel, gridBagConstraints_4);

		thresholdSlider = new JSlider();
		thresholdSlider.setMaximum(MAX_SCALE);
		thresholdSlider.addChangeListener(processTimepointChangeListener);
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_5.gridwidth = 2;
		gridBagConstraints_5.gridx = 0;
		gridBagConstraints_5.gridy = 4;
		add(thresholdSlider, gridBagConstraints_5);

		final JLabel thresholdForRoiLabel = new JLabel();
		thresholdForRoiLabel.setFont(new Font("", Font.BOLD, 12));
		thresholdForRoiLabel.setText("Threshold Adjust ROI");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.gridwidth = 2;
		gridBagConstraints_7.gridy = 3;
		gridBagConstraints_7.gridx = 0;
		add(thresholdForRoiLabel, gridBagConstraints_7);

		final JLabel lowerLabel = new JLabel();
		lowerLabel.setFont(new Font("", Font.BOLD, 14));
		lowerLabel.setText("ROI Shrink");
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.insets = new Insets(0, 4, 0, 0);
		gridBagConstraints_8.anchor = GridBagConstraints.WEST;
		gridBagConstraints_8.gridy = 5;
		gridBagConstraints_8.gridx = 0;
		add(lowerLabel, gridBagConstraints_8);

		final JLabel higherLabel = new JLabel();
		higherLabel.setFont(new Font("", Font.BOLD, 14));
		higherLabel.setText("ROI Grow");
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.insets = new Insets(0, 0, 0, 4);
		gridBagConstraints_9.anchor = GridBagConstraints.EAST;
		gridBagConstraints_9.gridy = 5;
		gridBagConstraints_9.gridx = 1;
		add(higherLabel, gridBagConstraints_9);

		final JPanel okCancelJPanel = new JPanel();
		okCancelJPanel.setLayout(new GridLayout(1, 0));
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.insets = new Insets(20, 2, 2, 2);
		gridBagConstraints_6.gridwidth = 2;
		gridBagConstraints_6.gridy = 6;
		gridBagConstraints_6.gridx = 0;
		add(okCancelJPanel, gridBagConstraints_6);

		applyROIButton = new JButton();
		applyROIButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try{
					overlayEditorPanelJAI.displaySpecialData(null, 0, 0);
				}catch(Exception e2){
					e2.printStackTrace();
				}
				if(disposableWindow != null){
					disposableWindow.dispose();
				}else{
					BeanUtils.dispose(VFrap_ROIAssistPanel.this);
				}
			}
		});
		applyROIButton.setText("Apply ROI");
		okCancelJPanel.add(applyROIButton);

		final JButton cancelButton = new JButton();
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try{
					overlayEditorPanelJAI.displaySpecialData(null, 0, 0);
				}catch(Exception e2){
					e2.printStackTrace();
				}
				frapData.addReplaceRoi(originalROI);
				if(disposableWindow != null){
					disposableWindow.dispose();
				}else{
					BeanUtils.dispose(VFrap_ROIAssistPanel.this);
				}
			}
		});
		cancelButton.setText("Cancel");
		okCancelJPanel.add(cancelButton);

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
		okCancelJPanel.add(resolveROIButton);

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
		okCancelJPanel.add(fillVoidsButton);
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
//			SwingUtilities.invokeAndWait(new Runnable(){public void run(){//}});
//				overlayEditorPanelJAI.setTimeIndex(timeIndex+1);//time starts at 1, quirk of overlayeditor (look into)
//			}});
			
			roiSourceData = collectAllZAtOneTimepointIntoOneArray(frapData.getImageDataset(), timeIndex);
			
////			short[] unScaledShorts = new short[roiSourceData.length];
////			int min = 0x000FFFF&frapData.getImageDataset().getImage(0, 0,timeIndex).getPixel(0, 0, 0);
////			int max = min;
//			int pixelIndex = 0;
//			for (int z = 0; z < frapData.getImageDataset().getSizeZ(); z++) {
//				UShortImage timePointDataImage = frapData.getImageDataset().getImage(z, 0,timeIndex);
//				for (int y = 0; y < timePointDataImage.getNumY(); y++) {
//					for (int x = 0; x < timePointDataImage.getNumX(); x++) {
//						roiSourceData[pixelIndex] = timePointDataImage.getPixel(x,y,0);
////						unScaledShorts[pixelIndex] = timePointDataImage.getPixel(x,y,0);
////						min = Math.min(min,unScaledShorts[pixelIndex]&0x0000FFFF);
////						max = Math.max(max,unScaledShorts[pixelIndex]&0x0000FFFF);
//						pixelIndex++;
//					}
//				}
//			}
			
			
//			double scale = 255.0/(max-min);
//			double offset = (255.0*min)/(min-max);
//			for (int i = 0; i < unScaledShorts.length; i++) {
//				System.out.println((unScaledShorts[i]&0x0000FFFF)+"  "+
//						(((int)((unScaledShorts[i]&0x0000FFFF)/(255.0/max))&0x000000FF))+"  "+
//						(int)((unScaledShorts[i]&0x0000FFFF)*scale+offset));
//				roiSourceData[i]|= (int)((unScaledShorts[i]&0x0000FFFF)*scale+offset);
//			}
			
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
//			(originalROI.getROIType().equals(RoiType.ROI_CELL)
//				?thresholdSliderIntensityLookup.length-newThresholdIndex-1
//				:newThresholdIndex);
		SwingUtilities.invokeAndWait(new Runnable(){public void run(){//}});
			thresholdSlider.setValue(finalNewThresholdIndex);
			processTimepointChangeListener.stateChanged(null);
		}});
		
		thresholdSlider.addChangeListener(processTimepointChangeListener);
		
	}
	public void init(Window disposableWindow,ROI originalROI,
			VFrap_ROISourceData frapData,VFrap_OverlayEditorPanelJAI overlayEditorPanelJAI){
		
		resolveROIButton.setEnabled(false);
		applyROIButton.setEnabled(false);
		fillVoidsButton.setEnabled(false);
		
		this.originalROI = originalROI;
		
		this.disposableWindow = disposableWindow;
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
		
//		try{
//			thresholdSlider.removeChangeListener(processTimepointChangeListener);
//			createROISourceData();
//////			int numTimes = frapData.getImageDataset().getSizeT();
//////			long[] timeSum = new long[frapData.getImageDataset().getISize().getXYZ()];
//////			for (int t = 0; t < numTimes; t++) {
//////				int pixelIndex = 0;
//////				for (int z = 0; z < frapData.getImageDataset().getSizeZ(); z++) {
//////					UShortImage timePointDataImage = frapData.getImageDataset().getImage(z, 0, t);
//////					for (int y = 0; y < timePointDataImage.getNumY(); y++) {
//////						for (int x = 0; x < timePointDataImage.getNumX(); x++) {
//////							timeSum[pixelIndex]+= timePointDataImage.getPixel(x,y,0)&0x0000FFFF;
//////							pixelIndex++;  
//////						}
//////					}
//////				}
//////			}
//////			roiTimeAverageDataShort = new short[timeSum.length];
//////			for (int i = 0; i < timeSum.length; i++) {
//////				roiTimeAverageDataShort[i]|= ((int)(timeSum[i]/numTimes))&0x0000FFFF;
//////			}
//////			scaleDataInPlace(roiTimeAverageDataShort);
////			
////			
////			
//////			int currentTime = overlayEditorPanelJAI.getT();
//////			roiSourceComboBox.removeActionListener(processROIActionListener);
//////			roiSourceComboBox.setSelectedIndex(currentTime+1);
//////			roiSourceComboBox.addActionListener(processROIActionListener);
////
////			short[] timePointPixels = collectAllZAtOneTimepointIntoOneArray(frapData.getImageDataset(),currentTime);
////			
//////			short[] timePointPixels = new short[frapData.getImageDataset().getISize().getXYZ()];
//////			int pixelIndex = 0;
//////			for (int z = 0; z < frapData.getImageDataset().getSizeZ(); z++) {
//////				short[] slicePixels = frapData.getImageDataset().getImage(z, 0, currentTime).getPixels();
//////				System.arraycopy(slicePixels, 0, timePointPixels, pixelIndex, slicePixels.length);
//////				pixelIndex+= slicePixels.length;
//////				
////////				UShortImage timePointDataImage = frapData.getImageDataset().getImage(z, 0, currentTime);
////////				for (int y = 0; y < timePointDataImage.getNumY(); y++) {
////////					for (int x = 0; x < timePointDataImage.getNumX(); x++) {
////////						timePointPixels[pixelIndex]+= timePointDataImage.getPixel(x,y,0)&0x0000FFFF;
////////						pixelIndex++; 
////////					}
////////				}
//////			}
////			
////			scaleDataInPlace(timePointPixels);
////			
////			HashMap<Integer, Integer> condensedBins = getCondensedBins(timePointPixels, originalROI.getROIType());
////			int sliderVal = getHistogramIntensityAtHalfPixelCount(condensedBins);
////		
//////			int sliderVal = getHistogramIndexAtHalfPixelCount(timePointPixels, originalROI.getROIType());
////			thresholdSlider.setValue((originalROI.getROIType().equals(RoiType.ROI_CELL)?MAX_SCALE-sliderVal:sliderVal));
////			
//////			short[] cellMaskArr = frapData.getRoi(RoiType.ROI_CELL).getPixelsXYZ();
//////			int[] bins = new int[MAX_SCALE+1];
//////			int binTotal = 0;
////////			int threshold = (originalROI.getROIType().equals(RoiType.ROI_CELL)?MAX_SCALE:0);
//////			for (int i = 0; i < roiTimeAverageDataShort.length; i++) {
//////				boolean bSet = isSet(originalROI.getROIType(), roiTimeAverageDataShort[i], MAX_SCALE, cellMaskArr[i] != 0);
//////				if(bSet){
//////					System.out.println();
//////				}
//////				bins[(int)(roiTimeAverageDataShort[i]&0x0000FFFF)]+= (bSet?1:0);
//////				binTotal+= (bSet?1:0);
//////			}
//////			int sliderVal = 0;
//////			int accum = binTotal;
////////			int lastNonZeroBinIndex = -1;
//////			for (int i = 0; i < bins.length; i++) {
//////				if(bins[i] != 0){
//////					accum-= bins[i];
//////					if(accum < binTotal/2){
//////						sliderVal = i+1;
//////						if(sliderVal> MAX_SCALE){
//////							sliderVal = MAX_SCALE;
//////						}
//////						thresholdSlider.setValue((originalROI.getROIType().equals(RoiType.ROI_CELL)?MAX_SCALE-sliderVal:sliderVal));
//////						break;
//////					}
////////					lastNonZeroBinIndex = i;
//////				}
//////			}
////			
//////			int min = (int)(timeAvg[0]/numTimes);
//////			int max = min;
//////			for (int i = 0; i < timeAvg.length; i++) {
//////				min = (int)Math.min(min,timeAvg[i]/numTimes);
//////				max = (int)Math.max(max, timeAvg[i]/numTimes);
//////			}
//////			roiTimeAverageData = new byte[timeAvg.length];
//////			int[] bins = new int[max+1];
//////			for (int i = 0; i < timeAvg.length; i++) {
//////				roiTimeAverageData[i] = (byte)((int)(((timeAvg[i]/numTimes)/(255.0/max)))&0x000000FF);
//////				bins[(int)(roiTimeAverageData[i]&0x000000FF)]++;
//////			}
//////			//calculate half the pixels
//////			int accum = 0;
//////			for (int i = 0; i < bins.length; i++) {
//////				accum+= bins[i];
//////				if(accum > roiTimeAverageData.length/2){
//////					int sliderVal = (i==255?255:i+1);
//////					thresholdSlider.setValue((originalROI.getROIType().equals(RoiType.ROI_CELL)?255-sliderVal:sliderVal));
//////					break;
//////				}
//////			}
////			
////			
//////			for (int i = 0; i < bins.length; i++) {
//////				if(bins[i] != 0){
//////					System.out.print(i+" ");
//////					for (int j = 0; j < bins[i]; j++) {
//////						if(j%1 == 0){
//////							System.out.print("*");
//////						}
//////					}
//////					System.out.println();
//////				}
//////			}
//////			System.out.println("Total pixels ="+frapData.getImageDataset().getISize()+" t="+frapData.getImageDataset().getSizeT());
//		}catch(Exception e){
//			e.printStackTrace();
//			DialogUtils.showErrorDialog("Auto ROI error:"+e.getMessage());
//		}

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
			int index = (int)(binThis[i]&0x0000FFFF);
//			if(index == 65535){
//				System.out.println();
//			}
			boolean bSet = isSet(roiName, binThis[i], MAX_SCALE, tempLookup,(bMaskWithCell?cellMaskArr[i] != 0:true));
//			if(index  == 65535){
//				System.out.println(("x="+i%originalROI.getISize().getX())+" y="+(i/originalROI.getISize().getX())+" bset="+bSet+" mask="+(bMaskWithCell?cellMaskArr[i] != 0:true));
//			}
			bins[index]+= (bSet?1:0);
			binTotal+= (bSet?1:0);
		}
		TreeMap<Integer, Integer> condensedBinsMap = new TreeMap<Integer, Integer>();
		for (int i = 0; i < bins.length; i++) {
			if(bins[i] != 0){
//				System.out.println(i);
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
//				int sliderVal = intensityIndex+1;
//				if(sliderVal> MAX_SCALE){
//					sliderVal = MAX_SCALE;
//				}
//				return sliderVal;
			}
			intensityIndex++;
		}
		return 0;
//		int sliderVal = 0;
//		int accum = binTotal;
//		for (int i = 0; i < bins.length; i++) {
//			if(bins[i] != 0){
//				accum-= bins[i];
//				if(accum < binTotal/2){
//					sliderVal = i+1;
//					if(sliderVal> MAX_SCALE){
//						sliderVal = MAX_SCALE;
//					}
//					return i;//(roiType.equals(RoiType.ROI_CELL)?MAX_SCALE-sliderVal:sliderVal);
//				}
//			}
//		}
//		return 0;

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
//			if(i== 12035){
//				System.out.println("ok "+(originalUnsignedShortData[i]&0x0000FFFF)+"  "+((int)((originalUnsignedShortData[i]&0x0000FFFF)*scale+offset))+" "+tempShort);
//			}
//			if((int)(originalUnsignedShortData[i]&0x0000FFFF) == 221){
//				System.out.println("221="+(tempShort&0x0000FFFF)+" scale="+scale+" offset="+offset);
//			}
			originalUnsignedShortData[i] = tempShort;

		}
//		System.out.println(scale+"  "+offset);

	}
	private  void processTimepoint(){
//		final AsynchProgressPopup pp =
//			new AsynchProgressPopup(
//				ROIAssistPanel.this,
//				"Thresholding...",
//				"Working...",true,false
//		);
//		pp.startKeepOnTop();

		new Thread(new Runnable(){public void run(){
		try{			
			
//			ROI oldROI = frapData.getRoi(ROI.RoiType.ROI_CELL);
//			short[] roiSourceData = null;//new short[oldROI.getISize().getXYZ()];
//			if(roiSourceComboBox.getSelectedIndex() == 0){//timeAverage
//				if(roiTimeAverageDataShort == null){
//					createScaledTimeAverageData();
//				}
//				roiSourceData = roiTimeAverageDataShort.clone();
//			}else{
//				final int timeIndex = roiSourceComboBox.getSelectedIndex()-1;
//				SwingUtilities.invokeAndWait(new Runnable(){public void run(){//}});
//					overlayEditorPanelJAI.setTimeIndex(timeIndex+1);//time starts at 1, quirk of overlayeditor (look into)
//				}});
//				
//				roiSourceData = collectAllZAtOneTimepointIntoOneArray(frapData.getImageDataset(), timeIndex);
//				
//////				short[] unScaledShorts = new short[roiSourceData.length];
//////				int min = 0x000FFFF&frapData.getImageDataset().getImage(0, 0,timeIndex).getPixel(0, 0, 0);
//////				int max = min;
////				int pixelIndex = 0;
////				for (int z = 0; z < frapData.getImageDataset().getSizeZ(); z++) {
////					UShortImage timePointDataImage = frapData.getImageDataset().getImage(z, 0,timeIndex);
////					for (int y = 0; y < timePointDataImage.getNumY(); y++) {
////						for (int x = 0; x < timePointDataImage.getNumX(); x++) {
////							roiSourceData[pixelIndex] = timePointDataImage.getPixel(x,y,0);
//////							unScaledShorts[pixelIndex] = timePointDataImage.getPixel(x,y,0);
//////							min = Math.min(min,unScaledShorts[pixelIndex]&0x0000FFFF);
//////							max = Math.max(max,unScaledShorts[pixelIndex]&0x0000FFFF);
////							pixelIndex++;
////						}
////					}
////				}
//				
//				
////				double scale = 255.0/(max-min);
////				double offset = (255.0*min)/(min-max);
////				for (int i = 0; i < unScaledShorts.length; i++) {
////					System.out.println((unScaledShorts[i]&0x0000FFFF)+"  "+
////							(((int)((unScaledShorts[i]&0x0000FFFF)/(255.0/max))&0x000000FF))+"  "+
////							(int)((unScaledShorts[i]&0x0000FFFF)*scale+offset));
////					roiSourceData[i]|= (int)((unScaledShorts[i]&0x0000FFFF)*scale+offset);
////				}
//				
//			}
//			scaleDataInPlace(roiSourceData);
//			
//			if(spatialEnhanceComboBox.getSelectedIndex() > 0){
//				short[] enhacedBytes = new short[roiSourceData.length];
//				int radius =
//					(spatialEnhanceComboBox.getSelectedItem().equals(ENHANCE_AVG_3X3)?1:0)+
//					(spatialEnhanceComboBox.getSelectedItem().equals(ENHANCE_AVG_5x5)?2:0)+
//					(spatialEnhanceComboBox.getSelectedItem().equals(ENHANCE_AVG_7x7)?3:0)+
//					(spatialEnhanceComboBox.getSelectedItem().equals(ENHANCE_MEDIAN_3X3)?1:0)+
//					(spatialEnhanceComboBox.getSelectedItem().equals(ENHANCE_MEDIAN_5x5)?2:0);
//				int pixelIndex = 0;
//				int accum = 0;
//				int convSize = (radius*2+1)*(radius*2+1);
//				int[] convPixels = new int[convSize];
//				for (int z = 0; z < oldROI.getISize().getZ(); z++) {
//					int zOffset = z*oldROI.getISize().getX()*oldROI.getISize().getY();
//					for (int y = 0; y < oldROI.getISize().getY(); y++) {
//						int yoffset = y*oldROI.getISize().getX();
//						for (int x = 0; x < oldROI.getISize().getX(); x++) {
//							accum = 0;
//							for (int xbox = -radius; xbox <= radius; xbox++) {
//								for (int ybox = -radius; ybox <= radius; ybox++) {
//									if(x+xbox >= 0 && x+xbox < oldROI.getISize().getX() &&
//										y+ybox >= 0 && y+ybox < oldROI.getISize().getY()){
//										convPixels[accum]= 0x0000FFFF&
//											roiSourceData[zOffset+yoffset+x+xbox+(ybox*oldROI.getISize().getX())];
//									}else{
//										convPixels[accum]= 0x0000FFFF&roiSourceData[zOffset+yoffset+x];
//									}
//									accum++;
//								}
//
//							}
//							if(spatialEnhanceComboBox.getSelectedItem().equals(ENHANCE_AVG_3X3) ||
//								spatialEnhanceComboBox.getSelectedItem().equals(ENHANCE_AVG_5x5) ||
//								spatialEnhanceComboBox.getSelectedItem().equals(ENHANCE_AVG_7x7)){
//								accum = 0;
//								for (int i = 0; i < convPixels.length; i++) {
//									accum+= convPixels[i];
//								}
//							}else{
//								Arrays.sort(convPixels);
//								accum = convPixels[convSize/2]*convSize;
//							}
//							enhacedBytes[pixelIndex]|= ((accum/(convSize))&0x0000FFFF);
//							pixelIndex++;
//						}
//					}
//				}
//				roiSourceData = enhacedBytes;
//			}
			
			
			boolean bIgnoreMask = false;
			boolean bInvert = !frapData.getCurrentlyDisplayedROI().getROIName().equals(VFrap_ROISourceData.VFRAP_ROI_ENUM.ROI_CELL.name());
//			if(bInvert){
//				bIgnoreMask = frapData.getRoi(RoiType.ROI_CELL).isAllPixelsZero();;
//			}
			boolean bBackground = frapData.getCurrentlyDisplayedROI().getROIName().equals(VFrap_ROISourceData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name());
			short[] cellMask = null;
			if(bInvert){
				cellMask = frapData.getRoi(VFrap_ROISourceData.VFRAP_ROI_ENUM.ROI_CELL.name()).getPixelsXYZ();
			}
			short[] shortPixels = new short[lastROISourceDataShort.length];
//			System.out.println(
//					thresholdSlider.getValue()+" "+
//					thresholdSliderIntensityLookup[thresholdSlider.getValue()]);
			
			for (int i = 0; i < lastROISourceDataShort.length; i++) {
//				if(lastROISourceDataShort[i] == 0xFFFF){
//					System.out.println();
//				}
				shortPixels[i] =(short)
					(isSet(frapData.getCurrentlyDisplayedROI().getROIName(), lastROISourceDataShort[i],
					thresholdSlider.getValue(),thresholdSliderIntensityLookup,
					(cellMask == null?true:cellMask[i] != 0))
						?0xFFFF:0);
//				if(i == 12035){
//					System.out.println("ROIAssistPanel.processROI cellmask at "+i+" = "+(cellMask == null?null:cellMask[i] != 0));
//					System.out.println("ROIAssistPanel.processROI val at "+i+" = "+(0x0000FFFF&shortPixels[i]));
//					System.out.println("ROIAssistPanel.processROI sourceData at "+i+" = "+(0x0000FFFF&roiSourceData[i]));
//					System.out.println("ROIAssistPanel.processROI threshold at "+i+" = "+thresholdSlider.getValue());
//				}

//				if(bInvert){
//					if(((int)roiSourceData[i]&0x000000FF) >= thresholdSlider.getValue()){
//						shortPixels[i] = 0;
//					}else{
////						if(/*bIgnoreMask || */(bBackground && (cellMask[i] == 0))){
//						if(bBackground){
//							shortPixels[i]|= (cellMask[i] == 0?0xFFFF:0);//0xFFFF;
//						}else if(cellMask[i] != 0){
//							shortPixels[i]|= 0xFFFF;
//						}else{
//							shortPixels[i] = 0;
//						}
//					}
////					else if((bBackground?cellMask[i] == 0:cellMask[i] != 0)){
////						shortPixels[i]|= 0xFFFF;
////					}
//				}else{
//					if(((int)roiSourceData[i]&0x000000FF) < thresholdSlider.getMaximum()-thresholdSlider.getValue()){
//						shortPixels[i] = 0;
//					}else{
//						shortPixels[i]|= 0xFFFF;
//					}
//				}
			}
			UShortImage ushortImage =
				new UShortImage(shortPixels,originalROI.getRoiImages()[0].getOrigin(),originalROI.getRoiImages()[0].getExtent(),
						originalROI.getISize().getX(),originalROI.getISize().getY(),originalROI.getISize().getZ());
			ROI newCellROI = new ROI(ushortImage,frapData.getCurrentlyDisplayedROI().getROIName());
			final ROI forceValidROI = forceROIValid(newCellROI);
			SwingUtilities.invokeAndWait(new Runnable(){public void run(){//}});
				frapData.addReplaceRoi(forceValidROI);
//				repaint();
			}});

		}catch(final Exception e2){
//			pp.stop();
			lastROISourceDataShort = null;
			e2.printStackTrace();
			SwingUtilities.invokeLater(new Runnable(){public void run(){//}});
				DialogUtils.showErrorDialog(VFrap_ROIAssistPanel.this, "Error setting new ROI. "+e2.getMessage());
			}});
		}
//		finally{
//			pp.stop();
//		}
		}}).start();
	
	}

	private boolean isSet(String roiName,short roiSourceDataUnsignedShort,int thresholdIndex,int[] thresholdLookupArr,boolean cellMask){
//		if((roiSourceDataUnsignedShort&0x0000FFFF) == 65535){
//			System.out.println();
//		}
		if(!roiName.equals(VFrap_ROISourceData.VFRAP_ROI_ENUM.ROI_CELL.name())){
			if(((int)(roiSourceDataUnsignedShort&0x0000FFFF)) >/*=*/ thresholdLookupArr[thresholdIndex]){
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
			if(((int)(roiSourceDataUnsignedShort&0x0000FFFF)) < thresholdLookupArr[thresholdLookupArr.length-1-thresholdIndex]){
				return false;//shortPixels[i] = 0;
			}else{
				return true;//shortPixels[i]|= 0xFFFF;
			}
		}

	}
	
	
	public void paint(Graphics g){
		super.paint(g);
//		if(lastROISourceDataShort == null){
//			return;
//		}
//		int[] histogram = new int[MAX_SCALE+1];
//		int min = lastROISourceDataShort[0]&0x0000FFFF;
//		int max = min;
//		for (int i = 0; i < lastROISourceDataShort.length; i++) {
//			int index = lastROISourceDataShort[i]&0x0000FFFF;
//			histogram[index]++;
//			min = Math.min(min,histogram[index]);
//			max = Math.max(max, histogram[index]);
//		}
//		
//		max = (int)Math.ceil(Math.sqrt(histogram[0]));
//		int[] sqrtHistogram = new int[histogram.length];
//		for (int i = 0; i < histogram.length; i++) {
//			sqrtHistogram[i] = (int)Math.ceil(Math.sqrt(histogram[i]));
//			max = Math.max(max, sqrtHistogram[i]);
//		}
//		histogram = sqrtHistogram;
//		
////		int accum = 0;		
////		for (int i = 0; i < histogram.length; i++) {
////			accum+= histogram[i];
////		}
////		max = accum/histogram.length;
//		
//		
////		int[] sortedHistogramArr = histogram.clone();
////		Arrays.sort(sortedHistogramArr);
////		max = sortedHistogramArr[sortedHistogramArr.length/2];
//		Point p = SwingUtilities.convertPoint(histogramPanel, 0, histogramPanel.getSize().height, this);
//		for (int i = 0; i < histogramPanel.getSize().getWidth(); i++) {
//			double indexf = (double)i/(histogramPanel.getSize().getWidth()-1);
//			int y = histogram[(int)(indexf*(histogram.length-1))];
//			y = (int)(((double)y/max)*histogramPanel.getSize().height);
//			if(y>histogramPanel.getSize().height){
//				y=histogramPanel.getSize().height;
//			}
//			g.drawLine(i, p.y, i, p.y-y);			
////			g.drawLine(p.x, p.y+histogramPanel.getSize().height, p.x, p.y+histogramPanel.getSize().height-256);			
//		}

	}
//	private void drawHistogram(byte[] roiSourceData){
//		int[] histogram = new int[256];
//		for (int i = 0; i < roiSourceData.length; i++) {
//			histogram[roiSourceData[i]&0x000000FF]++;
//		}
//		Graphics graphics = histogramPanel.getGraphics();
//		graphics.setColor(Color.black);
//		for (int i = 0; i < histogram.length; i++) {
//			graphics.drawLine(i, 0, i, 256);
//		}
//		graphics.dispose();
//	}

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
//		ROI validateROI = frapData.getCurrentlyDisplayedROI();
		if(validateROI.getNonzeroPixelsCount() < 1){
			return validateROI;
		}
		//
		//Remove pixels touching only at corners
		//
		short[] validatePixels = validateROI.getPixelsXYZ();
//		int XYSIZE = validateROI.getISize().getX()*validateROI.getISize().getY();
////		boolean bChanged;
////		do{
////			bChanged = false;
//			for (int z = 0; z < validateROI.getISize().getZ(); z++) {
//				int zOffset = z*XYSIZE;
//				for (int y = 0; y < validateROI.getISize().getY(); y++) {
//					int yoffset = y*validateROI.getISize().getX();
//					int zyOffset = zOffset+yoffset;
//					for (int x = 0; x < validateROI.getISize().getX(); x++) {
//						boolean bFoundNeighbor = false;
//						if(validatePixels[zyOffset+x] == 0){
//							bFoundNeighbor = true;
//							continue;
//						}
//						if((x-1) >= 0){
//							bFoundNeighbor = validatePixels[zyOffset+x-1] != 0;							
//						}
//						if(!bFoundNeighbor && (x+1) < validateROI.getISize().getX()){
//							bFoundNeighbor = validatePixels[zyOffset+x+1] != 0;
//						}
//						if(!bFoundNeighbor && (y-1) >= 0){
//							bFoundNeighbor = validatePixels[zyOffset+x-validateROI.getISize().getX()] != 0;
//						}
//						if(!bFoundNeighbor && (y+1) < validateROI.getISize().getY()){
//							bFoundNeighbor = validatePixels[zyOffset+x+validateROI.getISize().getX()] != 0;
//						}
//						if(!bFoundNeighbor && (z-1) >= 0){
//							bFoundNeighbor = validatePixels[zyOffset+x-XYSIZE] != 0;
//						}
//						if(!bFoundNeighbor && (z+1) < validateROI.getISize().getZ()){
//							bFoundNeighbor = validatePixels[zyOffset+x+XYSIZE] != 0;
//						}
//						if(!bFoundNeighbor){
//							validatePixels[zyOffset+x] = 0;
////							bChanged = true;
//						}
//					}
//				}
//			}
////		}while(bChanged);
			
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

//		}
//		else{//background
//			lastRegionInfos = null;
//			applyROIButton.setEnabled(true);
//			resolveROIButton.setEnabled(false);
//		}
		
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
//							System.out.println(j%frapData.getCurrentlyDisplayedROI().getISize().getX()+","+j/frapData.getCurrentlyDisplayedROI().getISize().getX());
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
//		if(lastRegionInfos == null){
//			throw new Exception("No regionInfo to resolve");
//		}
//			try{
//				final Vector<RegionInfo> roiRegionInfoV2 = new Vector<RegionInfo>();
//				for (int i = 0; i < lastRegionInfos.length; i++) {
//					if(lastRegionInfos[i].getPixelValue() == 1){
//						roiRegionInfoV2.add(lastRegionInfos[i]);
//					}
//				}
//				if(roiRegionInfoV2.size() <= 1){
//					throw new Exception("No regionInfo to resolve");
//				}
//
//				final RegionInfo[] regionInfoArr = roiRegionInfoV2.toArray(new RegionInfo[0]);
//				Arrays.sort(regionInfoArr,
//						new Comparator<RegionInfo>(){
//							public int compare(RegionInfo o1, RegionInfo o2) {
//								return o2.getNumPixels() - o1.getNumPixels();
//							}}
//				);
//				final Object[][] rowData = new Object[regionInfoArr.length][1];
//				for (int i = 0; i < regionInfoArr.length; i++) {
//					rowData[i][0] = regionInfoArr[i].getNumPixels()+" pixels";
//				}
//				
//				final ROI beforeROI = new ROI(frapData.getCurrentlyDisplayedROI());
//				final int[][] resultArr = new int[1][];
//				SwingUtilities.invokeAndWait(new Runnable(){public void run(){//}});
//					try{
////					resultArr[0] = /*DialogUtils.*/showComponentOKCancelTableList(null, "Select ROI to Keep",
////							new String[] {"ROI Size (pixel count)"}, rowData, ListSelectionModel.SINGLE_SELECTION);
//					resultArr[0] = /*DialogUtils.*/showComponentOKCancelTableList(null, "Select ROI to Keep",
//							new String[] {"ROI Size (pixel count)"}, rowData, ListSelectionModel.SINGLE_SELECTION,
//							regionInfoArr,beforeROI.getPixelsXYZ());
//					}catch(UserCancelException e){
//						resultArr[0] = null;
//					}
//				}});
//				if(resultArr[0] != null && resultArr[0].length > 0){
//					overlayEditorPanelJAI.waitCursor(true);
//					RegionInfo keepRegion = regionInfoArr[resultArr[0][0]];
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
//					lastROISourceDataShort = removePixels;
					SwingUtilities.invokeAndWait(new Runnable(){public void run(){//}});
						frapData.addReplaceRoi(newCellROI);
						applyROIButton.setEnabled(!hasInternalVoids);
						resolveROIButton.setEnabled(false);
						fillVoidsButton.setEnabled(hasInternalVoids);
					}});
//				}
//				else{
//					SwingUtilities.invokeAndWait(new Runnable(){public void run(){//}});
//						frapData.addReplaceRoi(beforeROI);
//						applyROIButton.setEnabled(false);
//						resolveROIButton.setEnabled(true);
//						fillVoidsButton.setEnabled(false);
//					}});
//
//				}
//			}catch(UserCancelException e){
//				//ignore
//			}
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
		
		ScopedExpressionTableCellRenderer.formatTableCellSizes(table, null, null);
		
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
//	public static void setCursorThroughout(Container container, Cursor cursor) {
//		if (container==null){
//			return;
//		}
//		Component[] components = container.getComponents();
//		for (int i=0;i<components.length;i++) {
//			System.out.println(components[i].getClass().getName());
//			if(components[i] instanceof JRootPane){
//				System.out.println();
//			}else{
//				components[i].setCursor(cursor);
//			}
//			if(components[i] instanceof JRootPane){
//				ROIAssistPanel.setCursorThroughout(((JRootPane)components[i]).getContentPane(), cursor);
//			}else if (components[i] instanceof Container) {
//				if (((Container)components[i]).getComponentCount() > 0) {
//					ROIAssistPanel.setCursorThroughout((Container)components[i], cursor);
//				}
//			}
//		}
//	}
}


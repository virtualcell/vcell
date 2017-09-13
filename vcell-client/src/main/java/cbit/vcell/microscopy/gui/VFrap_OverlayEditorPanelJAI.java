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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

import org.vcell.util.BeanUtils;
import org.vcell.util.ISize;
import org.vcell.util.NumberUtils;
import org.vcell.util.Range;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.vcellij.ImageDatasetReaderService;

import cbit.vcell.VirtualMicroscopy.Image.ImageStatistics;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.client.UserMessage;
import cbit.vcell.geometry.gui.OverlayEditorPanelJAI.BrushToolHelper;
import cbit.vcell.graph.gui.ZoomShapeIcon;
import cbit.vcell.microscopy.VFrap_ROISourceData;
//comments added Jan 2008, this is the panel that displayed at the top of the FRAPDataPanel which deals with serials of images.
/**
 */
public class VFrap_OverlayEditorPanelJAI extends JPanel {

	
	private static final long serialVersionUID = 1L;
	private static final double SHORT_TO_BYTE_FACTOR = 256;
	
	public static final String INITIAL_BLEACH_AREA_TEXT = "Initial Bleach Area";
	public static final String WHOLE_CELL_AREA_TEXT = "Whole Cell Area";
	public static final String ROI_ASSIST_TEXT = "ROI Assist";
	//properties
	public static final String FRAP_DATA_CROP_PROPERTY = "FRAP_DATA_CROP_PROPERTY";
	public static final String FRAP_DATA_TIMEPLOTROI_PROPERTY = "FRAP_DATA_TIMEPLOTROI_PROPERTY";
	public static final String FRAP_DATA_CURRENTROI_PROPERTY = "FRAP_DATA_CURRENTROI_PROPERTY";
	public static final String FRAP_DATA_UNDOROI_PROPERTY = "FRAP_DATA_UNDOROI_PROPERTY";
	//used for new frap
	public static final int DISPLAY_WITH_ROIS = 0;
	public static final int DEFINE_CROP = 1;
	public static final int DEFINE_CELLROI = 2;
	public static final int DEFINE_BLEACHEDROI = 3;
	public static final int DEFINE_BACKGROUNDROI = 4;
	public static final int SHOW_RIO_ASSISTANT = 5;
	public static final int HIDE_ROI_ASSISTANT = 6;
	private VFrap_ROISourceData roiSourceData = null;
	//scale factors
	public static final double DEFAULT_SCALE_FACTOR = 1.0;
	public static final double DEFAULT_OFFSET_FACTOR = 0.0;
	private double originalScaleFactor = DEFAULT_SCALE_FACTOR;
	private double originalOffsetFactor = DEFAULT_OFFSET_FACTOR;
	private ISize originalISize;
	//editable mode, useful for listing buttons in one or two columns,
	//for adding roiAssitPanel or not.
	private boolean isEditable = true;
	//panel components
	private JComboBox roiComboBox;
	private JButton contrastButtonMinus;
	private JButton contrastButtonPlus;
	private JToggleButton cropButton;
	private JToggleButton autoCropButton;
	private JToggleButton fillButton;
	private JToggleButton eraseButton;
	private JToggleButton paintButton;
	private JButton importROIMaskButton;
	private JButton clearROIbutton;
	private JButton roiTimePlotButton;
	private JButton roiAssistButton;
	private VFrap_OverlayImageDisplayJAI imagePane = null;
	private JSlider timeSlider = null;
	private ImageDataset imageDataset = null;
	private ROI roi = null;
	private StringBuffer sb = new StringBuffer();
	private JScrollPane jScrollPane2 = null;
	private JPanel rightPanel = null;
	private JButton zoomInButton = null;
	private JButton zoomOutButton = null;
	private Color highlightColor = Color.yellow.darker();
	private JSlider zSlider = null;
	private ButtonGroup roiDrawButtonGroup = new ButtonGroup();
	private Point lastHighlightPoint = null;
	private JLabel textLabel = null;
	private JPanel editROITopPanel = null;
	private JPanel editROIPanel = null;
	private JLabel viewTLabel;
	private JLabel viewZLabel;
	private JLabel editRoiLabel;
	private VFrap_ROIAssistPanel roiAssistPanel = null;
	private JPanel roiAssistBasePanel = null;
	//variables for undo function	
	UndoableEditSupport undoableEditSupport;
	ROI undoableROI;
		
	private Hashtable<String, Cursor> cursorsForROIsHash = null;
	
	public interface CustomROIImport {
		public boolean importROI(File importROIFile) throws Exception;
	};
	private CustomROIImport customROIImport;
	private JFileChooser openJFileChooser = new JFileChooser();
	private Range minmaxPixelValues = null;
	//variable used to avoid unnecessary firing of the combobox action event
	private String roiName;
	
	//ROI comboBox action
	ActionListener ROI_COMBOBOX_ACTIONLISTENER =
		new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
//				delROIButton.setEnabled(((ComboboxROIName)roiComboBox.getSelectedItem()).isEditable());				
				String selectedName = ((ComboboxROIName)roiComboBox.getSelectedItem()).getROIName();
				if (!selectedName.equals(roiName)) {					
					firePropertyChange(FRAP_DATA_CURRENTROI_PROPERTY, null,((ComboboxROIName)roiComboBox.getSelectedItem()).getROIName());
					roiName = selectedName;
				}
			}
		};
	private class ComboboxROIName {
		private String roiName;
		public ComboboxROIName(String roiName,boolean bEdit){
			this.roiName = roiName;
		}
		public String getROIName(){
			return roiName;
		}
		public String toString(){
			return getROIName();
		}
	}
	@SuppressWarnings("serial")
	public static final UndoableEdit CLEAR_UNDOABLE_EDIT =
		new AbstractUndoableEdit(){
			public boolean canUndo() {
				return false;
			}
			public String getUndoPresentationName() {
				return null;
			}
			public void undo() throws CannotUndoException {
				super.undo();
			}
		};

	/**
	 * This is the default constructor
	 */
	public VFrap_OverlayEditorPanelJAI(boolean isEditable) {
		super();
		this.isEditable = isEditable;
		initialize();
	}

	public void setUndoableEditSupport(UndoableEditSupport undoableEditSupport){
		this.undoableEditSupport = undoableEditSupport;
	}
	
	private boolean isAutoCroppable(){
		try {
			Rectangle cropRectangle = null;
			if(imageDataset != null){
				cropRectangle = imageDataset.getNonzeroBoundingRectangle();
			}
			else
			{
				return false;
			}
			if(cropRectangle != null &&
				cropRectangle.x == 0 && cropRectangle.y == 0 &&
				cropRectangle.width == imageDataset.getISize().getX() &&
				cropRectangle.height == imageDataset.getISize().getY()){
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	private void waitCursor(boolean bOn){
		Container topLevelContainer = BeanUtils.findTypeParentOfComponent(this, JFrame.class);
		BeanUtils.setCursorThroughout(topLevelContainer,
				(bOn?Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR):Cursor.getDefaultCursor()));
		if(!bOn){
			updateROICursor();
		}
	}
	public void updateROICursor(){
		getImagePane().setCursor(getROICursor());
	}
	private Cursor getROICursor(){
		if(roi == null || cursorsForROIsHash == null || cursorsForROIsHash.get(roi.getROIName()) == null){
			return Cursor.getDefaultCursor();
		}
		return cursorsForROIsHash.get(roi.getROIName());
	}
	
	public void setCursorsForROIs(Hashtable<String, Cursor> cursorsForROIsHash){
		this.cursorsForROIsHash = cursorsForROIsHash;
	}
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,7,0};
		//the top panel is the container for both image editor panel and roi assist panel
		JPanel topPanel = new JPanel(gridBagLayout);
				
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints2.anchor = GridBagConstraints.NORTH;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.gridx = 2;
		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		gridBagConstraints12.gridwidth = 2;
		gridBagConstraints12.ipadx = 570;
		gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints12.fill = GridBagConstraints.BOTH;
		gridBagConstraints12.weighty = 1.0;
		gridBagConstraints12.gridx = 0;
		gridBagConstraints12.gridy = 1;
		gridBagConstraints12.weightx = 1;

		editROIPanel = new JPanel();
		final GridBagLayout gridBagLayout_2 = new GridBagLayout();
		gridBagLayout_2.rowHeights = new int[] {0,0,7};
		gridBagLayout_2.columnWidths = new int[] {0,7};
		editROIPanel.setLayout(gridBagLayout_2);
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.anchor = GridBagConstraints.WEST;
		gridBagConstraints_6.insets = new Insets(8, 2, 0, 0);
		gridBagConstraints_6.weightx = 0;
		gridBagConstraints_6.gridy = 0;
		gridBagConstraints_6.gridx = 1;

		final JLabel infoLabel = new JLabel();
		infoLabel.setText("Data Info:");
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.insets = new Insets(0, 0, 0, 4);
		gridBagConstraints_12.anchor = GridBagConstraints.EAST;
		gridBagConstraints_12.gridy = 0;
		gridBagConstraints_12.gridx = 0;
		editROIPanel.add(infoLabel, gridBagConstraints_12);

		textLabel = new JLabel();
		textLabel.setPreferredSize(new Dimension(500, 20));
		textLabel.setMinimumSize(new Dimension(500, 20));
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.weightx = 1;
		gridBagConstraints_2.insets = new Insets(0, 2, 0, 0);
		gridBagConstraints_2.anchor = GridBagConstraints.WEST;
		gridBagConstraints_2.gridy = 0;
		gridBagConstraints_2.gridx = 1;
		textLabel.setText("No FRAP DataSet loaded.");
		editROIPanel.add(textLabel, gridBagConstraints_2);
		

		editROITopPanel = new JPanel();
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 1, 0, 0);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 1;
		final GridBagLayout gridBagLayout_3 = new GridBagLayout();
		gridBagLayout_3.columnWidths = new int[] {0,7,7,0,7};
		editROITopPanel.setLayout(gridBagLayout_3);
		editROIPanel.add(editROITopPanel, gridBagConstraints);

		viewZLabel = new JLabel();
		viewZLabel.setText("View Z:");
		final GridBagConstraints gridBagConstraints_17 = new GridBagConstraints();
		gridBagConstraints_17.insets = new Insets(0, 0, 0, 4);
		gridBagConstraints_17.anchor = GridBagConstraints.EAST;
		gridBagConstraints_17.gridy = 2;
		gridBagConstraints_17.gridx = 0;
		editROIPanel.add(viewZLabel, gridBagConstraints_17);

		final JPanel ZSliderPanel = new JPanel();
		final GridBagLayout gridBagLayout_4 = new GridBagLayout();
		gridBagLayout_4.columnWidths = new int[] {7,0};
		ZSliderPanel.setLayout(gridBagLayout_4);
		final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
		gridBagConstraints_18.insets = new Insets(0, 2, 0, 0);
		gridBagConstraints_18.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_18.weightx = 0;
		gridBagConstraints_18.gridy = 2;
		gridBagConstraints_18.gridx = 1;
		editROIPanel.add(ZSliderPanel, gridBagConstraints_18);
		final GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_19.anchor = GridBagConstraints.WEST;
		gridBagConstraints_19.weightx = 1;
		gridBagConstraints_19.gridy = 0;
		gridBagConstraints_19.gridx = 0;
		ZSliderPanel.add(getZSlider(), gridBagConstraints_19);

		viewTLabel = new JLabel();
		viewTLabel.setText("View Time:");
		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.insets = new Insets(0, 0, 0, 4);
		gridBagConstraints_13.anchor = GridBagConstraints.EAST;
		gridBagConstraints_13.gridy = 3;
		gridBagConstraints_13.gridx = 0;
		editROIPanel.add(viewTLabel, gridBagConstraints_13);

		final JPanel timeSliderPanel = new JPanel();
		timeSliderPanel.setLayout(new GridBagLayout());
		final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_15.weightx = 1;
		gridBagConstraints_15.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_15.anchor = GridBagConstraints.WEST;
		gridBagConstraints_15.gridy = 0;
		gridBagConstraints_15.gridx = 0;
		timeSliderPanel.add(getTimeSlider(), gridBagConstraints_15);
		
		final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_14.insets = new Insets(0, 2, 0, 0);
		gridBagConstraints_14.anchor = GridBagConstraints.WEST;
		gridBagConstraints_14.gridy = 3;
		gridBagConstraints_14.gridx = 1;
		editROIPanel.add(timeSliderPanel, gridBagConstraints_14);

		editRoiLabel = new JLabel();
		editRoiLabel.setText("Active ROI:");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.insets = new Insets(0, 0, 0, 4);
		gridBagConstraints_7.anchor = GridBagConstraints.EAST;
		gridBagConstraints_7.gridy = 4;
		gridBagConstraints_7.gridx = 0;
		editROIPanel.add(editRoiLabel, gridBagConstraints_7);

		final JPanel editROIButtonPanel = new JPanel();
		final GridBagLayout gridBagLayout_5 = new GridBagLayout();
		gridBagLayout_5.columnWidths = new int[] {0,7,7};
		editROIButtonPanel.setLayout(gridBagLayout_5);
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.weightx = 0;
		gridBagConstraints_8.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_8.insets = new Insets(0, 2, 0, 0);
		gridBagConstraints_8.anchor = GridBagConstraints.WEST;
		gridBagConstraints_8.gridy = 4;
		gridBagConstraints_8.gridx = 1;
		roiComboBox = new JComboBox();
		roiComboBox.addActionListener(ROI_COMBOBOX_ACTIONLISTENER);
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_1.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_1.weightx = 1;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.gridx = 0;
		editROIButtonPanel.add(roiComboBox, gridBagConstraints_1);
		editROIPanel.add(editROIButtonPanel, gridBagConstraints_8);
		
		topPanel.add(editROIPanel, gridBagConstraints_6);
		topPanel.add(getRightPanel(), gridBagConstraints2);
		topPanel.add(getJScrollPane2(), gridBagConstraints12);
		topPanel.setBorder(new TitledBorder(new EtchedBorder(), ""));
				
		if(isEditable)//in ROI wizard
		{
	        this.add(topPanel, BorderLayout.CENTER);
	        this.add(getROIAssistBasePanel(), BorderLayout.SOUTH);
	        setROIAssistVisible(false);
		}
		else // in main frame
		{
			this.add(topPanel, BorderLayout.CENTER);
		}
		
		roiDrawButtonGroup.add(paintButton);
		roiDrawButtonGroup.add(eraseButton);
		roiDrawButtonGroup.add(fillButton);
		roiDrawButtonGroup.add(cropButton);
		
		BeanUtils.enableComponents(getRightPanel(), false);
		BeanUtils.enableComponents(editROIPanel, false);
	}
	
	public void showROIAssist()
	{
		//before showing ROI assistant, check if ROI is defined. If yes, ask user if they want to override previous defined ROI. Since ROI assistant always
		//shows up with automatica detected region of interest regardless of whatever is defined, users may lose defined ROI by accidentally clicking the ROI assistant.
		if(getRoiSouceData().getCurrentlyDisplayedROI() !=  null && getRoiSouceData().getCurrentlyDisplayedROI().getNonzeroPixelsCount() > 0)
		{
			String choice = DialogUtils.showWarningDialog(this, "Invoking the ROI assistant will override already defined '" + getRoiSouceData().getCurrentlyDisplayedROI().getROIName() + "'. Do you want to continue?",
					                                      new String[]{UserMessage.OPTION_CONTINUE, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_CANCEL);
			if(choice.equals(UserMessage.OPTION_CANCEL))
			{
				return;
			}
		}
		//only check when doing roiAssist for bleached, this is actually used by VFRAP. (VCell has Cell ROI only)
		if(getRoiSouceData().getCurrentlyDisplayedROI().getROIName().equals(VFrap_ROISourceData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()) &&
				getRoiSouceData().getRoi(VFrap_ROISourceData.VFRAP_ROI_ENUM.ROI_CELL.name()).getNonzeroPixelsCount()<1)
		{
			DialogUtils.showInfoDialog(VFrap_OverlayEditorPanelJAI.this,"Cell ROI must be defined before using ROI Assist Tool to create Bleached ROI.");
			return;
		}
		ROI originalROI = null;
		try{
			originalROI = new ROI(roi);
		}catch(Exception ex){
			ex.printStackTrace();
			//can't happen
		}
		getROIAssistPanel().init(originalROI, getRoiSouceData(),VFrap_OverlayEditorPanelJAI.this);
		setROIAssistVisible(true);
		adjustComponentsForVFRAP(SHOW_RIO_ASSISTANT);
	}
	
	public void setROIAssistVisible(boolean isVisible)
	{
		getROIAssistPanel().setVisible(isVisible);
		if(isVisible)
		{
			getRoiAssistButton().setIcon(new ImageIcon(getClass().getResource("/images/roiAssistOpen.gif")));
		}
		else
		{
			getRoiAssistButton().setIcon(new ImageIcon(getClass().getResource("/images/roiAssistClose.gif")));
		}
	}
	
	public boolean isROIAssistVisible()
	{
		return getROIAssistPanel().isVisible();
	}
	
	public VFrap_ROIAssistPanel getROIAssistPanel()
	{
		if(roiAssistPanel == null)
		{
			roiAssistPanel = new VFrap_ROIAssistPanel();
		}
		return roiAssistPanel;
	}
	
	public JPanel getROIAssistBasePanel()
	{
		if(roiAssistBasePanel == null)
		{
			roiAssistBasePanel = new JPanel(new BorderLayout());
			JPanel buttonPanel = new JPanel(new BorderLayout());
			buttonPanel.add(getRoiAssistButton(), BorderLayout.WEST);
			roiAssistBasePanel.add(buttonPanel, BorderLayout.NORTH);
			roiAssistBasePanel.add(getROIAssistPanel(),BorderLayout.CENTER);
			//set border
			TitledBorder tb = new TitledBorder(new EtchedBorder(), "");
			roiAssistBasePanel.setBorder(tb);
		}
		return roiAssistBasePanel;
	}
	
	public void adjustComponentsForVFRAP(int choice)
	{
		if(choice == DISPLAY_WITH_ROIS)
		{
			setAllComponentsVisible();
			roiDrawButtonGroup.remove(paintButton);
			roiDrawButtonGroup.remove(eraseButton);
			roiDrawButtonGroup.remove(fillButton);
			roiDrawButtonGroup.remove(cropButton);
			
			getCropButton().setVisible(false);
			getAutoCropButton().setVisible(false);
			getPaintButton().setVisible(false);
			getPaintButton().setSelected(false);
			getEraseButton().setVisible(false);
			getFillButton().setVisible(false);
			getImportROIMaskButton().setVisible(false);
			getClearROIbutton().setVisible(false);
			getRoiTimePlotButton().setVisible(false);
			editRoiLabel.setEnabled(true);

		}
		else if(choice == DEFINE_CROP)
		{
			setAllComponentsVisible();

			setDrawingToolButtonEnabled(false);
			getCropButton().setEnabled(true);
			getCropButton().setSelected(true);//paint button will not be selected
			getAutoCropButton().setEnabled(isAutoCroppable());
			getPaintButton().setSelected(false);
			//other components
			getRoiAssistButton().setEnabled(false);
			roiComboBox.setVisible(false);
			roiComboBox.setEnabled(false);
			editRoiLabel.setVisible(false);
		}
		else if(choice == DEFINE_CELLROI || choice == DEFINE_BLEACHEDROI || choice == DEFINE_BACKGROUNDROI)
		{
			setAllComponentsVisible();
			setDrawingToolButtonEnabled(true);			
			getCropButton().setEnabled(false);
			getAutoCropButton().setEnabled(false);
			//other components
			getRoiAssistButton().setEnabled(true);
			roiComboBox.setVisible(false);
			roiComboBox.setEnabled(false);
			editRoiLabel.setVisible(false);
		}
		else if(choice == SHOW_RIO_ASSISTANT)
		{
			setDrawingToolButtonEnabled(false);
			getCropButton().setEnabled(false);
			getAutoCropButton().setEnabled(false);
		}
		else if(choice == HIDE_ROI_ASSISTANT)
		{
			setDrawingToolButtonEnabled(true);
			getCropButton().setEnabled(false);
			getAutoCropButton().setEnabled(false);
		}
	}
	
	private void setDrawingToolButtonEnabled(boolean bEnabled)
	{
		getPaintButton().setEnabled(bEnabled);
		getPaintButton().setEnabled(bEnabled);
		getEraseButton().setEnabled(bEnabled);
		getFillButton().setEnabled(bEnabled);
		getImportROIMaskButton().setEnabled(bEnabled);
		getClearROIbutton().setEnabled(bEnabled);
		getRoiTimePlotButton().setEnabled(bEnabled);
	}
	
	protected void setAllComponentsVisible()
	{
		BeanUtils.enableComponents(getRightPanel(), true);
		BeanUtils.enableComponents(editROIPanel, true);
		//buttons
		getCropButton().setVisible(true);
		getAutoCropButton().setVisible(true);
		getPaintButton().setVisible(true);
		getPaintButton().setSelected(true);
		getEraseButton().setVisible(true);
		getFillButton().setVisible(true);
		getImportROIMaskButton().setVisible(true);
		getClearROIbutton().setVisible(true);
		getRoiTimePlotButton().setVisible(true);
		//other components
		roiComboBox.setVisible(true);
		editRoiLabel.setVisible(true);
		
		roiDrawButtonGroup.add(getPaintButton());
		roiDrawButtonGroup.add(getEraseButton());
		roiDrawButtonGroup.add(getFillButton());
		roiDrawButtonGroup.add(getCropButton());
	}
	
	private void clearROI(){
		saveUndoableROI();
		if (roi!=null){
			short[] roiPixels = roi.getRoiImages()[getRoiImageIndex()].getPixels();
			for (int i = 0; i < roiPixels.length; i++) {
				roiPixels[i] = 0;
			}
			refreshROI();
		}
		fireUndoableEditROI(EDITTYPE_CLEAR);
	}
	
	private void refreshROI(){
		if (roi!=null && imageDataset != null){
			getImagePane().setHighlightImageAndWritebackBuffer(
				createHighlightImageFromROI(),
				roi.getRoiImages()[getRoiImageIndex()].getPixels());
		}else{
			getImagePane().setHighlightImageAndWritebackBuffer(null,null);
		}
	}

	public ROI getROI(){
		return roi;
	}
	
	/**
	 * Method setROI.
	 * @param argROI ROI
	 */
	public void setROI(ROI argROI)
	{
		if (argROI != null /*&& roi != argROI*/ /*&& (this.isShowing() || roi == null)*/)
		{
			roi = argROI;
			roiName = roi.getROIName();
			
			refreshROI();
			if(roi != null){
				for (int i = 0; i < roiComboBox.getItemCount(); i++) {
					if(((ComboboxROIName)roiComboBox.getItemAt(i)).getROIName().equals(roi.getROIName())){
						roiComboBox.setSelectedIndex(i);
						break;
					}
				}
			}
			updateROICursor();
		}
	}
	
	public void setTimeIndex(int timeIndex){
		if(timeSlider.getValue() == timeIndex){
			forceImage();
		}else{
			timeSlider.setValue(timeIndex);
		}
	}
	
	public void saveUserChangesToROI(){
		short[] roiPixels = getImagePane().getHighlightImageWritebackImageBuffer();
		if (roiPixels!=null){
			BufferedImage highlightImage = imagePane.getHighlightImage();
			byte[] hiLiteArr = ((DataBufferByte)highlightImage.getRaster().getDataBuffer()).getData();
			for (int i = 0; i < roiPixels.length; i++) {
				roiPixels[i] = (hiLiteArr[i] == 0?0:(short)0xffff);
			}
		}
	}
	
	/**
	 * Method createUnderlyingImage.
	 * @param image UShortImage
	 * @return BufferedImage
	 */
	private BufferedImage createUnderlyingImage(UShortImage image){
		int[] cmap = new int[256];
		//colormap (grayscale)
		for(int i=0;i<256;i+= 1){
			int iv = (0x000000FF&i);
			cmap[i] = 0xFF000000 | iv<<16 | iv<<8 | i;
			
		}
		IndexColorModel indexColorModel =
			new java.awt.image.IndexColorModel(
				8, cmap.length,cmap,0,
				false /*false means NOT USE alpha*/   ,
				-1/*NO transparent single pixel*/,
				java.awt.image.DataBuffer.TYPE_BYTE);

		int width = image.getNumX();
		int height = image.getNumY();
		ImageStatistics imageStats = image.getImageStatistics();
		short[] pixels = image.getPixels();
		BufferedImage underImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED, indexColorModel);
		byte[] byteData = ((DataBufferByte)underImage.getRaster().getDataBuffer()).getData();
		for (int i = 0; i < byteData.length; i++) {
			byteData[i] = (imageStats.maxValue < SHORT_TO_BYTE_FACTOR?(byte)pixels[i]:(byte)(((pixels[i]&0x0000FFFF))/SHORT_TO_BYTE_FACTOR));
		}
		
		return underImage;
	}
	
	public void displaySpecialData(short[] specialData,int width, int height) throws Exception{
		if(specialData == null){
			forceImage();
			return;
		}
		UShortImage specialUShortImage = new UShortImage(specialData,null,null,width,height,1);
		BufferedImage specialBufferedImage = createUnderlyingImage(specialUShortImage);
		imagePane.setUnderlyingImage(specialBufferedImage, false,null);
	}
	/**
	 * Method createHighlightImageFromROI.
	 * @return BufferedImage
	 */
	private BufferedImage createHighlightImageFromROI(){
		int[] cmap = new int[256];
		//colormap (grayscale)
		for(int i=0;i<256;i+= 1){
			if(i != 0){
				cmap[i] = 0xFF000000 | highlightColor.getRGB();
			}else{
				cmap[1] = 0xFF000000;
			}
		}
		IndexColorModel indexColorModel =
			new java.awt.image.IndexColorModel(
				8, cmap.length,cmap,0,
				false /*false means NOT USE alpha*/   ,
				-1/*NO transparent single pixel*/,
				java.awt.image.DataBuffer.TYPE_BYTE);

		UShortImage roiImage = roi.getRoiImages()[getRoiImageIndex()];
		int width = roiImage.getNumX();
		int height = roiImage.getNumY();
		BufferedImage hiLiteImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED, indexColorModel);
		byte[] hiLiteArr = ((DataBufferByte)hiLiteImage.getRaster().getDataBuffer()).getData();
		for (int i = 0; i < roiImage.getPixels().length; i++) {
			hiLiteArr[i] = (roiImage.getPixels()[i] != 0?(byte)highlightColor.getRed():0);
		}
		return hiLiteImage;
	}
	
	public void setROITimePlotVisible(boolean bROITimePlotVisible){
		roiTimePlotButton.setVisible(bROITimePlotVisible);
	}

	public void addROIName(String roiName,boolean isEditable,String selectROIName){
		try{
			roiComboBox.removeActionListener(ROI_COMBOBOX_ACTIONLISTENER);
			roiComboBox.addItem(new ComboboxROIName(roiName,isEditable));
			for (int i = 0; i < roiComboBox.getItemCount(); i++) {
				if(((ComboboxROIName)roiComboBox.getItemAt(i)).getROIName().equals(selectROIName)){
					roiComboBox.setSelectedIndex(i);
					break;
				}
			}

		}finally{
			roiComboBox.addActionListener(ROI_COMBOBOX_ACTIONLISTENER);
			ROI_COMBOBOX_ACTIONLISTENER.actionPerformed(new ActionEvent(roiComboBox,0,roiComboBox.getSelectedItem().toString()));
		}
	}
	/** Sets the viewer to display the given images. * @param argImageDataset ImageDataset
	 */
	public void setImages(ImageDataset argImageDataset,boolean bNew/*,double originalScaleFactor,double originalOffsetFactor*/) {
		imageDataset = argImageDataset;
		BufferedImage underlyingImage = null;
		if (imageDataset!=null){
//			this.originalScaleFactor = originalScaleFactor;
//			this.originalOffsetFactor = originalOffsetFactor;
			originalISize = (bNew?imageDataset.getISize():originalISize);
			if(!timeSlider.isEnabled()) //if the component is already enabled, don't do anything
			{
				BeanUtils.enableComponents(rightPanel, true);
				BeanUtils.enableComponents(editROITopPanel, true);
				BeanUtils.enableComponents(editROIPanel, true);
			}
			timeSlider.setVisible(imageDataset.getSizeT() > 1);
			viewTLabel.setVisible(timeSlider.isVisible());
			int currTimeSliderValue = timeSlider.getValue();
			if(imageDataset.getSizeT() > 1){
//				timeSlider.setLabelTable(timeSlider.createStandardLabels(imageDataset.getSizeT()-1,1));
				Hashtable<Integer, JComponent> labeltable = new Hashtable<Integer, JComponent>();
				labeltable.put(1, new JLabel(imageDataset.getImageTimeStamps()[0]+""));
				labeltable.put(imageDataset.getSizeT(),
					new JLabel(NumberUtils.formatNumber(imageDataset.getImageTimeStamps()[imageDataset.getSizeT()-1])));
				timeSlider.setLabelTable(labeltable);
				timeSlider.setMaximum(imageDataset.getSizeT());
				//Added because setMaximum SOMETIMES resets the label table
				timeSlider.setLabelTable(labeltable);
			}
			timeSlider.setValue(Math.max(1,Math.min(imageDataset.getSizeT(),currTimeSliderValue)));
			timeSlider.setMajorTickSpacing(imageDataset.getSizeT());
			timeSlider.setEnabled(imageDataset.getSizeT() > 1);
			
			zSlider.setVisible(imageDataset.getSizeZ() > 1);
			viewZLabel.setVisible(zSlider.isVisible());
			if(imageDataset.getSizeZ() > 1){
				zSlider.setLabelTable(zSlider.createStandardLabels(imageDataset.getSizeZ()-1,1));
			}
			int currZSliderValue = timeSlider.getValue();
			zSlider.setValue(Math.max(1,Math.min(imageDataset.getSizeZ(),currZSliderValue)));
			zSlider.setMaximum(imageDataset.getSizeZ());
			zSlider.setMajorTickSpacing(imageDataset.getSizeZ());
			zSlider.setEnabled(imageDataset.getSizeZ() > 1);
			
			minmaxPixelValues = calcMinMaxPixelValueRange(imageDataset);
			underlyingImage = createUnderlyingImage(imageDataset.getImage((zSlider.getValue()-1),0,(timeSlider.getValue()-1)));
			autoCropButton.setEnabled(isAutoCroppable());
		}else{
			minmaxPixelValues = null;
			this.originalScaleFactor = DEFAULT_SCALE_FACTOR;
			this.originalOffsetFactor = DEFAULT_OFFSET_FACTOR;
			originalISize = null;
			timeSlider.setValue(1);
			timeSlider.setMaximum(1);
			timeSlider.setLabelTable(null);
			timeSlider.setEnabled(false);
			zSlider.setValue(1);
			zSlider.setMaximum(1);
			zSlider.setLabelTable(null);
			zSlider.setEnabled(false);
			BeanUtils.enableComponents(rightPanel, false);
			BeanUtils.enableComponents(editROITopPanel, false);
			BeanUtils.enableComponents(editROIPanel, false);
			underlyingImage = null;
			setROI(null);
		}

		updateLabel(-1, -1);
		getImagePane().setUnderlyingImage(underlyingImage,bNew,minmaxPixelValues);
		if(imageDataset != null && bNew){
			contrastButtonPlus.doClick();
		}
	}

	private Range calcMinMaxPixelValueRange(ImageDataset argImageDataset){
		UShortImage[] allImages = argImageDataset.getAllImages();
		double min = 0;
		double max = min;
		for (int i = 0; i < allImages.length; i++) {
			ImageStatistics imageStats = allImages[i].getImageStatistics();
			if(i==0 || imageStats.minValue < min){min = imageStats.minValue;}
			if(i==0 || imageStats.maxValue > max){max = imageStats.maxValue;}
		}
		if(max < SHORT_TO_BYTE_FACTOR){
			return new Range(min,max);
		}
		return new Range(min/SHORT_TO_BYTE_FACTOR,max/SHORT_TO_BYTE_FACTOR);
	}
	/** Gets the currently displayed image. * @return BufferedImage
	 */
	private BufferedImage getImage() {
		if(imageDataset == null){
			return null;
		}
		int ndx = getImageIndex();
		if (imageDataset == null || ndx >= imageDataset.getAllImages().length){
			return null;
		}
		UShortImage image = imageDataset.getAllImages()[ndx];
		return createUnderlyingImage(image);
	}

	private void saveUndoableROI(){
		if(roi == null){
			return;
		}
//		saveUserChangesToROI();
		try{
			undoableROI = new ROI(roi);
		}catch(Exception e2){
			e2.printStackTrace();
			//can't happen
		}
	}
	private static final String EDITTYPE_FILL = "fill";
	private static final String EDITTYPE_PAINT = "paint";
	private static final String EDITTYPE_ERASE = "erase";
	private static final String EDITTYPE_CLEAR = "clear";
	@SuppressWarnings("serial")
	private void fireUndoableEditROI(final String editType){
		if(undoableROI == null){
			return;
		}
		final ROI originalROI = undoableROI;
		undoableROI = null;
		if(editType != null && undoableEditSupport != null){
			undoableEditSupport.postEdit(
				new AbstractUndoableEdit(){
					public boolean canUndo() {
						return true;
					}
					public String getUndoPresentationName() {
						return editType+" "+originalROI.getROIName();
					}
					public void undo() throws CannotUndoException {
						super.undo();
						firePropertyChange(FRAP_DATA_UNDOROI_PROPERTY, null,originalROI);
					}
				}
			);			
		}else{
			if(undoableEditSupport != null)
			{
				undoableEditSupport.postEdit(VFrap_OverlayEditorPanelJAI.CLEAR_UNDOABLE_EDIT);
			}
		}
		
	}
	/**
	 * Method getImagePane.
	 * @return OverlayImageDisplayJAI
	 */
	private VFrap_OverlayImageDisplayJAI getImagePane(){
		if (imagePane == null){
			imagePane = new VFrap_OverlayImageDisplayJAI();
			//imagePane = new ZoomableOverlayImagePane();
			imagePane.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseExited(MouseEvent e){
					imagePane.setBrush(null);
					imagePane.refreshImage();
					System.out.println(e);
					updateLabel(-1, -1);
				}
				public void mousePressed(MouseEvent e){
					updateLabel(e.getX(), e.getY());
					lastHighlightPoint = e.getPoint();
					saveUndoableROI();
					if(SwingUtilities.isLeftMouseButton(e) && (paintButton.isSelected() || eraseButton.isSelected()) && !brushToolHelper.isBrushSizeClickDragMode()){
						getImagePane().setBrush(null);
						drawHighlight(e.getX(), e.getY(), brushToolHelper.getBrushSize(getImagePane().getZoom()), eraseButton.isSelected());
					}else if(SwingUtilities.isLeftMouseButton(e) && (paintButton.isSelected() || eraseButton.isSelected()) && brushToolHelper.isBrushSizeClickDragMode()){
						brushToolHelper.startBrushSizeClickDragMode(e);
					}
				}
				public void mouseReleased(MouseEvent e){

					if(SwingUtilities.isLeftMouseButton(e) && (paintButton.isSelected() || eraseButton.isSelected()) && !brushToolHelper.isBrushSizeClickDragMode()){
						getImagePane().setBrush(brushToolHelper.getBrushShape(e));
						if(paintButton.isSelected()){ fireUndoableEditROI(EDITTYPE_PAINT);}
						else if(eraseButton.isSelected()){fireUndoableEditROI(EDITTYPE_ERASE);}
					}else if(fillButton.isSelected()){
						//done later in 'click'
					}else if(SwingUtilities.isLeftMouseButton(e) && (paintButton.isSelected() || eraseButton.isSelected()) && brushToolHelper.isBrushSizeClickDragMode()){
						brushToolHelper.stopBrushSizeClickDragMode(e,imagePane);
					}else{
						fireUndoableEditROI(null);//remove any pending undoableEdit
					}
					
					if(cropButton.isSelected()){
						Rectangle cropRect =
							(imagePane.getHighlightImage() == null
							?null
							:(imagePane.getCrop() == null
								?null
								:(Rectangle)imagePane.getCrop().clone()));
						if(cropRect == null){
							imagePane.setCrop(null, null);
							return;
						}
						cropRect.x = (int)(cropRect.x/imagePane.getZoom());
						cropRect.y = (int)(cropRect.y/imagePane.getZoom());
						cropRect.width = (int)(cropRect.width/imagePane.getZoom());
						cropRect.height = (int)(cropRect.height/imagePane.getZoom());
						if(cropRect.x < 0){cropRect.x = 0;}
						if(cropRect.y < 0){cropRect.y = 0;}
						if(cropRect.x >= imagePane.getHighlightImage().getWidth() ||
							cropRect.y >= imagePane.getHighlightImage().getHeight()){
							imagePane.setCrop(null, null);
							return;
						}
						if(cropRect.x+cropRect.width >= imagePane.getHighlightImage().getWidth()){
							cropRect.width = imagePane.getHighlightImage().getWidth()-cropRect.x;
						}
						if(cropRect.y+cropRect.height >= imagePane.getHighlightImage().getHeight()){
							cropRect.height = imagePane.getHighlightImage().getHeight()-cropRect.y;
						}
						if(cropRect.width <= 0){
							cropRect.width = 1;
						}if(cropRect.height <= 0){
							cropRect.height = 1;
						}
						if(!cropConfirm(cropRect)){
							return;
						}
						imagePane.setCrop(null, null);
						firePropertyChange(FRAP_DATA_CROP_PROPERTY, null, cropRect);
					}
				}
				@Override
				public void mouseClicked(MouseEvent e) {
					if(fillButton.isSelected()){
						fireUndoableEditROI(EDITTYPE_FILL);
						try{
							waitCursor(true);
							ROI.fillAtPoint(
								(int)(e.getPoint().getX()/imagePane.getZoom()),
								(int)(e.getPoint().getY()/imagePane.getZoom()),
								imagePane.getHighlightImage(),
								highlightColor.getRGB());
//								VirtualFrapLoader.mf.setSaveStatus(true);
						}finally{
							imagePane.refreshImage();
							waitCursor(false);
						}
					}
				}
				@Override
				public void mouseEntered(MouseEvent e) {
					//Set cursor
					if(!paintButton.isSelected() && !eraseButton.isSelected()){
						getImagePane().setBrush(null);
					}
				}

			});
			imagePane.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {   
				@Override
				public void mouseDragged(java.awt.event.MouseEvent e) {
					updateLabel(e.getX(), e.getY());
					if(SwingUtilities.isLeftMouseButton(e) && (paintButton.isSelected() || eraseButton.isSelected()) && !brushToolHelper.isBrushSizeClickDragMode()){
						drawHighlight(e.getX(), e.getY(), brushToolHelper.getBrushSize(getImagePane().getZoom()), eraseButton.isSelected());
						lastHighlightPoint = e.getPoint();
//						VirtualFrapLoader.mf.setSaveStatus(true);
					}else if(cropButton.isSelected()){
						imagePane.setCrop(lastHighlightPoint, e.getPoint());
					}else if(SwingUtilities.isLeftMouseButton(e) && (paintButton.isSelected() || eraseButton.isSelected()) && brushToolHelper.isBrushSizeClickDragMode()){
						brushToolHelper.dragBrushSizeClickDragMode(e, imagePane); 
					}
				}
				@Override
				public void mouseMoved(java.awt.event.MouseEvent e) {
					updateLabel(e.getX(), e.getY());
					if(!brushToolHelper.isBrushSizeClickDragMode() && (paintButton.isSelected() || eraseButton.isSelected())){
						getImagePane().setBrush(brushToolHelper.getBrushShape(e));
						getImagePane().refreshImage();
					}
				}
			});
		}
		return imagePane;
	}
	
	private boolean cropConfirm(Rectangle cropRect){
		JLabel cropConfirmJlabel =
			new JLabel("Crop FRAP data to new bounds?: ("+cropRect.x+","+cropRect.y+") to ("+
					(cropRect.x+cropRect.width-1)+","+(cropRect.y+cropRect.height-1)+")");
		cropConfirmJlabel.setPreferredSize(new Dimension(300,40));
		cropConfirmJlabel.setMinimumSize(new Dimension(300,40));
		if(DialogUtils.showComponentOKCancelDialog(
			VFrap_OverlayEditorPanelJAI.this,
			cropConfirmJlabel,
			"Confirm Crop FRAP Data to new boundaries.") != JOptionPane.OK_OPTION){
			imagePane.setCrop(null, null);
			return false;
		}
		return true;
	}
	/**
	 * Method drawHighlight.
	 * @param x int
	 * @param y int
	 * @param radius int
	 * @param erase boolean
	 */
	private void drawHighlight(int x, int y, int radius, boolean erase){
		imagePane.drawHighlight(x, y, radius, erase, highlightColor, lastHighlightPoint);
		repaint();
	}

	/**
	 * This method initializes timeSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getTimeSlider() {
		if (timeSlider == null) {
			timeSlider = new JSlider(1,1);
			timeSlider.setPaintLabels(true);
			timeSlider.setMajorTickSpacing(100);
			timeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					forceImage();
				}
			});
		}
		return timeSlider;
	}
	private void forceImage(){
		updateLabel(-1, -1);
		if(imageDataset != null){
			BufferedImage image = getImage();
			if (image != null){
				imagePane.setUnderlyingImage(image,false,minmaxPixelValues);
			}
		}
		imagePane.repaint();

	}
	/** Gets the index of the currently displayed image. * @return int
	 */
	private int getImageIndex() { return imageDataset.getIndexFromZCT(getZ(),0,getT()); }
	
	/** Gets the index of the currently displayed image. * @return int
	 */
	private int getRoiImageIndex() { return getZ(); }
	
	/** Gets the T value of the currently displayed image. * @return int
	 */
	public int getT() { return Math.max(0,Math.min(imageDataset.getSizeT(),timeSlider.getValue())) - 1; }

	/** Gets the Z value of the currently displayed image. * @return int
	 */
	public int getZ() { return Math.max(0,Math.min(imageDataset.getSizeZ(),zSlider.getValue())) - 1; }
	
	/** Updates cursor probe label. * @param x int
	 * @param y int
	 */
	protected void updateLabel(int inx, int iny) {
		if (imageDataset == null) {
			return;
		}
		float zoom = imagePane.getZoom();
		if((inx/zoom)>= imageDataset.getISize().getX() || (iny/zoom)>= imageDataset.getISize().getY()){
			inx = -1;
			iny = -1;
		}
		int x = (int)(inx/zoom);
		int y = (int)(iny/zoom);
		UShortImage[] images = imageDataset.getAllImages();
		sb.setLength(0);
		boolean bMultipleZ = imageDataset.getSizeZ() > 1;
		if (bMultipleZ) {
			sb.append("; Z=");
			sb.append(getZ() + 1);
			sb.append("/");
			sb.append(imageDataset.getSizeZ());
		}
		if (imageDataset.getSizeT() > 1) {
			sb.append((bMultipleZ?"; ":"")+"T=");
			sb.append(imageDataset.getImageTimeStamps()[getT()]);
			//add the indicator to show it is the no. x image among total images.
			sb.append("  ("+(getT()+1) + "" + "/" + imageDataset.getImageTimeStamps().length + ")");
		}
//		BufferedImage image = ImageTools.makeImage(images[ndx].getPixels(), images[ndx].getNumX(), images[ndx].getNumY());
//		int w = image == null ? -1 : image.getWidth();
//		int h = image == null ? -1 : image.getHeight();
		int w = imageDataset == null ? -1 : imageDataset.getISize().getX();
		int h = imageDataset == null ? -1 : imageDataset.getISize().getY();
		if (x >= w) x = w - 1;
		if (y >= h) y = h - 1;
		if (x >= 0 && y >= 0 && inx >= 0 && iny >= 0 &&
				x < imageDataset.getISize().getX() &&
				y < imageDataset.getISize().getY()){
			if (images.length > 1) sb.append("; ");
			sb.append("X=");
			sb.append(x);
			if (w > 0) {
				sb.append("/");
				sb.append(w);
			}
			sb.append("; Y=");
			sb.append(y);
			if (h > 0) {
				sb.append("/");
				sb.append(h);
			}
			sb.append("; zoom("+NumberUtils.formatNumber(imagePane.getZoom(), 3)+")");
			sb.append("; contr("+imagePane.getContrastDescription()+")");
			if (imageDataset != null) {
//				Raster r = image.getRaster();
//				double[] pix = r.getPixel(x, y, (double[]) null);
				short[] pix = null;
				try{
					pix = new short[imageDataset.getSizeC()];
					for (int i = 0; i < pix.length; i++) {
						pix[i] = imageDataset.getImage(getZ(), i, getT()).getPixel(x, y, 0);						
					}
				}catch(Exception e){
					pix = null;
					e.printStackTrace();
					//do nothing
				}
				sb.append("; value"+(isOriginalValueScaled()?"(scld)":""));
				if(pix != null){
					sb.append(pix.length > 1 ? "s=(" : "=");
					for (int i=0; i<pix.length; i++) {
						if (i > 0) sb.append(", ");
						sb.append((pix[i]&0x0000FFFF));
					}
					if (pix.length > 1) sb.append(")");
				}else{
					sb.append(" error");
				}
				if(isOriginalValueScaled()){
					sb.append("; value(orig)");
					if(pix != null){
						sb.append(pix.length > 1 ? "s=(" : "=");
						for (int i=0; i<pix.length; i++) {
							if (i > 0) sb.append(", ");
							sb.append(
								NumberUtils.formatNumber(
								(((pix[i]&0x0000FFFF))-originalOffsetFactor)/
								originalScaleFactor
								, 6));
						}
						if (pix.length > 1) sb.append(")");
					}else{
						sb.append(" error");
					}
					
				}
				
//				sb.append(" udnerly="+Integer.toHexString(imagePane.getUnderlyingImage().getRGB(x, y)));
			}
		}else{
			sb.append("; zoom("+NumberUtils.formatNumber(imagePane.getZoom(), 3)+")");
			sb.append("; contr("+imagePane.getContrastDescription()+")");
		}
		sb.append(" ");
		textLabel.setText(sb.toString());
	}

	private boolean isOriginalValueScaled(){
		if(originalOffsetFactor != 0 || originalScaleFactor != 1){
			return true;
		}
		return false;
	}
	/**
	 * This method initializes jScrollPane2	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane2() {
		if (jScrollPane2 == null) {
			jScrollPane2 = new JScrollPane();
			jScrollPane2.setViewportView(getImagePane());
		}
		return jScrollPane2;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */

	private BrushToolHelper brushToolHelper;

	private JPanel getRightPanel() {
		if (rightPanel == null) {
			//this is added to the right side of the editor panel with all the buttons in two columns.
			rightPanel = new JPanel();
			final GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.rowHeights = new int[] {0,0,7,7,7,0,7};
			rightPanel.setLayout(gridBagLayout);
			
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			rightPanel.add(getZoomInButton(), gridBagConstraints1);
			
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			if(!isEditable)//buttons in one column
			{
				gridBagConstraints2.gridx = 0;
				gridBagConstraints2.gridy = 1;
			}
			else //display in two columns
			{
				gridBagConstraints2.gridx = 1;
				gridBagConstraints2.gridy = 0;
			}
			rightPanel.add(getZoomOutButton(), gridBagConstraints2);

			final GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			if(!isEditable)//buttons in one column
			{
				gridBagConstraints3.gridy = 2;
				gridBagConstraints3.gridx = 0;
			}
			else //display in two columns
			{
				gridBagConstraints3.gridy = 1;
				gridBagConstraints3.gridx = 0;
			}
			rightPanel.add(getContrastButtonPlus(), gridBagConstraints3);

			final GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			if(!isEditable)//buttons in one column
			{
				gridBagConstraints4.gridy = 3;
				gridBagConstraints4.gridx = 0;
			}
			else //display in two columns
			{
				gridBagConstraints4.gridy = 1;
				gridBagConstraints4.gridx = 1;
			}
			rightPanel.add(getContrastButtonMinus(), gridBagConstraints4);
			
			/*
			 *NOTE: display type will not be applied to other buttons so far.
			 *The reason is only 4 buttons are visible in the image panel in main frame.
			 *We want one column display for the four buttons in main frame.
			 *If anyone wants one column display for all the buttons, please change following buttons accordingly.
			 */
			
			final GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.insets = new Insets(12, 0, 12, 0);
			rightPanel.add(getCropButton(), gridBagConstraints5);
			
			final GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridy = 2;
			gridBagConstraints6.gridx = 1;
			gridBagConstraints6.insets = new Insets(12, 0, 12, 0);
			rightPanel.add(getAutoCropButton(), gridBagConstraints6);
			
			final GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridy = 3;
			gridBagConstraints7.gridx = 0;
			rightPanel.add(getPaintButton(), gridBagConstraints7);

			final GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridy = 4;
			gridBagConstraints8.gridx = 0;
			rightPanel.add(getEraseButton(), gridBagConstraints8);

			brushToolHelper = new BrushToolHelper(new JToggleButton[] {paintButton,eraseButton}, 10,null);
				getImagePane().addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						// TODO Auto-generated method stub
						super.mouseEntered(e);
						brushToolHelper.hidePopup();
					}
					
				});

			final GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridy = 5;
			gridBagConstraints9.gridx = 0;
			rightPanel.add(getFillButton(), gridBagConstraints9);

			final GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridy = 3;
			gridBagConstraints10.gridx = 1;
			rightPanel.add(getImportROIMaskButton(), gridBagConstraints10);
			
			final GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridy = 4;
			gridBagConstraints11.gridx = 1;
			rightPanel.add(getClearROIbutton(), gridBagConstraints11);
			
			final GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridy = 5;
			gridBagConstraints12.gridx = 1;
			rightPanel.add(getRoiTimePlotButton(), gridBagConstraints12);
			
		}

		return rightPanel;
	}

	private JButton getZoomInButton() {
		if (zoomInButton == null) {
			zoomInButton = new JButton();
			zoomInButton.setMargin(new Insets(2, 2, 2, 2));
			zoomInButton.setPreferredSize(new Dimension(32, 32));
			ZoomShapeIcon.setZoomOverlayEditorMod(zoomInButton, ZoomShapeIcon.Sign.plus);
			zoomInButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getImagePane().setZoom(getImagePane().getZoom()*1.3f);
					updateLabel(-1,-1);
				}
			});
		}
		return zoomInButton;
	}
	
	private JButton getZoomOutButton() {
		if (zoomOutButton == null) {
			zoomOutButton = new JButton();
			zoomOutButton.setPreferredSize(new Dimension(32, 32));
			zoomOutButton.setMargin(new Insets(2, 2, 2, 2));
			ZoomShapeIcon.setZoomOverlayEditorMod(zoomOutButton, ZoomShapeIcon.Sign.minus);
			zoomOutButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getImagePane().setZoom(getImagePane().getZoom()/1.3f);
					updateLabel(-1,-1);
				}
			});
			zoomOutButton.setIcon(new ImageIcon(getClass().getResource("/images/zoomout.gif")));
		}
		return zoomOutButton;
	}

	public JButton getContrastButtonMinus() {
		if(contrastButtonMinus == null)
		{
			contrastButtonMinus = new JButton(new ImageIcon(getClass().getResource("/images/contrastDown.gif")));
			contrastButtonMinus.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					imagePane.decreaseContrast();
					updateLabel(-1,-1);
				}
			});
			contrastButtonMinus.setPreferredSize(new Dimension(32, 32));
			contrastButtonMinus.setMargin(new Insets(2, 2, 2, 2));
			contrastButtonMinus.setToolTipText("Decrease Contrast");
		}
		return contrastButtonMinus;
	}

	public JButton getContrastButtonPlus() {
		if(contrastButtonPlus == null)
		{
			contrastButtonPlus = new JButton(new ImageIcon(getClass().getResource("/images/contrastUp.gif")));
			contrastButtonPlus.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					imagePane.increaseContrast();
					updateLabel(-1,-1);
				}
			});
			contrastButtonPlus.setPreferredSize(new Dimension(32, 32));
			contrastButtonPlus.setMargin(new Insets(2, 2, 2, 2));
			contrastButtonPlus.setToolTipText("Increase Contrast");
		}
		return contrastButtonPlus;
	}

	public JToggleButton getCropButton() {
		if(cropButton == null)
		{
			cropButton = new JToggleButton(new ImageIcon(getClass().getResource("/images/crop.gif")));
			cropButton.setPreferredSize(new Dimension(32, 32));
			cropButton.setMargin(new Insets(2, 2, 2, 2));
			cropButton.setToolTipText("Crop");
		}
		return cropButton;
	}

	public JToggleButton getFillButton() {
		if(fillButton == null)
		{
			fillButton = new JToggleButton(new ImageIcon(getClass().getResource("/images/fill.gif")));
			fillButton.setPreferredSize(new Dimension(32, 32));
			fillButton.setMargin(new Insets(2, 2, 2, 2));
			fillButton.setToolTipText("Fill");
		}
		return fillButton;
	}

	public JToggleButton getEraseButton() {
		if(eraseButton == null)
		{
			eraseButton = new JToggleButton(new ImageIcon(getClass().getResource("/images/eraser.gif")));
			eraseButton.setPreferredSize(new Dimension(32, 32));
			eraseButton.setMargin(new Insets(2, 2, 2, 2));
			eraseButton.setToolTipText("Erase");
		}
		return eraseButton;
	}

	public JToggleButton getPaintButton() {
		if(paintButton == null)
		{
			paintButton = new JToggleButton(new ImageIcon(getClass().getResource("/images/paint.gif")));
			paintButton.setSelected(true);
			paintButton.setPreferredSize(new Dimension(32, 32));
			paintButton.setMargin(new Insets(2, 2, 2, 2));
			paintButton.setToolTipText("Paint");
		}
		return paintButton;
	}

	public JButton getImportROIMaskButton() {
		if(importROIMaskButton == null)
		{
			importROIMaskButton = new JButton(new ImageIcon(getClass().getResource("/images/importROI.gif")));
			importROIMaskButton.setPreferredSize(new Dimension(32, 32));
			importROIMaskButton.setMargin(new Insets(2, 2, 2, 2));
			importROIMaskButton.setToolTipText("Import ROI mask from file");
			importROIMaskButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					File inputFile = null;
					try {
						int option = openJFileChooser.showOpenDialog(VFrap_OverlayEditorPanelJAI.this);
						if (option == JFileChooser.APPROVE_OPTION){
							inputFile = openJFileChooser.getSelectedFile();
						}else{
							throw UserCancelException.CANCEL_GENERIC;
						}
						if(!customROIImport.importROI(inputFile)){
							ImageDataset importImageDataset = ImageDatasetReaderService.getInstance().getImageDatasetReader().readImageDataset(inputFile.getAbsolutePath(), null);
							if(importImageDataset.getISize().getX() * importImageDataset.getISize().getY() != 
								getImagePane().getHighlightImage().getWidth()*getImagePane().getHighlightImage().getHeight()){
								throw new Exception(
									"Imported ROI mask size ("+
											importImageDataset.getISize().getX()+","+importImageDataset.getISize().getY()+")"+
									" does not match current Frap DataSet size ("+
									getImagePane().getHighlightImage().getWidth()+","+
									getImagePane().getHighlightImage().getHeight()+")");
							}
//							BufferedImage roiMaskImage = BufferedImageReader.makeBufferedImageReader(iFormatReader).openImage(0);
							UShortImage roiMaskImage = importImageDataset.getImage(0, 0, 0);
							int maskColor = highlightColor.getRGB();
							for (int y = 0; y < importImageDataset.getISize().getY(); y++) {
								for (int x = 0; x < importImageDataset.getISize().getX(); x++) {
									if((roiMaskImage.getPixel(x,y,0)&0xFFFF) != 0){
										getImagePane().getHighlightImage().setRGB(x,y,maskColor);
									}
								}
							}
							getImagePane().refreshImage();
						}
					} catch (UserCancelException uce) {
						//Do Nothing
					} catch (Exception e1) {
						e1.printStackTrace();
						DialogUtils.showErrorDialog(VFrap_OverlayEditorPanelJAI.this, "Error importing ROI"+e1.getMessage());
					}
				}
			});
		}
		return importROIMaskButton;
	}

	public JButton getClearROIbutton() {
		if(clearROIbutton == null)
		{
			clearROIbutton = new JButton(new ImageIcon(getClass().getResource("/images/clearROI.gif")));
			clearROIbutton.setPreferredSize(new Dimension(32, 32));
			clearROIbutton.setMargin(new Insets(2, 2, 2, 2));
			clearROIbutton.setToolTipText("Clear ROI");
			clearROIbutton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					clearROI();
				}
			});
		}
		return clearROIbutton;
	}

	public JButton getRoiTimePlotButton() {
		if(roiTimePlotButton == null)
		{
			roiTimePlotButton = new JButton(new ImageIcon(getClass().getResource("/images/plotROI.gif")));
			roiTimePlotButton.setPreferredSize(new Dimension(32, 32));
			roiTimePlotButton.setMargin(new Insets(2, 2, 2, 2));
			roiTimePlotButton.setToolTipText("ROI time plot");
			roiTimePlotButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					if(getImagePane() == null || getImagePane().getHighlightImage() == null){
						return;
					}
					boolean bHasROI = false;
					for (int y = 0; y < getImagePane().getHighlightImage().getHeight(); y++) {
						for (int x = 0; x < getImagePane().getHighlightImage().getWidth(); x++) {
							if((getImagePane().getHighlightImage().getRGB(x, y)&0x00FFFFFF) != 0){
								bHasROI = true;
								break;
							}
						}
						if(bHasROI){
							break;
						}
					}
					if(bHasROI){
						firePropertyChange(FRAP_DATA_TIMEPLOTROI_PROPERTY, null,new Boolean(true));
					}else{
						DialogUtils.showInfoDialog(VFrap_OverlayEditorPanelJAI.this, 
							"ROI for "+roi.getROIName()+" is empty.\n"+
							"Paint, Fill or Import ROI using ROI tools.");
					}
				}
			});
		}
		return roiTimePlotButton;
	}

	public JToggleButton getAutoCropButton() {
		if(autoCropButton == null)
		{
			autoCropButton = new JToggleButton(new ImageIcon(getClass().getResource("/images/autoCrop.gif")));
			autoCropButton.setPreferredSize(new Dimension(32, 32));
			autoCropButton.setMargin(new Insets(2, 2, 2, 2));
			autoCropButton.setToolTipText("Auto Crop");
			autoCropButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					try {
						waitCursor(true);
						if(isAutoCroppable()){
							Rectangle cropRect = null;
							if(imageDataset != null){
								cropRect = imageDataset.getNonzeroBoundingRectangle();
							}
							imagePane.setCrop(
									new Point(
										(int)(cropRect.x*imagePane.getZoom()),
										(int)(cropRect.y*imagePane.getZoom())),
									new Point(
										(int)((cropRect.x+cropRect.width)*imagePane.getZoom()),
										(int)((cropRect.y+cropRect.height)*imagePane.getZoom())));
							waitCursor(false);
							if(!cropConfirm(cropRect)){
								imagePane.setCrop(null, null);
								return;
							}
							waitCursor(true);
							imagePane.setCrop(null, null);
							firePropertyChange(FRAP_DATA_CROP_PROPERTY, null,cropRect);						
						}else{
							DialogUtils.showInfoDialog(VFrap_OverlayEditorPanelJAI.this, "AutoCrop: No zero values around outer edges.");
						}
					} catch (Exception e1) {
						e1.printStackTrace();
						DialogUtils.showErrorDialog(VFrap_OverlayEditorPanelJAI.this, "Error AutoCrop:\n"+e1.getMessage());
					}finally{
						waitCursor(false);				
					}
				}
			});
		}
		return autoCropButton;
	}

	public JButton getRoiAssistButton() {
		if(roiAssistButton == null)
		{
			roiAssistButton = new JButton("ROI ASSIST (Help Detecting ROI Automatically)");
			roiAssistButton.setIcon(new ImageIcon(getClass().getResource("/images/roiAssistClose.gif")));
			roiAssistButton.setMargin(new Insets(2, 2, 2, 2));
			roiAssistButton.setToolTipText("ROI Assist");
			roiAssistButton.setContentAreaFilled(false);
			roiAssistButton.setFocusPainted(false);
			roiAssistButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					if(isROIAssistVisible())
					{
						setROIAssistVisible(false);
						adjustComponentsForVFRAP(HIDE_ROI_ASSISTANT);
					}
					else
					{
						showROIAssist();
					}
				}
			});
		}
		return roiAssistButton;
	}

	/**
	 * This method initializes zSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getZSlider() {
		if (zSlider == null) {
			zSlider = new JSlider(1,1);
			zSlider.setMajorTickSpacing(100);
			zSlider.setPaintLabels(true);
			zSlider.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					saveUserChangesToROI();
					updateLabel(-1, -1);
					BufferedImage image = getImage();
					if (image != null){
						imagePane.setUnderlyingImage(image,false,minmaxPixelValues);
					}
					refreshROI();
					imagePane.repaint();
				}
			});
		}
		return zSlider;
	}
	
	public void setCustomROIImport(CustomROIImport customROIImport){
		this.customROIImport = customROIImport;
	}

	public void setDefaultImportDirAndFilters(File defaultImportDir,FileFilter[] fileFilterArr){
	    openJFileChooser.setCurrentDirectory(defaultImportDir);
	    for (int i = 0; i < fileFilterArr.length; i++) {
	    	openJFileChooser.addChoosableFileFilter(fileFilterArr[i]);
		}
	}
	
	public VFrap_ROISourceData getRoiSouceData()
	{
		//if roiSourceData is not set when the panel is created, we create an instance here.
		//VCell uses the instance created here and VFRAP uses instance which is set when the panel is created.
		if(roiSourceData == null)
		{
			roiSourceData = new VFrap_ROISourceData(){
				public void addReplaceRoi(ROI originalROI) {
					roi.setRoiName(originalROI.getROIName());
					roi.setROIImages(originalROI.getRoiImages());
					refreshROI();
					if(roi != null){
						for (int i = 0; i < roiComboBox.getItemCount(); i++) {
							if(((ComboboxROIName)roiComboBox.getItemAt(i)).getROIName().equals(roi.getROIName())){
								roiComboBox.setSelectedIndex(i);
								break;
							}
						}
					}
					updateROICursor();
				}
				public ROI getCurrentlyDisplayedROI() {
					return VFrap_OverlayEditorPanelJAI.this.getROI();
				}
				public ImageDataset getImageDataset() {
					return VFrap_OverlayEditorPanelJAI.this.imageDataset;
				}
				public ROI getRoi(String name) {
					return VFrap_OverlayEditorPanelJAI.this.getROI();
				}
			};
		}
		return roiSourceData;
	}

	public void setRoiSouceData(VFrap_ROISourceData roiSourceData) 
	{
		this.roiSourceData = roiSourceData;
	}

}


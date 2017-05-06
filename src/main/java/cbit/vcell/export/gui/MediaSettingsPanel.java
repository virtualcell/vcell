/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.vcell.util.gui.DialogUtils;

import cbit.image.DisplayAdapterService;
import cbit.image.DisplayPreferences;
import cbit.image.ImagePaneModel;
import cbit.vcell.export.server.ExportConstants;
import cbit.vcell.export.server.ExportFormat;
import cbit.vcell.export.server.FormatSpecificSpecs;
import cbit.vcell.export.server.ImageSpecs;
import cbit.vcell.export.server.MovieSpecs;
import cbit.vcell.export.server.TimeSpecs;
import cbit.vcell.solvers.CartesianMesh;

@SuppressWarnings("serial")
public class MediaSettingsPanel extends JPanel {
	private JTextField movieDurationTextField;
	private JRadioButton encodeFormatJPG;
	private JRadioButton encodeFormatGIF;
	private JSlider compressionSlider;
	private JRadioButton compositionSeparate;
	private JRadioButton compositionCombined;
	private JRadioButton qtFormatRegular;
	private JRadioButton qtFormatQTVR;
	private JComboBox<String> mirrorComboBox;
	private JComboBox<String> scalingCombobox;
	private JLabel membrVarThicknessLabel;
	private JLabel lblMirroring;
	private JLabel lblImageScale;
	private JLabel lblEncodingFormat;
	public MediaSettingsPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0,1.0};
		gridBagLayout.rowWeights = new double[]{0, 0.0, 0,0,0, 0.0, 0.0, 0.0, 0.0, 1.0,0.0};
		setLayout(gridBagLayout);
		
		lblEncodingFormat = new JLabel("Encoding Format:");
		GridBagConstraints gbc_lblEncodingFormat = new GridBagConstraints();
		gbc_lblEncodingFormat.anchor = GridBagConstraints.EAST;
		gbc_lblEncodingFormat.insets = new Insets(4, 4, 5, 5);
		gbc_lblEncodingFormat.gridx = 0;
		gbc_lblEncodingFormat.gridy = 0;
		add(lblEncodingFormat, gbc_lblEncodingFormat);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		encodeFormatJPG = new JRadioButton("JPEG");
		GridBagConstraints gbc_encodeFormatJPG = new GridBagConstraints();
		gbc_encodeFormatJPG.insets = new Insets(0, 0, 0, 5);
		gbc_encodeFormatJPG.gridx = 0;
		gbc_encodeFormatJPG.gridy = 0;
		panel.add(encodeFormatJPG, gbc_encodeFormatJPG);
		
		encodeFormatGIF = new JRadioButton("GIF");
		GridBagConstraints gbc_encodeFormatGIF = new GridBagConstraints();
		gbc_encodeFormatGIF.insets = new Insets(0, 0, 0, 5);
		gbc_encodeFormatGIF.gridx = 1;
		gbc_encodeFormatGIF.gridy = 0;
		panel.add(encodeFormatGIF, gbc_encodeFormatGIF);
		
		compressionLabel = new JLabel("File Compression:");
		GridBagConstraints gbc_compressionLabel = new GridBagConstraints();
		gbc_compressionLabel.anchor = GridBagConstraints.EAST;
		gbc_compressionLabel.insets = new Insets(0, 0, 5, 5);
		gbc_compressionLabel.gridx = 0;
		gbc_compressionLabel.gridy = 1;
		add(compressionLabel, gbc_compressionLabel);
		
		compressionSlider = new JSlider();
		GridBagConstraints gbc_compressionSlider = new GridBagConstraints();
		gbc_compressionSlider.fill = GridBagConstraints.HORIZONTAL;
		gbc_compressionSlider.weightx = 1.0;
		gbc_compressionSlider.insets = new Insets(0, 0, 5, 0);
		gbc_compressionSlider.gridx = 1;
		gbc_compressionSlider.gridy = 1;
		add(compressionSlider, gbc_compressionSlider);
		
		compositionLabel = new JLabel("Variables Display Composition:");
		GridBagConstraints gbc_compositionLabel = new GridBagConstraints();
		gbc_compositionLabel.anchor = GridBagConstraints.EAST;
		gbc_compositionLabel.insets = new Insets(0, 0, 5, 5);
		gbc_compositionLabel.gridx = 0;
		gbc_compositionLabel.gridy = 2;
		add(compositionLabel, gbc_compositionLabel);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 2;
		add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		compositionSeparate = new JRadioButton("Separate");
		GridBagConstraints gbc_compositionSeparate = new GridBagConstraints();
		gbc_compositionSeparate.insets = new Insets(0, 0, 0, 5);
		gbc_compositionSeparate.gridx = 0;
		gbc_compositionSeparate.gridy = 0;
		panel_1.add(compositionSeparate, gbc_compositionSeparate);
		
		compositionCombined = new JRadioButton("Combined");
		GridBagConstraints gbc_compositionCombined = new GridBagConstraints();
		gbc_compositionCombined.gridx = 1;
		gbc_compositionCombined.gridy = 0;
		panel_1.add(compositionCombined, gbc_compositionCombined);
		
		qtLabel = new JLabel("QuickTime Format:");
		GridBagConstraints gbc_qtLabel = new GridBagConstraints();
		gbc_qtLabel.anchor = GridBagConstraints.EAST;
		gbc_qtLabel.insets = new Insets(0, 0, 5, 5);
		gbc_qtLabel.gridx = 0;
		gbc_qtLabel.gridy = 3;
		add(qtLabel, gbc_qtLabel);
		
		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 1;
		gbc_panel_2.gridy = 3;
		add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{0, 0, 0};
		gbl_panel_2.rowHeights = new int[]{0, 0};
		gbl_panel_2.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		qtFormatRegular = new JRadioButton("Regular");
		GridBagConstraints gbc_qtFormatRegular = new GridBagConstraints();
		gbc_qtFormatRegular.insets = new Insets(0, 0, 0, 5);
		gbc_qtFormatRegular.gridx = 0;
		gbc_qtFormatRegular.gridy = 0;
		panel_2.add(qtFormatRegular, gbc_qtFormatRegular);
		
		qtFormatQTVR = new JRadioButton("QTVR");
		GridBagConstraints gbc_qtFormatQTVR = new GridBagConstraints();
		gbc_qtFormatQTVR.gridx = 1;
		gbc_qtFormatQTVR.gridy = 0;
		panel_2.add(qtFormatQTVR, gbc_qtFormatQTVR);
		
		volvarMembrOutlineLabel = new JLabel("Volume Var Membr Outline Thickness:");
		GridBagConstraints gbc_membrOutlineLabel = new GridBagConstraints();
		gbc_membrOutlineLabel.anchor = GridBagConstraints.EAST;
		gbc_membrOutlineLabel.insets = new Insets(0, 0, 5, 5);
		gbc_membrOutlineLabel.gridx = 0;
		gbc_membrOutlineLabel.gridy = 4;
		add(volvarMembrOutlineLabel, gbc_membrOutlineLabel);
		
		JPanel panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 1;
		gbc_panel_3.gridy = 4;
		add(panel_3, gbc_panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[]{0};
		gbl_panel_3.rowHeights = new int[]{0};
		gbl_panel_3.columnWeights = new double[]{0.0};
		gbl_panel_3.rowWeights = new double[]{0.0};
		panel_3.setLayout(gbl_panel_3);
		
		volVarMembrOutlineThicknessSlider = new JSlider();
		volVarMembrOutlineThicknessSlider.setPaintTicks(true);
		volVarMembrOutlineThicknessSlider.setPaintLabels(true);
		GridBagConstraints gbc_volVarMembrOutlineThicknessSlider = new GridBagConstraints();
		gbc_volVarMembrOutlineThicknessSlider.fill = GridBagConstraints.HORIZONTAL;
		gbc_volVarMembrOutlineThicknessSlider.weightx = 1.0;
		gbc_volVarMembrOutlineThicknessSlider.gridx = 0;
		gbc_volVarMembrOutlineThicknessSlider.gridy = 0;
		panel_3.add(volVarMembrOutlineThicknessSlider, gbc_volVarMembrOutlineThicknessSlider);
		
		lblMirroring = new JLabel("Variables Mirroring:");
		GridBagConstraints gbc_lblMirroring = new GridBagConstraints();
		gbc_lblMirroring.anchor = GridBagConstraints.EAST;
		gbc_lblMirroring.insets = new Insets(0, 0, 5, 5);
		gbc_lblMirroring.gridx = 0;
		gbc_lblMirroring.gridy = 5;
		add(lblMirroring, gbc_lblMirroring);
		
		mirrorComboBox = new JComboBox<String>();
		GridBagConstraints gbc_mirrorComboBox = new GridBagConstraints();
		gbc_mirrorComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_mirrorComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_mirrorComboBox.gridx = 1;
		gbc_mirrorComboBox.gridy = 5;
		add(mirrorComboBox, gbc_mirrorComboBox);
		
		lblImageScale = new JLabel("Image Size Scaling:");
		GridBagConstraints gbc_lblImageScale = new GridBagConstraints();
		gbc_lblImageScale.anchor = GridBagConstraints.EAST;
		gbc_lblImageScale.insets = new Insets(0, 0, 5, 5);
		gbc_lblImageScale.gridx = 0;
		gbc_lblImageScale.gridy = 6;
		add(lblImageScale, gbc_lblImageScale);
		
		scalingCombobox = new JComboBox<String>();
		GridBagConstraints gbc_scalingCombobox = new GridBagConstraints();
		gbc_scalingCombobox.insets = new Insets(0, 0, 5, 0);
		gbc_scalingCombobox.fill = GridBagConstraints.HORIZONTAL;
		gbc_scalingCombobox.gridx = 1;
		gbc_scalingCombobox.gridy = 6;
		add(scalingCombobox, gbc_scalingCombobox);
		
		membrVarThicknessLabel = new JLabel("Membrane Variable Thickness:");
		GridBagConstraints gbc_membrVarThicknessLabel = new GridBagConstraints();
		gbc_membrVarThicknessLabel.anchor = GridBagConstraints.EAST;
		gbc_membrVarThicknessLabel.insets = new Insets(0, 0, 5, 5);
		gbc_membrVarThicknessLabel.gridx = 0;
		gbc_membrVarThicknessLabel.gridy = 7;
		add(membrVarThicknessLabel, gbc_membrVarThicknessLabel);
		
		membrVarThicknessSlider = new JSlider();
		membrVarThicknessSlider.setPaintTicks(true);
		membrVarThicknessSlider.setPaintLabels(true);
		GridBagConstraints gbc_membrVarThicknessSlider = new GridBagConstraints();
		gbc_membrVarThicknessSlider.fill = GridBagConstraints.HORIZONTAL;
		gbc_membrVarThicknessSlider.weightx = 1.0;
		gbc_membrVarThicknessSlider.insets = new Insets(0, 0, 5, 0);
		gbc_membrVarThicknessSlider.gridx = 1;
		gbc_membrVarThicknessSlider.gridy = 7;
		add(membrVarThicknessSlider, gbc_membrVarThicknessSlider);
		
		movieDurationLabel = new JLabel("Movie Duration (seconds):");
		GridBagConstraints gbc_movieDurationLabel = new GridBagConstraints();
		gbc_movieDurationLabel.anchor = GridBagConstraints.EAST;
		gbc_movieDurationLabel.insets = new Insets(0, 0, 5, 5);
		gbc_movieDurationLabel.gridx = 0;
		gbc_movieDurationLabel.gridy = 8;
		add(movieDurationLabel, gbc_movieDurationLabel);
		
		movieDurationTextField = new JTextField();
		GridBagConstraints gbc_movieDurationTextField = new GridBagConstraints();
		gbc_movieDurationTextField.insets = new Insets(0, 0, 5, 0);
		gbc_movieDurationTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_movieDurationTextField.gridx = 1;
		gbc_movieDurationTextField.gridy = 8;
		add(movieDurationTextField, gbc_movieDurationTextField);
		movieDurationTextField.setColumns(10);
		
		particleModeLabel = new JLabel("Particle info export format:");
		particleModeLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_particleModeLabel = new GridBagConstraints();
		gbc_particleModeLabel.anchor = GridBagConstraints.EAST;
		gbc_particleModeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_particleModeLabel.gridx = 0;
		gbc_particleModeLabel.gridy = 9;
		add(particleModeLabel, gbc_particleModeLabel);
		
		panel_5 = new JPanel();
		GridBagConstraints gbc_panel_5 = new GridBagConstraints();
		gbc_panel_5.fill = GridBagConstraints.BOTH;
		gbc_panel_5.insets = new Insets(0, 0, 5, 0);
		gbc_panel_5.gridx = 1;
		gbc_panel_5.gridy = 9;
		add(panel_5, gbc_panel_5);
		GridBagLayout gbl_panel_5 = new GridBagLayout();
		gbl_panel_5.columnWidths = new int[]{0, 0, 0, 0};
		gbl_panel_5.rowHeights = new int[]{0, 0};
		gbl_panel_5.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_5.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_5.setLayout(gbl_panel_5);
		
		particleRadioButton = new JRadioButton("xyz coordinate lists");
//		particleRadioButton.setSelected(true);
		GridBagConstraints gbc_particleRadioButton = new GridBagConstraints();
		gbc_particleRadioButton.insets = new Insets(0, 0, 0, 5);
		gbc_particleRadioButton.gridx = 0;
		gbc_particleRadioButton.gridy = 0;
		panel_5.add(particleRadioButton, gbc_particleRadioButton);
		
		particleCountsRadiobutton = new JRadioButton("volume counts as image");
		particleCountsRadiobutton.setSelected(true);
		GridBagConstraints gbc_particleCountsRadiobutton = new GridBagConstraints();
		gbc_particleCountsRadiobutton.insets = new Insets(0, 0, 0, 5);
		gbc_particleCountsRadiobutton.gridx = 1;
		gbc_particleCountsRadiobutton.gridy = 0;
		panel_5.add(particleCountsRadiobutton, gbc_particleCountsRadiobutton);
		
		panel_4 = new JPanel();
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.weighty = 1.0;
		gbc_panel_4.gridwidth = 2;
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.gridx = 0;
		gbc_panel_4.gridy = 10;
		add(panel_4, gbc_panel_4);
		
		continueButton = new JButton("Continue...");
		panel_4.add(continueButton);
		
		cancelButton = new JButton("Cancel");
		panel_4.add(cancelButton);
		
		init();
	}

	public static final String OK_ACTION_COMMAND = "OK";
	public static final String CANCEL_ACTION_COMMAND = "Cancel";
	Vector<ActionListener> actionListenerV = new Vector<ActionListener>();
	public void addActionListener(ActionListener actionListener){
		if(!actionListenerV.contains(actionListener)){
			actionListenerV.add(actionListener);
		}
	}
	private static final String MESH_MODE_TEXT = "from 'View Data' zoom";
	private ButtonGroup encodeFormatButtonGroup = new ButtonGroup();
	private ButtonGroup compositionButtonGroup = new ButtonGroup();
	private ButtonGroup qtFormatButtonGroup = new ButtonGroup();
	private ButtonGroup particleButtonGroup = new ButtonGroup();
	private double duration;
	ActionListener buttonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == continueButton){
				try {
					duration = getDuration();
				} catch (Exception e2) {
					e2.printStackTrace();
					DialogUtils.showErrorDialog(MediaSettingsPanel.this,"'Movie Duration (seconds)' value error: ('"+movieDurationTextField.getText()+
							"')\nRe-enter a 'Duration' value between "+Double.toString(MOVIE_DURATION_MIN_SECONDS)+" and "+Double.toString(MOVIE_DURATION_MAX_SECONDS));
					return;
				}
				
				int numVars = variableNames.length;
				int numSlices = sliceCount;
				int numTimePoints = timeSpecs.getEndTimeIndex()-timeSpecs.getBeginTimeIndex()+1;
				boolean bQTVR = qtFormatQTVR.isEnabled() && qtFormatQTVR.isSelected();
				boolean bSeparate = !compositionSeparate.isEnabled() || compositionSeparate.isSelected();
				int numMedia  = -1;
				int numMovieFrames = -1;
				if(bMovie){
					if(bQTVR){
						numMovieFrames = numTimePoints*numSlices;
						if(bSeparate){
							numMedia = numVars;
						}else{
							numMedia = 1;
						}
					}else{
						numMovieFrames = numTimePoints;
						if(bSeparate){
							numMedia = numVars*numSlices;
						}else{
							numMedia = numSlices;
						}
					}
				}else{
					if(bSeparate){
						numMedia = numVars*numSlices*numTimePoints;
					}else{
						numMedia = numSlices*numTimePoints;
					}
				}
				String encodingFormat = null;
				if(mediaType == ExportFormat.ANIMATED_GIF || mediaType == ExportFormat.GIF){
					encodingFormat = "GIF";
				}else{
					encodingFormat = "JPG";
				}
				String mediaDescription = null;
				boolean bLossLess = true;
				switch(mediaType){
					case GIF:
						mediaDescription = "GIF Image";
						break;
					case FORMAT_JPEG:
						mediaDescription = "JPEG Image";
						bLossLess = compressionSlider.getValue() == compressionSlider.getMaximum();
						break;
					case ANIMATED_GIF:
						mediaDescription = "Animated GIF";
						break;
					case QUICKTIME:
						mediaDescription = "QuickTime "+(bQTVR?"VR ":"")+"Movie";
						bLossLess = compressionSlider.getValue() == compressionSlider.getMaximum();
						break;
					default:
							mediaDescription = "Unknown";
				}
				//Calculate Image/Frame size
				Dimension imageDim = null;
				int meshMode = -1;
				int imageScale = -1;
				boolean bSmoldynParticle = (isSmoldyn && particleRadioButton.isSelected());
				if(bSmoldynParticle){
					imageDim = FormatSpecificSpecs.SMOLDYN_DEFAULT_FRAME_SIZE;
				}else{
					meshMode = (scalingCombobox.getSelectedItem().equals(MESH_MODE_TEXT)?ImagePaneModel.MESH_MODE:ImagePaneModel.NORMAL_MODE);
					imageScale = (meshMode == ImagePaneModel.MESH_MODE?viewZoom:Integer.valueOf((String)scalingCombobox.getSelectedItem()));
					imageDim = FormatSpecificSpecs.getImageDimension(meshMode, imageScale, mesh, normalAxis);
					imageDim = FormatSpecificSpecs.getMirrorDimension(mirrorComboBox.getSelectedIndex(), imageDim.width, imageDim.height);
					imageDim.height = (bSeparate?imageDim.height:imageDim.height*variableNames.length);
				}
				String finalFileDescription = null;
				if(!bSmoldynParticle && (numMedia > 1)){
					finalFileDescription = "ZIP file containing "+numMedia+" files of type "+mediaDescription;
				}else{
					finalFileDescription = "1 "+mediaDescription;
				}
				JTextArea exportInfoJTextArea = new JTextArea();
				JScrollPane jScrollPane = new JScrollPane(/*exportInfoJTextArea*/);
				jScrollPane.setMaximumSize(new Dimension(400,300));
				jScrollPane.setPreferredSize(new Dimension(400,300));
				exportInfoJTextArea.setEditable(false);
				if(isSmoldyn && particleRadioButton.isSelected()){
					String str = "Collection of text files ("+numTimePoints+") each containing\n"+
							"all variables particle coordinates (xyz) for a timepoint.\n"+
							"One row per particle coordinate with format [varname x y z]";
					exportInfoJTextArea.append("Export Type: Particle Coordinate Lists\n");
					exportInfoJTextArea.append("Description: "+str+"\n");
				}else{
					exportInfoJTextArea.append("Export Type: "+(bMovie?"Movie":"Image")+"\n");
					exportInfoJTextArea.append("Description: "+finalFileDescription+"\n");
					exportInfoJTextArea.append((bMovie?"Frame":"Image")+" Size: width="+imageDim.width+" height="+imageDim.height+"\n\n");
					if(bMovie){
						exportInfoJTextArea.append("Movie Duration: "+getDuration()/1000.0+" Seconds\n");
						exportInfoJTextArea.append("Movie Frame Count: "+numMovieFrames+"\n");
						if(bQTVR){
							exportInfoJTextArea.append("QuickTime VR is a special movie format that standard QuickTime viewers will recognize and allows\n"+
								"     users to traverse through time and slice information in a single movie\n");
						}
						exportInfoJTextArea.append("\n");
					}
					if(bSmoldynParticle){
						exportInfoJTextArea.append("Composition: All particles rendered together in each frame.\n");
					}else{
						exportInfoJTextArea.append("Composition: "+(bSeparate?"Export variables individually":"Export variables composited together vertically")+"\n");					
					}
					exportInfoJTextArea.append("Encoding Format: "+encodingFormat+"\n");
					int compressionValue = 0;
					if(!bLossLess && compressionSlider.getValue() != 0){
						compressionValue = (int)(100*compressionSlider.getValue()/(double)compressionSlider.getMaximum());
					}
					exportInfoJTextArea.append("Compression Quality: "+(bLossLess?"LossLess (best quality,least compression)":(compressionValue==0?"0% (worst quality, most compression":compressionValue+"%"))+"\n");
					exportInfoJTextArea.append("Num Variables: "+numVars+"\n");
					exportInfoJTextArea.append("Num Slices: "+numSlices+"\n");
					exportInfoJTextArea.append("Num TimePoints: "+numTimePoints+" (from "+timeSpecs.getAllTimes()[timeSpecs.getBeginTimeIndex()]+" to "+timeSpecs.getAllTimes()[timeSpecs.getEndTimeIndex()]+")\n");
	
					if(bSmoldynParticle){
						exportInfoJTextArea.append("Particle Mode: "+(isSmoldyn?"Particle Data ("+(particleRadioButton.isSelected()?"Render Particles":"Render Particle Counts")+")":"Non Particle Data")+"\n");
					}else{
						if(volVarMembrOutlineThicknessSlider.isEnabled() && volVarMembrOutlineThicknessSlider.getValue() == 0){
							exportInfoJTextArea.append("Hiding Membrane Outlines for Volume variables\n");
						}else if(volVarMembrOutlineThicknessSlider.isEnabled() && volVarMembrOutlineThicknessSlider.getValue() > 0){
							exportInfoJTextArea.append("Showing Membrane Outlines for Volume variables\n");
						}
						if(membrVarThicknessSlider.isEnabled()){
							exportInfoJTextArea.append("Membrane Thickness: "+membrVarThicknessSlider.getValue()+"\n");
						}
						exportInfoJTextArea.append("Variable Mirroring: "+mirrorComboBox.getSelectedItem()+"\n");
						exportInfoJTextArea.append("Display Scaling: "+imageScale+(meshMode==ImagePaneModel.MESH_MODE?" ("+scalingCombobox.getSelectedItem()+")":"")+"\n");
						exportInfoJTextArea.append("\nVariable Display Preferences:\n");
						for (int i = 0; i < variableNames.length; i++) {
							boolean bDefaultScaleRange = displayPreferences[i].getScaleSettings() == null;
							boolean bSpecialColorsCustom =
								!Arrays.equals(DisplayAdapterService.createGraySpecialColors(), displayPreferences[i].getSpecialColors()) &&
								!Arrays.equals(DisplayAdapterService.createBlueRedSpecialColors(), displayPreferences[i].getSpecialColors());
							exportInfoJTextArea.append(
								"'"+variableNames[i]+"':\n"+
								"     ColorScheme: "+(displayPreferences[i].isGrayScale()?"Gray":"Color")+" (click 'Gray' or 'BlueRed' in 'Results Viewer' to change)\n"+
								"     Value Scale Range: "+(bDefaultScaleRange?"Min/Max each timepoint (uncheck 'Auto(current time)' in 'Results Viewer' to change)":"User Defined->"+displayPreferences[i].getScaleSettings())+"\n"+
								"     Special Colors: "+(bSpecialColorsCustom?"User Defined":"Default (right-click 'BM AM NN ND NR' color bar in 'Results Viewer' to change)")+"\n"+
								"     Domain Infomation: "+(displayPreferences[i].getDomainValid()==null?"False":"True")+"\n"
							);
						}
					}
				}
				jScrollPane.setViewportView(exportInfoJTextArea);
				exportInfoJTextArea.setCaretPosition(0);//reset scroll to beginning
				if(DialogUtils.showComponentOKCancelDialog(MediaSettingsPanel.this, jScrollPane,"Export Information") != JOptionPane.OK_OPTION){
					return;
				}
				//Convert continue to OK if user satisfied
				e = new ActionEvent(e.getSource(), e.getID(), OK_ACTION_COMMAND, e.getWhen(), e.getModifiers());
			}

			for (int i = 0; i < actionListenerV.size(); i++) {
				actionListenerV.elementAt(i).actionPerformed(e);
			}
		}
	};
	private void init(){
		encodeFormatButtonGroup.add(encodeFormatJPG);
		encodeFormatButtonGroup.add(encodeFormatGIF);
		encodeFormatJPG.setSelected(true);
		
		compositionButtonGroup.add(compositionSeparate);
		compositionButtonGroup.add(compositionCombined);
		compositionSeparate.setSelected(true);
		
		qtFormatButtonGroup.add(qtFormatRegular);
		qtFormatButtonGroup.add(qtFormatQTVR);
		qtFormatRegular.setSelected(true);
		
		mirrorComboBox.addItem("No mirroring");
		mirrorComboBox.addItem("Mirror left");
		mirrorComboBox.addItem("Mirror top");
		mirrorComboBox.addItem("Mirror right");
		mirrorComboBox.addItem("Mirror bottom");

		scalingCombobox.setModel(new DefaultComboBoxModel<String>(new String[] {MESH_MODE_TEXT,"1","2","3","4","5","6","7","8","9","10"}));

		compressionSlider.setValue(10);
		compressionSlider.setMajorTickSpacing(1);
		compressionSlider.setMaximum(10);
		compressionSlider.setPaintTicks(true);
		Hashtable<Integer, JLabel> compressionSliderLabelTable = new Hashtable<Integer, JLabel>();
		compressionSliderLabelTable.put( new Integer( 0 ), new JLabel("high") );
		compressionSliderLabelTable.put( new Integer( compressionSlider.getMaximum()/2 ), new JLabel("medium") );
		compressionSliderLabelTable.put( new Integer( compressionSlider.getMaximum() ), new JLabel("lossless") );
		compressionSlider.setLabelTable( compressionSliderLabelTable );
		compressionSlider.setPaintLabels(true);

		volVarMembrOutlineThicknessSlider.setValue(1);
		volVarMembrOutlineThicknessSlider.setMajorTickSpacing(2);
		volVarMembrOutlineThicknessSlider.setMaximum(20);
		volVarMembrOutlineThicknessSlider.setPaintTicks(true);
		Hashtable<Integer, JLabel> volVarOutlineLabelTable = new Hashtable<Integer, JLabel>();
		volVarOutlineLabelTable.put( new Integer( 0 ), new JLabel("Hide") );
		for (int i = 2; i <= 20; i+= 2) {
			volVarOutlineLabelTable.put( new Integer( i ), new JLabel(i+"") );
		}
		volVarMembrOutlineThicknessSlider.setLabelTable( volVarOutlineLabelTable );
		volVarMembrOutlineThicknessSlider.setPaintLabels(true);
		
		membrVarThicknessSlider.setValue(1);
		membrVarThicknessSlider.setMajorTickSpacing(2);
		membrVarThicknessSlider.setMinimum(1);
		membrVarThicknessSlider.setMaximum(20);
		membrVarThicknessSlider.setPaintTicks(true);
		membrVarThicknessSlider.setPaintLabels(true);

		
		continueButton.addActionListener(buttonActionListener);
		cancelButton.addActionListener(buttonActionListener);
		
		movieDurationTextField.setText("10");
		
		particleButtonGroup.add(particleRadioButton);
		particleButtonGroup.add(particleCountsRadiobutton);
		
		particleRadioButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				configureGUI();
			}
		});

	}
	
	private static final double MOVIE_DURATION_MIN_SECONDS = .1;
	private static final double MOVIE_DURATION_MAX_SECONDS = 1000.0;
	private JLabel compressionLabel;
	private JLabel qtLabel;
	private JLabel volvarMembrOutlineLabel;
	public double getDuration() {
		String typed = movieDurationTextField.getText();
		double seconds = new Double(typed).doubleValue();
		if (seconds < MOVIE_DURATION_MIN_SECONDS) seconds = MOVIE_DURATION_MIN_SECONDS;
		if (seconds > MOVIE_DURATION_MAX_SECONDS) seconds = MOVIE_DURATION_MAX_SECONDS;
		int milliseconds = (int)(seconds * 1000);
		seconds = (double) milliseconds / 1000;
		movieDurationTextField.setText(Double.toString(seconds));
		return milliseconds;
	}

	private static void jSliderEnable(boolean bEnable,JSlider jslider){
		jslider.setEnabled(bEnable);
		@SuppressWarnings("unchecked") //JSlider uses non-generic legacy interface.
		Dictionary<Integer, JLabel> labeltable = jslider.getLabelTable();
		if(labeltable != null){
			Enumeration<JLabel> jLabelEnum = labeltable.elements();
			while(jLabelEnum.hasMoreElements()){
				jLabelEnum.nextElement().setEnabled(bEnable);
			}
		}
		jslider.repaint();
	}

	private ExportFormat mediaType;
	private boolean selectionHasVolumeVars;
	private boolean selectionHasMembraneVars;
	private boolean bMovie;
	public void configure(ExportFormat mediaType,boolean selectionHasVolumeVars,boolean selectionHasMembraneVars){
		this.mediaType = mediaType;
		this.selectionHasVolumeVars = selectionHasVolumeVars;
		this.selectionHasMembraneVars = selectionHasMembraneVars;
		configureGUI();
	}
	private void configureGUI(){
		bMovie = mediaType == ExportFormat.QUICKTIME || mediaType == ExportFormat.ANIMATED_GIF;
		boolean bParticle = isSmoldyn && particleRadioButton.isSelected();
		jSliderEnable(!bParticle && (mediaType == ExportFormat.QUICKTIME || mediaType == ExportFormat.FORMAT_JPEG),compressionSlider);
		compressionLabel.setEnabled(compressionSlider.isEnabled());
		
		lblEncodingFormat.setEnabled(!bParticle);
		encodeFormatGIF.setEnabled(!bParticle &&(mediaType == ExportFormat.GIF || mediaType == ExportFormat.ANIMATED_GIF));
		encodeFormatJPG.setEnabled(!bParticle && (mediaType == ExportFormat.FORMAT_JPEG || mediaType == ExportFormat.QUICKTIME));
		encodeFormatGIF.setSelected(encodeFormatGIF.isEnabled());
		encodeFormatJPG.setSelected(encodeFormatJPG.isEnabled());
		
		qtFormatRegular.setEnabled(mediaType == ExportFormat.QUICKTIME);
		qtFormatQTVR.setEnabled(mediaType == ExportFormat.QUICKTIME);
		qtLabel.setEnabled(mediaType == ExportFormat.QUICKTIME);
		
		jSliderEnable(selectionHasVolumeVars, volVarMembrOutlineThicknessSlider);
		volvarMembrOutlineLabel.setEnabled(selectionHasVolumeVars);
		
		jSliderEnable(selectionHasMembraneVars, membrVarThicknessSlider);
		membrVarThicknessLabel.setEnabled(selectionHasMembraneVars);
		
		movieDurationLabel.setEnabled(!bParticle && bMovie);
		movieDurationTextField.setEnabled(movieDurationLabel.isEnabled());
		
		compositionLabel.setEnabled((variableNames == null || variableNames.length > 1?true:false));
		compositionSeparate.setEnabled(compositionLabel.isEnabled());
		compositionCombined.setEnabled(compositionLabel.isEnabled());
		
		particleModeLabel.setEnabled(isSmoldyn);
		particleRadioButton.setEnabled(isSmoldyn);
		particleCountsRadiobutton.setEnabled(isSmoldyn);
		if(isSmoldyn){
			if(particleRadioButton.isSelected()){
				jSliderEnable(false, volVarMembrOutlineThicknessSlider);
				volvarMembrOutlineLabel.setEnabled(false);
				
				jSliderEnable(false, membrVarThicknessSlider);
				membrVarThicknessLabel.setEnabled(false);
				
				compositionLabel.setEnabled(false);
				compositionSeparate.setEnabled(compositionLabel.isEnabled());
				compositionCombined.setEnabled(compositionLabel.isEnabled());
	
				qtFormatRegular.setEnabled(false);
				qtFormatQTVR.setEnabled(false);
				qtLabel.setEnabled(false);
	
				lblMirroring.setEnabled(false);
				mirrorComboBox.setEnabled(false);
				lblImageScale.setEnabled(false);
				scalingCombobox.setEnabled(false);
			}else{
				lblMirroring.setEnabled(true);
				mirrorComboBox.setEnabled(true);
				lblImageScale.setEnabled(true);
				scalingCombobox.setEnabled(true);
			}
		}
	}
	
	private int sliceCount = 0;
	public void setSliceCount(int sliceCount){
		this.sliceCount = sliceCount;
	}
	
	private DisplayPreferences[] displayPreferences;
	private String[] variableNames;
	private int viewZoom;
	private JPanel panel_4;
	private JButton continueButton;
	private JButton cancelButton;
	private JLabel movieDurationLabel;
	private JLabel compositionLabel;
	public void setDisplayPreferences(DisplayPreferences[] displayPreferences,String[] variableNames,int viewZoom){
		this.displayPreferences = displayPreferences;
		this.variableNames = variableNames;
		this.viewZoom = viewZoom;
	}
	
	private TimeSpecs timeSpecs;
	public void setTimeSpecs(TimeSpecs timeSpecs){
		this.timeSpecs = timeSpecs;
	}
	
	private CartesianMesh mesh;
	private int normalAxis;
	private JSlider volVarMembrOutlineThicknessSlider;
	private JSlider membrVarThicknessSlider;
	public void setImageSizeCalculationInfo(CartesianMesh mesh,int normalAxis){
		this.mesh = mesh;
		this.normalAxis = normalAxis;
	}
	private boolean isSmoldyn = false;
	private JLabel particleModeLabel;
	private JPanel panel_5;
	private JRadioButton particleRadioButton;
	private JRadioButton particleCountsRadiobutton;
	public void setIsSmoldyn(boolean isSmoldyn){
		this.isSmoldyn = isSmoldyn;
	}
	public FormatSpecificSpecs getSpecs() {
		int scaleMode = ImagePaneModel.MESH_MODE;
		if(!scalingCombobox.getSelectedItem().equals(MESH_MODE_TEXT)){
			scaleMode = ImagePaneModel.NORMAL_MODE;
		}
		float compressionQuality =
			(compressionSlider.isEnabled()?(float)compressionSlider.getValue()/(float)compressionSlider.getMaximum():1.0f);
		int imageScaling = (scalingCombobox.getSelectedItem().equals(MESH_MODE_TEXT)?viewZoom:Integer.valueOf(scalingCombobox.getSelectedItem().toString()));
		int membrScaling = (membrVarThicknessSlider.isEnabled()?membrVarThicknessSlider.getValue():0);
		boolean bOverLay = compositionCombined.isEnabled() && compositionCombined.isSelected();
		int volVarMembrOutlineThickness =
			(volVarMembrOutlineThicknessSlider.isEnabled()?volVarMembrOutlineThicknessSlider.getValue():0);
		int particleMode =
			(isSmoldyn && !particleCountsRadiobutton.isSelected()?FormatSpecificSpecs.PARTICLE_SELECT:FormatSpecificSpecs.PARTICLE_NONE);
		
		if(mediaType == ExportFormat.QUICKTIME){
			return new MovieSpecs(
				duration,
				bOverLay,
				displayPreferences,
				(encodeFormatGIF.isSelected()?ExportFormat.GIF:ExportFormat.FORMAT_JPEG),
				mirrorComboBox.getSelectedIndex(),
				volVarMembrOutlineThickness,
				imageScaling,
				membrScaling,
				scaleMode,
				FormatSpecificSpecs.CODEC_JPEG,
				compressionQuality,
				qtFormatQTVR.isSelected(),
				particleMode
			);
		}else{
			return new ImageSpecs(
					displayPreferences,
					mediaType,
					(encodeFormatGIF.isSelected()?ExportConstants.COMPRESSED_GIF_DEFAULT:ExportConstants.COMPRESSED_JPEG_DEFAULT),
					mirrorComboBox.getSelectedIndex(),
					duration,
					0/*Infinite*/,
					volVarMembrOutlineThickness, 
					imageScaling, 
					membrScaling, 
					scaleMode, 
					compressionQuality,
					bOverLay,
					particleMode
				);
		}
	}
	

}

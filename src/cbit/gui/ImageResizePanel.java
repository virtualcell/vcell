/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.vcell.util.ISize;

import cbit.vcell.client.ClientRequestManager;

public class ImageResizePanel extends JPanel {
	private static final String MERGE_CHKBOX_TEXT = "Merge All Channels to One?";
	private JLabel lblNewLabel;
	private JLabel ImageNameLabel;
	private JLabel lblNewLabel_2;
	private JLabel originalSizeLabel;
	private JLabel lblNewLabel_4;
	private JLabel newSizeLabel;
	private JLabel lblNewLabel_6;
	private JSlider resizeSlider;
	public ImageResizePanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0,1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0,0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		lblNewLabel = new JLabel("Image Name:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);
		
		ImageNameLabel = new JLabel("New label");
		GridBagConstraints gbc_ImageNameLabel = new GridBagConstraints();
		gbc_ImageNameLabel.anchor = GridBagConstraints.WEST;
		gbc_ImageNameLabel.insets = new Insets(0, 0, 5, 0);
		gbc_ImageNameLabel.gridx = 1;
		gbc_ImageNameLabel.gridy = 0;
		add(ImageNameLabel, gbc_ImageNameLabel);
		
		lblNewLabel_2 = new JLabel("Original Size (x,y,z):");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 1;
		add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		originalSizeLabel = new JLabel("New label");
		GridBagConstraints gbc_originalSizeLabel = new GridBagConstraints();
		gbc_originalSizeLabel.anchor = GridBagConstraints.WEST;
		gbc_originalSizeLabel.insets = new Insets(0, 0, 5, 0);
		gbc_originalSizeLabel.gridx = 1;
		gbc_originalSizeLabel.gridy = 1;
		add(originalSizeLabel, gbc_originalSizeLabel);
		
		lblNewLabel_4 = new JLabel("Reduced Size (x,y,z):");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 2;
		add(lblNewLabel_4, gbc_lblNewLabel_4);
		
		newSizeLabel = new JLabel("New label");
		GridBagConstraints gbc_newSizeLabel = new GridBagConstraints();
		gbc_newSizeLabel.anchor = GridBagConstraints.WEST;
		gbc_newSizeLabel.insets = new Insets(0, 0, 5, 0);
		gbc_newSizeLabel.gridx = 1;
		gbc_newSizeLabel.gridy = 2;
		add(newSizeLabel, gbc_newSizeLabel);
		
		lblNewLabel_6 = new JLabel("Move slider to reduce X,Y size (% of original image)");
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_6.gridwidth = 2;
		gbc_lblNewLabel_6.gridx = 0;
		gbc_lblNewLabel_6.gridy = 3;
		add(lblNewLabel_6, gbc_lblNewLabel_6);
		
		resizeSlider = new JSlider();
		resizeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(resizeSlider.getValue() == 0){
					resizeSlider.setValue(1);
					return;
				}
				resizeChanged();
			}
		});
		resizeSlider.setValue(100);
		resizeSlider.setPaintTicks(true);
		resizeSlider.setPaintLabels(true);
		resizeSlider.setMajorTickSpacing(10);
		GridBagConstraints gbc_resizeSlider = new GridBagConstraints();
		gbc_resizeSlider.insets = new Insets(0, 0, 5, 0);
		gbc_resizeSlider.weightx = 1.0;
		gbc_resizeSlider.fill = GridBagConstraints.HORIZONTAL;
		gbc_resizeSlider.gridwidth = 2;
		gbc_resizeSlider.gridx = 0;
		gbc_resizeSlider.gridy = 4;
		add(resizeSlider, gbc_resizeSlider);
		
		channelLabel = new JLabel("Channel Count:");
		GridBagConstraints gbc_channelLabel = new GridBagConstraints();
		gbc_channelLabel.anchor = GridBagConstraints.EAST;
		gbc_channelLabel.insets = new Insets(0, 0, 5, 5);
		gbc_channelLabel.gridx = 0;
		gbc_channelLabel.gridy = 5;
		add(channelLabel, gbc_channelLabel);
		
		panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.anchor = GridBagConstraints.WEST;
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 5;
		add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0};
		gbl_panel_1.rowHeights = new int[]{0};
		gbl_panel_1.columnWeights = new double[]{1.0, 0.0};
		gbl_panel_1.rowWeights = new double[]{0.0};
		panel_1.setLayout(gbl_panel_1);
		
		channelCountLabel = new JLabel("ChanlCount");
		GridBagConstraints gbc_channelCountLabel = new GridBagConstraints();
		gbc_channelCountLabel.anchor = GridBagConstraints.WEST;
		gbc_channelCountLabel.insets = new Insets(0, 0, 0, 5);
		gbc_channelCountLabel.gridx = 0;
		gbc_channelCountLabel.gridy = 0;
		panel_1.add(channelCountLabel, gbc_channelCountLabel);
		
		mergeCheckBox = new JCheckBox(MERGE_CHKBOX_TEXT);
		GridBagConstraints gbc_mergeCheckBox = new GridBagConstraints();
		gbc_mergeCheckBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_mergeCheckBox.anchor = GridBagConstraints.WEST;
		gbc_mergeCheckBox.weightx = 1.0;
		gbc_mergeCheckBox.gridx = 1;
		gbc_mergeCheckBox.gridy = 0;
		panel_1.add(mergeCheckBox, gbc_mergeCheckBox);
		mergeCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resizeChanged();
			}
		});
		
		timepointsLabel = new JLabel("TimePoints:");
		GridBagConstraints gbc_timepointsLabel = new GridBagConstraints();
		gbc_timepointsLabel.anchor = GridBagConstraints.EAST;
		gbc_timepointsLabel.insets = new Insets(0, 0, 0, 5);
		gbc_timepointsLabel.gridx = 0;
		gbc_timepointsLabel.gridy = 6;
		add(timepointsLabel, gbc_timepointsLabel);
		
		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.weightx = 1.0;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 6;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0,0};
		gbl_panel.rowHeights = new int[]{0};
		gbl_panel.columnWeights = new double[]{0.0,1.0};
		gbl_panel.rowWeights = new double[]{0.0};
		panel.setLayout(gbl_panel);
		
		timepointsComboBox = new JComboBox();
		GridBagConstraints gbc_timepointsComboBox = new GridBagConstraints();
		gbc_timepointsComboBox.weightx = 1.0;
		gbc_timepointsComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_timepointsComboBox.anchor = GridBagConstraints.WEST;
		gbc_timepointsComboBox.insets = new Insets(0, 0, 0, 5);
		gbc_timepointsComboBox.gridx = 0;
		gbc_timepointsComboBox.gridy = 0;
		panel.add(timepointsComboBox, gbc_timepointsComboBox);
		
		timepointdescrLabel = new JLabel("Select a TimePoint to import");
		GridBagConstraints gbc_timepointdescrLabel = new GridBagConstraints();
		gbc_timepointdescrLabel.anchor = GridBagConstraints.WEST;
		gbc_timepointdescrLabel.gridx = 1;
		gbc_timepointdescrLabel.gridy = 0;
		panel.add(timepointdescrLabel, gbc_timepointdescrLabel);
	}

	private ClientRequestManager.ImageSizeInfo origImageSizeInfo;
	private ClientRequestManager.ImageSizeInfo newImageSizeInfo;
	private JLabel channelLabel;
	private JCheckBox mergeCheckBox;
	private JLabel timepointsLabel;
	private JPanel panel;
	private JComboBox timepointsComboBox;
	private JLabel timepointdescrLabel;
	private JPanel panel_1;
	private JLabel channelCountLabel;
	public void init(ClientRequestManager.ImageSizeInfo origImageSizeInfo){
		this.origImageSizeInfo = origImageSizeInfo;
		ImageNameLabel.setText(origImageSizeInfo.getImagePath());
		originalSizeLabel.setText(origImageSizeInfo.getiSize().toString());
		for (int i = 0; i < origImageSizeInfo.getTimePoints().length; i++) {
			timepointsComboBox.insertItemAt(new Double(origImageSizeInfo.getTimePoints()[i]), i);
		}
		timepointsComboBox.setSelectedIndex(0);
		timepointsComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resizeChanged();
			}
		});
		resizeChanged();
		mergeCheckBox.setText(MERGE_CHKBOX_TEXT);
		channelCountLabel.setText("("+origImageSizeInfo.getNumChannels()+")");
		if(origImageSizeInfo.getNumChannels() == 1){
//			channelLabel.setEnabled(false);
			mergeCheckBox.setEnabled(false);
		}
		if(origImageSizeInfo.getTimePoints().length == 1){
			timepointdescrLabel.setEnabled(false);
			timepointsLabel.setEnabled(false);
			timepointsComboBox.setEnabled(false);
		}
	}
	private void resizeChanged(){
		if(origImageSizeInfo == null){
			return;
		}
		ISize newISize = new ISize(origImageSizeInfo.getiSize().getX()*resizeSlider.getValue()/100, origImageSizeInfo.getiSize().getY()*resizeSlider.getValue()/100, origImageSizeInfo.getiSize().getZ());
		newImageSizeInfo =
				new ClientRequestManager.ImageSizeInfo(origImageSizeInfo.getImagePath(), newISize, (mergeCheckBox.isSelected()?1:origImageSizeInfo.getNumChannels()),origImageSizeInfo.getTimePoints(),timepointsComboBox.getSelectedIndex());
		if(!newISize.compareEqual(origImageSizeInfo.getiSize())){
			newSizeLabel.setText("<html><font color=red>"+newISize.toString()+"</font></html>");
		}else{
			newSizeLabel.setText(newISize.toString());
		}
	}
	public ClientRequestManager.ImageSizeInfo getNewImageSizeInfo(){
		return newImageSizeInfo;
	}
}

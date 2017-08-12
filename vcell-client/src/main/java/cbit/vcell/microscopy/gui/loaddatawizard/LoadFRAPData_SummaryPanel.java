/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui.loaddatawizard;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.vcell.util.BeanUtils;
import org.vcell.util.NumberUtils;

import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.microscopy.DataVerifyInfo;
import cbit.vcell.microscopy.FRAPStudy;

@SuppressWarnings("serial")
public class LoadFRAPData_SummaryPanel extends JPanel
{
	public final String loadSuccessInfo = "Data loaded. Please verify/modify the following information.";
	public final String loadFailedInfo= "Data loading failed.";
	private JTextField imgPixelSizeY = null;
	private JTextField imgPixelSizeX = null;
	private JComboBox eTimeCombo = null;
	private JComboBox sTimeCombo = null;
	private JLabel totTimeLabel = null;
	private JPanel timePanel = null;
	private JPanel sizePanel = null;
	private JLabel loadInfo = null;
	private JLabel numXPixelsValLabel;
	private JLabel numYPixelsValLabel;
	public LoadFRAPData_SummaryPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,7,7,0,7,7,7,7};
		setLayout(gridBagLayout);

		loadInfo = new JLabel();
		loadInfo.setForeground(new Color(0, 0, 128));
		loadInfo.setFont(new Font("", Font.BOLD | Font.ITALIC, 15));
		loadInfo.setText(loadSuccessInfo);
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.gridx = 0;
		add(loadInfo, gridBagConstraints1);
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		gridBagConstraints2.gridy = 4;
		gridBagConstraints2.gridheight = 2;
		gridBagConstraints2.gridx = 0;
		add(getTimePanel(), gridBagConstraints2);
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints3.ipadx = 175;
		gridBagConstraints3.anchor = GridBagConstraints.WEST;
		gridBagConstraints3.gridy = 10;
		gridBagConstraints3.gridheight = 4;
		gridBagConstraints3.gridx = 0;
		add(getSizePanel(), gridBagConstraints3);
	}

	public JPanel getTimePanel()
	{
		if(timePanel == null)
		{
			final GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.rowHeights = new int[] {0,7,7};
			gridBagLayout.columnWidths = new int[] {7,7,7,0,0,7,0,7,7,7,0,0,7};
			timePanel = new JPanel(gridBagLayout);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.gridx = 1;
			timePanel.add(new JLabel("Image starts at:"), gridBagConstraints1);

			sTimeCombo = new JComboBox();
			final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
			gridBagConstraints_5.gridy = 0;
			gridBagConstraints_5.gridx = 3;
			timePanel.add(sTimeCombo, gridBagConstraints_5);
			sTimeCombo.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					updateTimeLabel();
				}
			});

			final JLabel sLabel1 = new JLabel();
			sLabel1.setText(" s");
			final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
			gridBagConstraints_7.gridy = 0;
			gridBagConstraints_7.gridx = 4;
			timePanel.add(sLabel1, gridBagConstraints_7);

			final JLabel eTimeLabel = new JLabel();
			eTimeLabel.setText("Image ends at:");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.anchor = GridBagConstraints.EAST;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.gridx = 8;
//			gridBagConstraints3.gridwidth = 2;
			timePanel.add(eTimeLabel, gridBagConstraints3);

			eTimeCombo = new JComboBox();
			final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
			gridBagConstraints_6.gridy = 0;
			gridBagConstraints_6.gridx = 10;
			timePanel.add(eTimeCombo, gridBagConstraints_6);
			eTimeCombo.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					updateTimeLabel();
				}
			});
			
			final JLabel sLabel2 = new JLabel();
			sLabel2.setText(" s");
			final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
			gridBagConstraints_8.gridy = 0;
			gridBagConstraints_8.gridx = 12;
			timePanel.add(sLabel2, gridBagConstraints_8);

			final JLabel tolTimeLabel = new JLabel();
			tolTimeLabel.setText("Image duration:");
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.gridx = 1;
			timePanel.add(tolTimeLabel, gridBagConstraints);

			totTimeLabel = new JLabel();
			final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
			gridBagConstraints_1.anchor = GridBagConstraints.WEST;
			gridBagConstraints_1.gridy = 1;
			gridBagConstraints_1.gridx = 3;
			timePanel.add(totTimeLabel, gridBagConstraints_1);

			final JLabel sLabel3 = new JLabel();
			sLabel3.setText(" s");
			final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
			gridBagConstraints_9.gridy = 1;
			gridBagConstraints_9.gridx = 4;
			timePanel.add(sLabel3, gridBagConstraints_9);
			timePanel.setBorder(new TitledBorder(new LineBorder(new Color(153, 186,243), 1),"Adjust FRAP Dataset"));
		}
		return timePanel;
	}
	
	public JPanel getSizePanel()
	{
		if(sizePanel == null)
		{
			final GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.rowHeights = new int[] {0,7,7};
			gridBagLayout.columnWidths = new int[] {7,7,7,0,0,7,7,0,7};
			sizePanel = new JPanel(gridBagLayout);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.gridx = 1;
			sizePanel.add(new JLabel("Pixel size X "), gridBagConstraints1);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.gridy = 2;
			gridBagConstraints2.gridx = 1;

			imgPixelSizeX = new JTextField(12);
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridx = 2;
			sizePanel.add(imgPixelSizeX, gridBagConstraints);

			final JLabel umLabel1 = new JLabel();
			umLabel1.setText(" um");
			final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
			gridBagConstraints_2.gridy = 0;
			gridBagConstraints_2.gridx = 4;
			sizePanel.add(umLabel1, gridBagConstraints_2);

			final JLabel imageNumOfLabel = new JLabel();
			imageNumOfLabel.setText("Total Num. of Pixels X:");
			final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
			gridBagConstraints_4.gridy = 0;
			gridBagConstraints_4.gridx = 7;
			sizePanel.add(imageNumOfLabel, gridBagConstraints_4);

			numXPixelsValLabel = new JLabel();
			numXPixelsValLabel.setText("");
			final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
			gridBagConstraints_6.gridy = 0;
			gridBagConstraints_6.gridx = 8;
			sizePanel.add(numXPixelsValLabel, gridBagConstraints_6);
			sizePanel.add(new JLabel("Pixel size Y "), gridBagConstraints2);

			imgPixelSizeY = new JTextField(12);
			final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
			gridBagConstraints_1.gridy = 2;
			gridBagConstraints_1.gridx = 2;
			sizePanel.add(imgPixelSizeY, gridBagConstraints_1);

			final JLabel umLabel2 = new JLabel();
			umLabel2.setText(" um");
			final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
			gridBagConstraints_3.gridy = 2;
			gridBagConstraints_3.gridx = 4;
			sizePanel.add(umLabel2, gridBagConstraints_3);
			sizePanel.setBorder(new TitledBorder(new LineBorder(new Color(153, 186,243), 1),"Adjust FRAP Image Size"));

			final JLabel totalNumOfLabel = new JLabel();
			totalNumOfLabel.setText("Total Num. of Pixels Y:");
			final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
			gridBagConstraints_5.gridy = 2;
			gridBagConstraints_5.gridx = 7;
			sizePanel.add(totalNumOfLabel, gridBagConstraints_5);

			numYPixelsValLabel = new JLabel();
			numYPixelsValLabel.setText("");
			final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
			gridBagConstraints_8.gridy = 2;
			gridBagConstraints_8.gridx = 8;
			sizePanel.add(numYPixelsValLabel, gridBagConstraints_8);

			final JLabel label = new JLabel();
			label.setText("");
			final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
			gridBagConstraints_7.gridy = 2;
			gridBagConstraints_7.gridx = 9;
			sizePanel.add(label, gridBagConstraints_7);
		}
		return sizePanel;
	}
	
	public void setLoadInfo(final FRAPStudy frapStudy)
	{
		if(frapStudy!=null && frapStudy.getFrapData()!=null)
		{
			BeanUtils.enableComponents(getTimePanel(), true);
			BeanUtils.enableComponents(getTimePanel(), true);
			//title
			loadInfo.setText(loadSuccessInfo);
			//time
			sTimeCombo.removeAllItems();
			eTimeCombo.removeAllItems();
			double[] timeSteps = frapStudy.getFrapData().getImageDataset().getImageTimeStamps();
			for(int i =0 ; i<timeSteps.length; i++)
			{
				sTimeCombo.addItem(timeSteps[i] + "");
				eTimeCombo.addItem(timeSteps[i] + "");
			}
			sTimeCombo.setSelectedIndex(0);
			eTimeCombo.setSelectedIndex(timeSteps.length-1);
			totTimeLabel.setText((timeSteps[timeSteps.length-1] - timeSteps[0])+""); 
			ImageDataset imgDataset = frapStudy.getFrapData().getImageDataset();
			int numXPixels = imgDataset.getAllImages()[0].getNumX();
			int numYPixels = imgDataset.getAllImages()[0].getNumY();
			numXPixelsValLabel.setText(numXPixels + "");
			numYPixelsValLabel.setText(numYPixels + "");
			double pixelSizeX = imgDataset.getExtent().getX()/numXPixels;
			double pixelSizeY = imgDataset.getExtent().getY()/numYPixels;
			imgPixelSizeX.setText(NumberUtils.formatNumber(pixelSizeX, 10));
			imgPixelSizeY.setText(NumberUtils.formatNumber(pixelSizeY, 10));
		}
		else
		{
			//title
			loadInfo.setText(loadFailedInfo);
			//time 
			sTimeCombo.removeAllItems();
			sTimeCombo.addItem("     N/A     ");
			eTimeCombo.removeAllItems();
			eTimeCombo.addItem("     N/A     ");
			totTimeLabel.setText("");
			numXPixelsValLabel.setText("");
			numYPixelsValLabel.setText("");
			imgPixelSizeX.setText("");
			imgPixelSizeY.setText("");
			BeanUtils.enableComponents(getSizePanel(), false);
			BeanUtils.enableComponents(getTimePanel(), false);
		}
	}
	
	public String checkInputValidity()
	{
		if(Double.parseDouble((String)sTimeCombo.getSelectedItem()) > Double.parseDouble((String)eTimeCombo.getSelectedItem()))
		{
			return "Starting time should NOT be greater than ending time.";
		}
		try{
			Double.parseDouble(imgPixelSizeX.getText());
		}catch(NumberFormatException e)
		{
			return "Image size X input error " + e.getMessage();
		}
		try{
			Double.parseDouble(imgPixelSizeY.getText());
		}catch(NumberFormatException e)
		{
			return "Image size Y input error " + e.getMessage();
		}
		return "";
	}
	
	public DataVerifyInfo saveDataInfo()
	{
		if(loadInfo.getText().equals(loadSuccessInfo)) //loaded successfully
		{
			double imgSizeX = Double.parseDouble(imgPixelSizeX.getText()) * Double.parseDouble(numXPixelsValLabel.getText());
			double imgSizeY = Double.parseDouble(imgPixelSizeY.getText()) * Double.parseDouble(numYPixelsValLabel.getText());
			return new DataVerifyInfo(Double.parseDouble((String)sTimeCombo.getSelectedItem()), Double.parseDouble((String)eTimeCombo.getSelectedItem()),
									  imgSizeX, imgSizeY, sTimeCombo.getSelectedIndex(), eTimeCombo.getSelectedIndex());
		}
		return null;
	}
	
	private void updateTimeLabel() {
		String stString = (String)sTimeCombo.getSelectedItem();
		String etString = (String)eTimeCombo.getSelectedItem();
		if(stString != null && etString != null)
		{
			totTimeLabel.setText(( Double.parseDouble(etString) - Double.parseDouble(stString) )+"");
		}
		else
		{
			totTimeLabel.setText("");
		}
		
	}
	
}

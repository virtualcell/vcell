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

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;

/*
 * This dialog is used to input file series.
 * The file series are either time series or Z series.
 * For time series, the total time span and the index for recovery need to be specified.
 */
@SuppressWarnings("serial")
public class LoadFRAPData_MultiFilePanel extends JPanel implements ActionListener
{
	VirtualFrapMainFrame parent = null;
	
	private JLabel tSampleIntervalLabel = null;
	private JTextField tSampleIntervalTextField = null;
	private JTextArea fileTextArea = null;
	private JScrollPane scroll = null;
	private JButton inputFileButton = null;
	
	private File[] files = null;
	private double tInterval;
		
	public LoadFRAPData_MultiFilePanel(VirtualFrapMainFrame arg_parent)
	{
		super();
		parent = arg_parent;
		initialize();
	}
	public LoadFRAPData_MultiFilePanel()//constructor for wizard
	{
		super();
//		parent = arg_parent;
		initialize();
	}
	private void initialize()
	{
	    setLayout(new BorderLayout());

		final JLabel timeSeriesLabel = new JLabel();
		timeSeriesLabel.setText("  Time Series");
		tSampleIntervalLabel = new JLabel("  Time Sample Intv. (s)");
		tSampleIntervalTextField = new JTextField(8);
		tSampleIntervalTextField.setText("0.01");
		
		fileTextArea = new JTextArea(20,10);
		fileTextArea.setEditable(false);
		scroll = new JScrollPane(fileTextArea);
		scroll.setAutoscrolls(true);
		inputFileButton = new JButton("Choose Files");
		inputFileButton.addActionListener(this);
	    
		//separate to leftPanel, rightPanel and botPanel
		//leftPanel puts those buttons and textfields
		//rightPanel puts testarea and the inputfile button
		//botPanel puts ok and cancel buttons
		JPanel leftPanel = new JPanel(new GridLayout(0,1));
		leftPanel.add(timeSeriesLabel);
	    JPanel p1 = new JPanel(new GridLayout(0,2)); // put time interval label and text field togeter
		p1.add(tSampleIntervalLabel);
		p1.add(tSampleIntervalTextField);
		leftPanel.add(p1);
		leftPanel.add(new JLabel(""));
		p1 = new JPanel(new GridLayout(0,2)); // put z interval label and text field together
		leftPanel.add(p1);
				
		JPanel rightPanel = new JPanel(new BorderLayout());
		JPanel fileTextPanel = new JPanel(new BorderLayout());
		fileTextPanel.add(scroll, BorderLayout.CENTER);
		fileTextPanel.setBorder(new TitledBorder(new EtchedBorder(),"Files to be input:", TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 11)));
		rightPanel.add(fileTextPanel, BorderLayout.CENTER);
		JPanel p2 = new JPanel(new BorderLayout());
		p2.add(new JLabel(" "), BorderLayout.WEST);
		p2.add(inputFileButton, BorderLayout.CENTER);
		p2.add(new JLabel(" "), BorderLayout.EAST);
		rightPanel.add(p2, BorderLayout.SOUTH);
		
		// put leftPanel and rightPanel together
		JPanel upPanel = new JPanel(new GridLayout(0,2));
		upPanel.add(leftPanel);
		upPanel.add(rightPanel);
		
		//create botPanel
		JPanel botPanel = new JPanel(new GridLayout(0,1));
		botPanel.add(new JLabel(" "));
				
		add(upPanel, BorderLayout.CENTER);
		add(botPanel, BorderLayout.SOUTH);
		    
//	    setSize(440,200);
	}

	public void actionPerformed(ActionEvent evt)
	{
		if(evt.getSource() instanceof JButton && ((JButton)evt.getSource()).equals(inputFileButton))
		{
			int option = VirtualFrapLoader.multiOpenFileChooser.showOpenDialog(this);
			if (option == JFileChooser.APPROVE_OPTION) 
			{
			    files = VirtualFrapLoader.multiOpenFileChooser.getSelectedFiles();
			    String fileString = "";
				for(int i =0; i < files.length; i++)
				{
					fileString = fileString + files[i].getName()+"\n";
				}
				fileTextArea.setText(fileString);
			}
		}
		//ok and cancel button actions
//		else if(evt.getSource() instanceof JButton && ((JButton)evt.getSource()).equals(okButton))
//		{
//			if(checkValidity().equals(""))
//			{
//				if(files != null && files.length > 0)
//				{
//					try{
//						//need to be changed later, coz there is not z series any more, only time series
//						VirtualFrapMainFrame.frapStudyPanel.load(files, new FRAPStudyPanel.MultiFileImportInfo(true, tInterval, zInterval));
//					}catch(Exception e){
//						PopupGenerator.showErrorDialog(e.getMessage());
//					}
//				}
//				this.setVisible(false);
//			}
//			else
//			{
//				DialogUtils.showErrorDialog("Error: " + checkValidity());
//			}
//		}
//		else if(evt.getSource() instanceof JButton && ((JButton)evt.getSource()).equals(cancelButton))
//		{
//			this.setVisible(false);
//		}
	}
	
	public File[] getSelectedFiles()
	{
		return files;
	}
	
	public boolean isTimeSeries()
	{
		return true; //deal with time series only currently.
	}
	
	public double getTimeInterval() throws NumberFormatException
	{
		try{
			tInterval = Double.parseDouble(tSampleIntervalTextField.getText());
			return tInterval;
		}catch(Exception e)
		{
			throw new NumberFormatException("Time interval input error:\n " + e.getMessage()); 
		}
	}
}

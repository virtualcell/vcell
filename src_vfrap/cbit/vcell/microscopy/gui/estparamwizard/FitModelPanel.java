/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui.estparamwizard;

import java.awt.BorderLayout;

import org.vcell.util.gui.BoxPanel;

@SuppressWarnings("serial")
public class FitModelPanel extends BoxPanel
{
	private FitModelRadioButtonPanel radioPanel;
	public FitModelPanel() {
		super("Choose the Model that Fits Your Data");
		radioPanel = new FitModelRadioButtonPanel();
	    contentPane.setLayout(new BorderLayout());
	    contentPane.add(radioPanel, BorderLayout.WEST);
	}
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			FitModelPanel aPanel = new FitModelPanel();
			frame.setContentPane(aPanel);
//			frame.pack();
			frame.setSize(900,800);
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
			
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}
	
	public void setBestModelRadioButton(int bestModel)
	{
		radioPanel.setBestModelRadioButton(bestModel);
	}
	
	public int getBestModelIndex()
	{
		return radioPanel.getBestModelIndex();
	}
	
	public void disableAllRadioButtons()
	{
		radioPanel.disableAllRadioButtons();
	}
	
	public void enableRadioButton(int modelIdx)
	{
		radioPanel.enableRadioButton(modelIdx);
	}
	
}

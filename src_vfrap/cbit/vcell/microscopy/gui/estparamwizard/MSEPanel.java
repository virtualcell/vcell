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

import javax.swing.BoxLayout;

import org.vcell.util.gui.BoxPanel;

import cbit.vcell.microscopy.FRAPSingleWorkspace;

@SuppressWarnings("serial")
public class MSEPanel extends BoxPanel
{
	private MSETablePanel mseTablePanel;
	private FRAPSingleWorkspace frapWorkspace = null;
	public MSEPanel() {
		super("Squared Error among Available Models under Selected ROIs");
			mseTablePanel = new MSETablePanel(this);
	        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
	        contentPane.add(mseTablePanel);
	}
	
	public FRAPSingleWorkspace getFrapWorkspace()
    {
    	return frapWorkspace;
    }
    
    public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace)
	{
		this.frapWorkspace = frapWorkspace;
		mseTablePanel.setFrapWorkspace(frapWorkspace);
	}
	
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			MSEPanel aPanel = new MSEPanel();
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
}

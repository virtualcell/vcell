/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.documentation;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.help.HelpSet;
import javax.help.JHelp;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.vcell.util.BeanUtils;

import cbit.vcell.client.ChildWindowManager.ChildWindow;

/**
 * This helpviewer enables navigate virtual frap help through table of contents. 
 * The contents are displayed as html files which enable hyperlinks.
 * In addition, the helpviewer provides word search.
 * JavaHelp map, TOC, index, and HelpSet files have to be created to make helpviewer work. 
 * @author Tracy LI
 * Created in June 2008.
 * @version 1.0
 */
@SuppressWarnings("serial")
public class VcellHelpViewer extends JPanel 
{
	public static final int DEFAULT_HELP_DIALOG_WIDTH = 900;
	public static final int DEFAULT_HELP_DIALOG_HEIGHT = 700;
	
	public static final String VFRAP_DOC_URL = "/doc/HelpSet.hs";
	public static final String VCELL_DOC_URL = "/vcellDoc/HelpSet.hs";
	
	private JButton btnCloseHelp;
	private ChildWindow closeableWindow;
	
	public void setCloseMyParent(ChildWindow closeableWindow){
		this.closeableWindow = closeableWindow;
		if(closeableWindow != null){
			btnCloseHelp.setVisible(true);
		}
	}
	public VcellHelpViewer(String docUrl) {
		URL resourceURL = VcellHelpViewer.class.getResource(docUrl);

		HelpSet hs;
		try {
			// get the system class loader
			ClassLoader cl = this.getClass().getClassLoader();
			// create helpset
			hs = new HelpSet(cl, resourceURL);
			JHelp jhelp = new JHelp(hs);
			setLayout(new BorderLayout());
			add(jhelp);
			BeanUtils.addCloseWindowKeyboardAction(this);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		setPreferredSize(new Dimension(DEFAULT_HELP_DIALOG_WIDTH,DEFAULT_HELP_DIALOG_HEIGHT));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnCloseHelp = new JButton("Close");
		btnCloseHelp.setVisible(false);
		btnCloseHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(closeableWindow != null){
					closeableWindow.close();
					closeableWindow = null;
				}
			}
		});
		panel.add(btnCloseHelp);
	}
	
	public static void main(String[] args){
		VcellHelpViewer helpViewer = new VcellHelpViewer(VcellHelpViewer.VCELL_DOC_URL);
		JFrame frame = new JFrame("Virtual Cell Help");
		
		frame.setPreferredSize(new Dimension(VcellHelpViewer.DEFAULT_HELP_DIALOG_WIDTH,VcellHelpViewer.DEFAULT_HELP_DIALOG_HEIGHT));
		frame.pack();
		frame.getContentPane().add(helpViewer);
		frame.setVisible(true);
	}
}
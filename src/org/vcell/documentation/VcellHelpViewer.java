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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.help.HelpSet;
import javax.help.JHelp;
import javax.swing.JFrame;
import javax.swing.UIManager;

import cbit.vcell.client.desktop.DocumentWindowAboutBox;

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
public class VcellHelpViewer extends JFrame 
{
	public static final int DEFAULT_HELP_DIALOG_WIDTH = 900;
	public static final int DEFAULT_HELP_DIALOG_HIGHT = 700;
	private static final int DEFAULT_HELP_DIALOG_LOCX = 300;
	private static final int DEFAULT_HELP_DIALOG_LOCY = 200;
	
	public VcellHelpViewer() {
		super("Virtual Cell Help" + " -- VCell " + DocumentWindowAboutBox.getVERSION_NO() + " (build " + DocumentWindowAboutBox.getBUILD_NO() + ")");
//		setIconImage(new ImageIcon("//vcell.gif").getImage());

		URL resourceURL = VcellHelpViewer.class.getResource("/vcellDoc/HelpSet.hs");

		Container contentPane = getContentPane();
		//URL hsURL;
		HelpSet hs;
		try {
			// get the system class loader
			ClassLoader cl = this.getClass().getClassLoader();
			// create helpset
			hs = new HelpSet(cl, resourceURL);
			JHelp jhelp = new JHelp(hs);
			contentPane.setLayout(new BorderLayout());
			contentPane.add(jhelp);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				VcellHelpViewer.this.setVisible(false);
			}
		});
		
		setLocation(DEFAULT_HELP_DIALOG_LOCX,DEFAULT_HELP_DIALOG_LOCY);
		setPreferredSize(new Dimension(DEFAULT_HELP_DIALOG_WIDTH,DEFAULT_HELP_DIALOG_HIGHT));
		pack();
	}


	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) { }

		final VcellHelpViewer viewer = new VcellHelpViewer();
		viewer.setVisible(true);
		viewer.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				viewer.dispose();
				System.exit(0);
			}
		});
	}

}
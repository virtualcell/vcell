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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.JHelp;
import javax.help.JHelpContentViewer;
import javax.help.JHelpNavigator;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.vcell.util.gui.VCellIcons;

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
public class VcellHelpViewer extends JPanel 
{
	public static final int DEFAULT_HELP_DIALOG_WIDTH = 900;
	public static final int DEFAULT_HELP_DIALOG_HEIGHT = 700;
	
	public static final String VFRAP_DOC_URL = "/doc/HelpSet.hs";
	public static final String VCELL_DOC_URL = "/vcellDoc/HelpSet.hs";
	
	private void makeFontBigger(Component cmpt) {
		Font fn = cmpt.getFont();
		float s = fn.getSize();
		Font bigger = fn.deriveFont(s + 10);
		cmpt.setFont(bigger);
		Font back = cmpt.getFont();
		System.out.println(back);
	}
	
	public VcellHelpViewer(String docUrl) {
		URL resourceURL = VcellHelpViewer.class.getResource(docUrl);

		HelpSet hs;
		try {
			// get the system class loader
			ClassLoader cl = this.getClass().getClassLoader();
			// create helpset
			hs = new HelpSet(cl, resourceURL);
			hs.setKeyData(HelpSet.kitTypeRegistry,"text/html" ,"javax.swing.text.html.HTMLEditorKit");
			JHelp jhelp = new JHelp(hs);
			setLayout(new BorderLayout());
			add(jhelp);
			HelpBroker broker = hs.createHelpBroker();
			Font fn = broker.getFont();
			float s = fn.getSize();
			Font bigger = fn.deriveFont(s + 10);
			broker.setFont(bigger);
			
			JHelpContentViewer cv = jhelp.getContentViewer();
			makeFontBigger(cv);
			
			/*
			JHelpNavigator cn = jhelp.getCurrentNavigator();
			makeFontBigger(cn);
			*/
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		setPreferredSize(new Dimension(DEFAULT_HELP_DIALOG_WIDTH,DEFAULT_HELP_DIALOG_HEIGHT));
	}
	
	public static void main(String[] args){
		VcellHelpViewer helpViewer = new VcellHelpViewer(VcellHelpViewer.VCELL_DOC_URL);
		JFrame frame = new JFrame("Virtual Cell Help");
		
		frame.setPreferredSize(new Dimension(VcellHelpViewer.DEFAULT_HELP_DIALOG_WIDTH,VcellHelpViewer.DEFAULT_HELP_DIALOG_HEIGHT));
		frame.pack();
		frame.add(helpViewer);
		frame.setVisible(true);
	}
}
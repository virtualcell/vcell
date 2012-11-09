/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cbit.vcell.client.desktop.biomodel.DocumentEditor;

@SuppressWarnings("serial")
public class JTabbedPaneEnhanced extends JTabbedPane {

//	private List<String> tabTitles = new ArrayList<String>();
//	private boolean bBoldActiveTab = true;
	
//	private ChangeListener boldChangeListner = new ChangeListener() {			
//		public void stateChanged(ChangeEvent e) {
//			if (bBoldActiveTab) {
//				assert getTabCount() == tabTitles.size();
//				int selectedIndex = getSelectedIndex();
//				for (int i = 0; i < getTabCount(); i ++) {
//					String title = tabTitles.get(i);
//					if (i == selectedIndex) {
//						//title = ""+ title + "</b></html>";
//					}
//					if (!title.contains(DocumentEditor.TAB_TITLE_PROBLEMS)){
//						setTitleAt(i, title);
//					}
//				}
//			}
//		}
//	};
	public JTabbedPaneEnhanced() {
		this(true);
	}
	
	public JTabbedPaneEnhanced(boolean boldActivTab) {
		super();
//		this.bBoldActiveTab = boldActivTab;
//		addChangeListener(boldChangeListner);
	}

	@Override
	public void insertTab(String title, Icon icon, Component component,
			String tip, int index) {
//		tabTitles.add(index, title);
		super.insertTab(title, icon, component, tip, index);
	}

	@Override
	public void removeTabAt(int index) {
//		tabTitles.remove(index);
		super.removeTabAt(index);
	}

	@Override
	public void setComponentAt(int index, Component component) {
		// a bug in BasicTabbedPaneUI
		putClientProperty("__index_to_remove__", index);
		super.setComponentAt(index, component);
	}	
}

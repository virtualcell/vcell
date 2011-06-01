/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.desktop;
import java.util.*;

import org.vcell.util.document.BioModelInfo;

import cbit.vcell.mapping.*;
import cbit.vcell.solver.*;
import cbit.vcell.biomodel.*;
import cbit.vcell.clientdb.*;
import cbit.vcell.server.*;
/**
 * Insert the type's description here.
 * Creation date: (11/28/00 1:14:05 PM)
 * @author: Jim Schaff
 */
public class BioModelMetaDataTreePanelTest extends cbit.vcell.client.test.ClientTester {
/**
 * Insert the method's description here.
 * Creation date: (11/28/00 1:14:34 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {		
		javax.swing.JFrame frame = new javax.swing.JFrame();
		BioModelMetaDataPanel aBioModelMetaDataPanel;
		aBioModelMetaDataPanel = new BioModelMetaDataPanel();
		frame.setContentPane(aBioModelMetaDataPanel);
		frame.setSize(aBioModelMetaDataPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});

		cbit.vcell.client.server.ClientServerManager managerManager = mainInit(args,"BioModelTreeDbPanelTest",frame);
		DocumentManager docManager = managerManager.getDocumentManager();
		aBioModelMetaDataPanel.setDocumentManager(docManager);
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);

		BioModelInfo bioModelInfos[] = docManager.getBioModelInfos();
		System.out.println("there are ("+bioModelInfos.length+") bioModelInfos");
		for (int i=0;i<bioModelInfos.length;i++){
			System.out.println("displaying bioModelInfo = "+bioModelInfos[i].toString());
			aBioModelMetaDataPanel.setBioModelInfo(bioModelInfos[i]);
			break;
		}
		
		
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of BioModelMetaDataPanelTest");
		exception.printStackTrace(System.out);
	}
}
}

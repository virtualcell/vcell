package cbit.vcell.vcml.test;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;

import cbit.util.document.BioModelInfo;
import cbit.vcell.mapping.*;
import cbit.vcell.biomodel.*;
import cbit.vcell.client.database.DocumentManager;
import cbit.vcell.desktop.BioModelMetaDataPanel;
import cbit.vcell.server.*;
import cbit.vcell.simulation.*;
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
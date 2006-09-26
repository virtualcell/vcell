package cbit.vcell.vcml.test;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;
import cbit.vcell.mapping.*;
import cbit.vcell.biomodel.*;
import cbit.vcell.client.database.DocumentManager;
import cbit.vcell.desktop.ImageDbTreePanel;
import cbit.vcell.server.*;
import cbit.vcell.simulation.*;
/**
 * Insert the type's description here.
 * Creation date: (11/28/00 1:14:05 PM)
 * @author: Jim Schaff
 */
public class ImageDbTreePanelTest extends cbit.vcell.client.test.ClientTester {
/**
 * Insert the method's description here.
 * Creation date: (11/28/00 1:14:34 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {		
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ImageDbTreePanel aImageDbTreePanel;
		aImageDbTreePanel = new ImageDbTreePanel();
		frame.setContentPane(aImageDbTreePanel);
		frame.setSize(aImageDbTreePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});

		cbit.vcell.client.server.ClientServerManager managerManager = mainInit(args,"ImageDbTreePanelTest",frame);
		DocumentManager docManager = managerManager.getDocumentManager();
		aImageDbTreePanel.setDocumentManager(docManager);
		cbit.vcell.desktop.controls.ImageWorkspace imageWorkspace = new cbit.vcell.desktop.controls.ImageWorkspace();
		
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);	
		
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of ImageDbTreePanelTest");
		exception.printStackTrace(System.out);
	}
}
}
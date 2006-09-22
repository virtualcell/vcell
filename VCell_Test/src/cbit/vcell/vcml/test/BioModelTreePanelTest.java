package cbit.vcell.vcml.test;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;
import cbit.vcell.mapping.*;
import cbit.vcell.biomodel.*;
import cbit.vcell.desktop.BioModelTreePanel;
import cbit.vcell.server.*;
import cbit.vcell.simulation.*;
/**
 * Insert the type's description here.
 * Creation date: (11/28/00 1:14:05 PM)
 * @author: Jim Schaff
 */
public class BioModelTreePanelTest {
/**
 * Insert the method's description here.
 * Creation date: (11/28/00 1:14:34 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {		
		javax.swing.JFrame frame = new javax.swing.JFrame();
		BioModelTreePanel aBioModelTreePanel;
		aBioModelTreePanel = new BioModelTreePanel();
		frame.setContentPane(aBioModelTreePanel);
		frame.setSize(aBioModelTreePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});


		BioModel bioModel = BioModelTest.getExample();
		//bioModel = null;
		aBioModelTreePanel.setTheBioModel(bioModel);

		
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
		
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of BioModelTreePanelTest");
		exception.printStackTrace(System.out);
	}
}
}

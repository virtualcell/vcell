package cbit.vcell.model.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.Color;

import javax.swing.JInternalFrame;

import cbit.gui.graph.GraphPane;
import cbit.vcell.model.*;
/**
 * This type was created in VisualAge.
 */
public class MembraneDialogTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		javax.swing.JDesktopPane desktop = new org.vcell.util.gui.JDesktopPaneEnhanced();
		frame.setSize(500,500);
		frame.setContentPane(desktop);

		
		cbit.vcell.model.Model model = cbit.vcell.model.ModelTest.getExample();
		cbit.vcell.model.Membrane membrane = (Membrane)model.getStructure("ER_Membrane");

		MembraneDialog aMembraneDialog = new MembraneDialog();
		aMembraneDialog.setVisible(true);

		desktop.add(aMembraneDialog, aMembraneDialog.getName());
		frame.getContentPane().setLayout(new java.awt.BorderLayout());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
		aMembraneDialog.init(membrane);

	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of cbit.vcell.model.MembraneDialog");
		exception.printStackTrace(System.out);
	}
}
}

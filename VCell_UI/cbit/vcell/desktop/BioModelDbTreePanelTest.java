package cbit.vcell.desktop;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;
import cbit.vcell.mapping.*;
import cbit.vcell.biomodel.*;
import cbit.vcell.client.database.DocumentManager;
import cbit.vcell.server.*;
import cbit.vcell.simulation.*;
/**
 * Insert the type's description here.
 * Creation date: (11/28/00 1:14:05 PM)
 * @author: Jim Schaff
 */
public class BioModelDbTreePanelTest extends cbit.vcell.client.server.ClientTester {
/**
 * Insert the method's description here.
 * Creation date: (11/28/00 1:14:34 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {		
		javax.swing.JFrame frame = new javax.swing.JFrame();
		BioModelDbTreePanel aBioModelDbTreePanel;
		aBioModelDbTreePanel = new BioModelDbTreePanel();
		frame.setContentPane(aBioModelDbTreePanel);
		frame.setSize(aBioModelDbTreePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});

		cbit.vcell.client.server.ClientServerManager managerManager = mainInit(args,"BioModelTreeDbPanelTest",frame);
		DocumentManager docManager = managerManager.getDocumentManager();
		
/*
		BioModel bioModel = BioModelTest.getExample();
		bioModel = docManager.save(bioModel);
		SimulationContext simContext = bioModel.getSimulationContexts()[0];
		Simulation sim = new Simulation(simContext.getMathDescription());
		sim.setName("sim1_"+Integer.toHexString((new Random()).nextInt()));
		bioModel.setSimulations(new Simulation[] { sim });
		bioModel = docManager.save(bioModel);
*/


		
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);

		aBioModelDbTreePanel.setDocumentManager(docManager);
		
		
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of BioModelDbTreePanelTest");
		exception.printStackTrace(System.out);
	}
}
}
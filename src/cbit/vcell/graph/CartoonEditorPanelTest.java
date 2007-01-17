package cbit.vcell.graph;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.clientdb.DocumentManager;
/**
 * This type was created in VisualAge.
 */
public class CartoonEditorPanelTest extends cbit.vcell.client.test.ClientTester {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {		
		java.awt.Frame frame = new java.awt.Frame();
		CartoonEditorPanelFixed aCartoonEditorPanel;
		aCartoonEditorPanel = new CartoonEditorPanelFixed();
		frame.add("Center", aCartoonEditorPanel);
		frame.setSize(aCartoonEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});

		cbit.vcell.client.server.ClientServerManager managerManager = mainInit(args,"BioModelTreePanelTest", frame);
		DocumentManager docManager = managerManager.getDocumentManager();

		//
		// get BioModel from database
		//
//		Workspace workspace = new Workspace();
//		workspace.setDocumentManager(docManager);
//		BioModelInfo bioModelInfos[] = docManager.getBioModelInfos();
//		workspace.load(bioModelInfos[0]);

		//
		// create BioModel on the fly
		//
		frame.setSize(150,150);
		frame.setVisible(true);
		
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of BioModelTreePanelTest");
		exception.printStackTrace(System.out);
	}
}
}
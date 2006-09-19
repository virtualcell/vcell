package cbit.vcell.graph;
import cbit.vcell.client.database.DocumentManager;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.model.*;
/**
 * This type was created in VisualAge.
 */
public class ReactionCartoonEditorPanelTest extends cbit.vcell.client.server.ClientTester {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
      	java.awt.Frame frame = new java.awt.Frame();
		Model model = ModelTest.getExample2();
		Structure structure = (Structure)model.getStructure("Cytosol");
		ReactionCartoonEditorPanel aReactionCartoonEditorPanel = new ReactionCartoonEditorPanel();
		frame.add("Center", aReactionCartoonEditorPanel);
		frame.setSize(aReactionCartoonEditorPanel.getSize());
		frame.setVisible(true);
		new cbit.gui.WindowCloser(frame,true);

		cbit.vcell.client.server.ClientServerManager managerManager = mainInit(args,"ReactionCartoonEditorPanelTest",frame);
		cbit.vcell.client.database.ClientDocumentManager docManager = (cbit.vcell.client.database.ClientDocumentManager)managerManager.getDocumentManager();

		aReactionCartoonEditorPanel.setModel(model);
		aReactionCartoonEditorPanel.setStructure(structure);
		aReactionCartoonEditorPanel.setDocumentManager(docManager);

		java.io.PrintWriter pw = new java.io.PrintWriter(System.out);
		Diagram diagram = new Diagram(structure,structure.getName()+"_diagram");
		aReactionCartoonEditorPanel.repaint();
		diagram.write(pw);
		pw.flush();
		
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Panel");
		exception.printStackTrace(System.out);
	}
}
}
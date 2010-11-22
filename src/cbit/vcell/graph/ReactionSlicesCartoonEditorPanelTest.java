package cbit.vcell.graph;
import org.vcell.util.gui.WindowCloser;

import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.clientdb.ClientDocumentManager;
import cbit.vcell.model.Diagram;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelTest;
import cbit.vcell.model.Structure;
/**
 * This type was created in VisualAge.
 */
public class ReactionSlicesCartoonEditorPanelTest extends cbit.vcell.client.test.ClientTester {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
      	java.awt.Frame frame = new java.awt.Frame();
		Model model = ModelTest.getExample2();
		Structure structure = (Structure)model.getStructure("Cytosol");
		ReactionSlicesCartoonEditorPanel aReactionCartoonEditorPanel = new ReactionSlicesCartoonEditorPanel();
		frame.add("Center", aReactionCartoonEditorPanel);
		frame.setSize(aReactionCartoonEditorPanel.getSize());
		frame.setVisible(true);
		new WindowCloser(frame,true);

		ClientServerManager managerManager = mainInit(args,"ReactionCartoonEditorPanelTest",frame);
		ClientDocumentManager docManager = (ClientDocumentManager)managerManager.getDocumentManager();

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
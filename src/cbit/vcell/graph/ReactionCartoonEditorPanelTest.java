package cbit.vcell.graph;
import java.awt.Frame;
import java.io.PrintWriter;

import org.vcell.util.gui.WindowCloser;

import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.clientdb.ClientDocumentManager;
import cbit.vcell.graph.structures.AllStructureSuite;
import cbit.vcell.graph.structures.MembraneStructureSuite;
import cbit.vcell.graph.structures.SingleStructureSuite;
import cbit.vcell.graph.structures.StructureSuite;
import cbit.vcell.model.Diagram;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelTest;
import cbit.vcell.model.Structure;
/**
 * This type was created in VisualAge.
 */
public class ReactionCartoonEditorPanelTest extends cbit.vcell.client.test.ClientTester {
	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	
	public static class ModelOwner implements Model.Owner {

		protected Model model = createModel();

		public Model createModel() {
			try {
				return ModelTest.getExample2();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public Model getModel() {
			return model;
		}
		
	}
	
	
	public static void main(String[] args) {
		try {
			Frame frame = new Frame();
			ModelOwner modelOwner = new ModelOwner();
			Structure structure = modelOwner.getModel().getStructure("Cytosol");
			ReactionCartoonEditorPanel aReactionCartoonEditorPanel = new ReactionCartoonEditorPanel();
			frame.add("Center", aReactionCartoonEditorPanel);
			frame.setSize(aReactionCartoonEditorPanel.getSize());
			frame.setVisible(true);
			new WindowCloser(frame,true);

			ClientServerManager managerManager = mainInit(args,"ReactionCartoonEditorPanelTest",frame);
			ClientDocumentManager docManager = (ClientDocumentManager)managerManager.getDocumentManager();

			aReactionCartoonEditorPanel.setModel(modelOwner.getModel());
			boolean traditionalStyle = true;
			if(traditionalStyle) {
				StructureSuite layout;
				if(structure instanceof Membrane) {
					layout = new MembraneStructureSuite(((Membrane) structure));
				} else {
					layout = new SingleStructureSuite(structure);
				}
				StructureSuite structureSuite = layout;
				aReactionCartoonEditorPanel.setStructureSuite(structureSuite);
			} else {
				aReactionCartoonEditorPanel.setStructureSuite(
						new AllStructureSuite(modelOwner));				
			}
			aReactionCartoonEditorPanel.setDocumentManager(docManager);

			PrintWriter pw = new PrintWriter(System.out);
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
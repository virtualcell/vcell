package cbit.vcell.mapping.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContextTest;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 11:15:28 PM)
 * @author: 
 */
public class ReactionSpecsPanelTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ReactionSpecsPanel aReactionSpecsPanel;
		aReactionSpecsPanel = new ReactionSpecsPanel();
		frame.setContentPane(aReactionSpecsPanel);
		frame.setSize(aReactionSpecsPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);

		SimulationContext simContext = SimulationContextTest.getExample(2);
		aReactionSpecsPanel.setSimulationContext(simContext);

System.out.println("waiting for 8 seconds");
		try {
			Thread.sleep(8000);
		}catch (InterruptedException e){
		}

		simContext.getModel().addSpecies(new Species("abc123","abc123"));
		Species species = simContext.getModel().getSpecies("abc123");
		Structure structure = (Structure)simContext.getModel().getStructures()[0];
		simContext.getModel().addSpeciesContext(species,structure);
		SpeciesContext sc = simContext.getModel().getSpeciesContext("abc_struct");
		SimpleReaction simpleReaction = new SimpleReaction(structure,"newReact");
System.out.println("setting new reaction");
		simContext.getModel().addReactionStep(simpleReaction);
		
System.out.println("waiting for another 8 seconds");
		try {
			Thread.sleep(8000);
		}catch (InterruptedException e){
		}
System.out.println("renaming last reaction");
		simpleReaction.setName("LastReaction");

	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}

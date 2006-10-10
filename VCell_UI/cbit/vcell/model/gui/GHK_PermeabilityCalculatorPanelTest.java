package cbit.vcell.model.gui;

import org.vcell.expression.ExpressionFactory;

import cbit.vcell.model.*;
/**
 * Insert the type's description here.
 * Creation date: (4/4/2002 5:52:36 PM)
 * @author: Jim Schaff
 */
public class GHK_PermeabilityCalculatorPanelTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		GHK_PermeabilityCalculatorPanel aGHK_PermeabilityCalculatorPanel;
		aGHK_PermeabilityCalculatorPanel = new GHK_PermeabilityCalculatorPanel();
		frame.setContentPane(aGHK_PermeabilityCalculatorPanel);
		frame.setSize(aGHK_PermeabilityCalculatorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		cbit.vcell.model.Model model = new cbit.vcell.model.Model("test");
		model.addFeature("extracellular",null,null);
		Feature extracellular = (Feature)model.getStructure("extracellular");
		model.addFeature("cytosol",extracellular,"pm");
		cbit.vcell.model.Membrane pm = (cbit.vcell.model.Membrane)model.getStructure("pm");
		model.addSpecies(new Species("calcium","ca"));
		Species calcium = model.getSpecies("calcium");
		model.addSpeciesContext(calcium,pm.getInsideFeature());
		model.addSpeciesContext(calcium,pm.getOutsideFeature());
		FluxReaction fr = new FluxReaction(pm,calcium,model,"L-Type calcium channel");
		fr.getChargeCarrierValence().setExpression(ExpressionFactory.createExpression(2));
		GHKKinetics ghk = new GHKKinetics(fr);
		//ghk.setPermeability(new Expression("hello*goodbye"));
		//ghk.setPermeability(new Expression(10.0));
		aGHK_PermeabilityCalculatorPanel.setghkKinetics1(ghk);
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}

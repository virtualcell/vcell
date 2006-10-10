package cbit.vcell.model.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.expression.ExpressionFactory;

import cbit.vcell.model.*;
/**
 * This type was created in VisualAge.
 */
public class KineticsTypeTemplatePanelTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		KineticsTypeTemplatePanel aKineticsTypeTemplatePanel;
		aKineticsTypeTemplatePanel = new KineticsTypeTemplatePanel();
		frame.add("Center", aKineticsTypeTemplatePanel);
		frame.setSize(aKineticsTypeTemplatePanel.getSize());
		frame.setVisible(true);

		cbit.vcell.model.Model model = new cbit.vcell.model.Model("test");
		model.addFeature("extracellular",null,null);
		Feature extracellular = (Feature)model.getStructure("extracellular");
		model.addFeature("cytosol",extracellular,"pm");
		Feature cytosol = (Feature)model.getStructure("cytosol");
		cbit.vcell.model.Membrane pm = (cbit.vcell.model.Membrane)model.getStructure("pm");
		model.addSpecies(new Species("calcium","ca"));
		model.addSpecies(new Species("calcium2","ca2"));
		Species calcium = model.getSpecies("calcium");
		Species calcium2 = model.getSpecies("calcium2");
		model.addSpeciesContext(calcium,cytosol);
		model.addSpeciesContext(calcium2,cytosol);
		model.addSpeciesContext(calcium,extracellular);
	
		SimpleReaction sr = new SimpleReaction(pm.getInsideFeature(),"ReactionStep0");
		sr.addReactant(model.getSpeciesContext("ca_cyt"),1);
		sr.addProduct(model.getSpeciesContext("ca2_cyt"),1);
	
		FluxReaction fr = new FluxReaction(pm,calcium,model,"L-Type calcium channel");
		fr.getChargeCarrierValence().setExpression(ExpressionFactory.createExpression(2));

		//sr.setKinetics(new MassActionKinetics(sr));
		//MassActionKinetics massAct = (MassActionKinetics)sr.getKinetics();

		fr.setKinetics(new GHKKinetics(fr));
		GHKKinetics ghkKinetics = (GHKKinetics)fr.getKinetics();


		aKineticsTypeTemplatePanel.setKinetics(ghkKinetics);
		System.out.println("Rate = "+ghkKinetics.getRateParameter().getExpression());
		System.out.println("Current = "+ghkKinetics.getCurrentParameter().getExpression());
		
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Panel");
		exception.printStackTrace(System.out);
	}
}
}

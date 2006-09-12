package cbit.vcell.mapping.gui;

import cbit.vcell.parser.Expression;
import cbit.vcell.geometry.Geometry;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.model.*;
import cbit.vcell.mapping.*;
/**
 * This type was created in VisualAge.
 */
public class SpeciesContextSpecPanelTest {
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
		SpeciesContextSpecPanel aSpeciesContextSpecPanel;
		aSpeciesContextSpecPanel = new SpeciesContextSpecPanel();
		frame.add("Center", aSpeciesContextSpecPanel);
		frame.setSize(aSpeciesContextSpecPanel.getSize());
		frame.setVisible(true);
		Model model = new Model("model1");
		model.addSpecies(new Species("species1","species1"));
		model.addFeature("feature1",null,null);
		model.addSpeciesContext(model.getSpecies("species1"),model.getStructure("feature1"));
		SpeciesContext sc = model.getSpeciesContext(model.getSpecies("species1"),model.getStructure("feature1"));
		Geometry geometry = new Geometry("new",2);
		geometry.getGeometrySpec().addSubVolume(new cbit.vcell.geometry.AnalyticSubVolume("subVolume0",new Expression(1.0)));
		SimulationContext simContext = new SimulationContext(model,geometry);
		simContext.getGeometryContext().assignFeature((Feature)model.getStructure("feature1"),geometry.getGeometrySpec().getSubVolume("subVolume0"));
		aSpeciesContextSpecPanel.setSimulationContext(simContext);
		aSpeciesContextSpecPanel.setSpeciesContextSpec(simContext.getReactionContext().getSpeciesContextSpec(sc));
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Panel");
		exception.printStackTrace(System.out);
	}
}
}

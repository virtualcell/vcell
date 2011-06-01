/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model.gui;

import cbit.vcell.model.*;
/**
 * This type was created in VisualAge.
 */
public class SimpleReactionPanelTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		Model model = ModelTest.getExample();
		FluxReaction rs = (FluxReaction)model.getReactionStep("IP3_FLUX");
		//Structure structure = model.getStructure("Cytosol");
		Structure structure = model.getStructure("PlasmaMembrane");
		//SimpleReaction rs = (SimpleReaction)model.getReactionStep("IP3_DEGRADATION");
		SimpleReaction newReaction = new SimpleReaction(structure,"newReaction");
		ReactionParticipant reactionParticipants[] = rs.getReactionParticipants();
		//for (int i=0;i<reactionParticipants.length;i++){
			newReaction.addReactionParticipant(new Reactant(null,newReaction,reactionParticipants[0].getSpeciesContext(),1));
			newReaction.addReactionParticipant(new Product(null,newReaction,reactionParticipants[1].getSpeciesContext(),1));
		//}
		model.addReactionStep(newReaction);
		
		newReaction.setKinetics(new MassActionKinetics(newReaction));
		MassActionKinetics massAct = (MassActionKinetics)newReaction.getKinetics();
		newReaction.setKinetics(massAct);
		
		if (newReaction.getStructure() instanceof Membrane){
			newReaction.getChargeCarrierValence().setExpression(new cbit.vcell.parser.Expression(4));
		}

		javax.swing.JFrame frame = new javax.swing.JFrame();
		SimpleReactionPanel aSimpleReactionPanel = new SimpleReactionPanel();
		frame.setContentPane(aSimpleReactionPanel);
		frame.setSize(aSimpleReactionPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});

		
		aSimpleReactionPanel.setSimpleReaction(newReaction);
		
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
		
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of cbit.vcell.model.SimpleReactionDialog");
		exception.printStackTrace(System.out);
	}
}
}

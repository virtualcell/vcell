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
public class SimpleReactionPanelDialogTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		Model model = ModelTest.getExample();
		SimpleReaction simpleReaction = (SimpleReaction)model.getReactionStep("IP3_BINDING");
		//SimpleReaction simpleReaction = (SimpleReaction)model.getReactionStep("IP3_DEGRADATION");
		SimpleReaction newReaction = new SimpleReaction(simpleReaction.getStructure(),"newReaction");
		ReactionParticipant reactionParticipants[] = simpleReaction.getReactionParticipants();
		for (int i=0;i<reactionParticipants.length;i++){
			newReaction.addReactionParticipant(reactionParticipants[i]);
		}
		model.addReactionStep(newReaction);

		
		newReaction.setKinetics(new MassActionKinetics(newReaction));
		MassActionKinetics massAct = (MassActionKinetics)newReaction.getKinetics();


	//	newReaction.setKinetics(massAct);
		
		if (newReaction.getStructure() instanceof Membrane){
			newReaction.getChargeCarrierValence().setExpression(new cbit.vcell.parser.Expression(4));
		}

		SimpleReactionPanelDialog aSimpleReactionPanelDialog;
		aSimpleReactionPanelDialog = new SimpleReactionPanelDialog(new javax.swing.JFrame(),true);
		aSimpleReactionPanelDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = aSimpleReactionPanelDialog.getInsets();
		aSimpleReactionPanelDialog.setSize(aSimpleReactionPanelDialog.getWidth() + insets.left + insets.right, aSimpleReactionPanelDialog.getHeight() + insets.top + insets.bottom);
		aSimpleReactionPanelDialog.setTitle("SimpleReaction Dialog, reactionName = " + simpleReaction.getName());
		aSimpleReactionPanelDialog.setSimpleReaction(newReaction);
		
		aSimpleReactionPanelDialog.show();

	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of cbit.vcell.model.SimpleReactionDialog");
		exception.printStackTrace(System.out);
	}
}
}

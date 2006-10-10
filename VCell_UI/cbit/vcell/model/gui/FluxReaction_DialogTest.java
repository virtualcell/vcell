package cbit.vcell.model.gui;


import org.vcell.expression.ExpressionFactory;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.model.*;
/**
 * This type was created in VisualAge.
 */
public class FluxReaction_DialogTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		Model model = ModelTest.getExample();
		ReactionStep reactionSteps[] = model.getReactionSteps();
		FluxReaction tempFluxReaction = null;
		for (int i=0;i<reactionSteps.length;i++){
			if (reactionSteps[i] instanceof FluxReaction){
				tempFluxReaction = (FluxReaction)reactionSteps[i];
			}
		}
		final FluxReaction fluxReaction = tempFluxReaction;

		//System.out.println("Flux carrier : "+fluxReaction.getFluxCarrier().toString());

		fluxReaction.getChargeCarrierValence().setExpression(ExpressionFactory.createExpression(1));
		fluxReaction.setKinetics(new GHKKinetics(fluxReaction));
		GHKKinetics ghk = (GHKKinetics)fluxReaction.getKinetics();
		fluxReaction.setKinetics(ghk);
		
		FluxReaction_Dialog aFluxReaction_Dialog;
		aFluxReaction_Dialog = new FluxReaction_Dialog(new javax.swing.JFrame(),false);
		aFluxReaction_Dialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = aFluxReaction_Dialog.getInsets();
		aFluxReaction_Dialog.setSize(aFluxReaction_Dialog.getWidth() + insets.left + insets.right, aFluxReaction_Dialog.getHeight() + insets.top + insets.bottom);
		aFluxReaction_Dialog.setTitle("FluxReaction Dialog, modelName = "+model.getName());
		aFluxReaction_Dialog.init(fluxReaction, model);
		aFluxReaction_Dialog.show();

		/*Runnable job = new Runnable() {
			//public void run() {
				//try {
					//Thread.currentThread().sleep(20000);
					//SwingUtilities.invokeLater(new Runnable() {
						//public void run() {
							//try {
								//((Membrane)fluxReaction.getStructure()).getMembraneVoltage().setName("NewV");
								//System.out.println("V = NewV");
							//}catch (Throwable e){
								//e.printStackTrace(System.out);
							//}
						//}
					//});
				//}catch (Throwable e){
					//e.printStackTrace(System.out);
				//}
			//}
		//};
		//Thread newThread = new Thread(job);
		//newThread.start(); 

		Thread.sleep(20000);
		((Membrane)fluxReaction.getStructure()).getMembraneVoltage().setName("NewV");
		System.out.println("V = NewV"); */
		
		
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of cbit.vcell.model.FluxReactionDialog");
		exception.printStackTrace(System.out);
	}
}
}

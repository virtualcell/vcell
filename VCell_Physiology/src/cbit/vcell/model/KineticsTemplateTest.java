package cbit.vcell.model;

import org.vcell.expression.ExpressionException;

import edu.uchc.vcell.expression.internal.*;
/**
 * Insert the type's description here.
 * Creation date: (6/18/2002 1:44:36 PM)
 * @author: Anuradha Lakshminarayana
 */
public class KineticsTemplateTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
    try {

	    cbit.vcell.model.Model model = cbit.vcell.model.ModelTest.getExample();
	    
       /* cbit.vcell.model.Model model = new cbit.vcell.model.Model("test");
        model.addFeature("extracellular", null, null);
        Feature extracellular = (Feature) model.getStructure("extracellular");
        model.addFeature("cytosol", extracellular, "pm");
        Feature cytosol = (Feature) model.getStructure("cytosol");
        cbit.vcell.model.Membrane pm =
            (cbit.vcell.model.Membrane) model.getStructure("pm");
        model.addSpecies("calcium", "ca");
        model.addSpecies("calcium2", "ca2");
        Species calcium = model.getSpecies("calcium");
        Species calcium2 = model.getSpecies("calcium2");
        model.addSpeciesContext("ca_cyt", calcium, cytosol);
        model.addSpeciesContext("ca2_cyt", calcium2, cytosol);
        model.addSpeciesContext("ca_ec", calcium, extracellular);

        SimpleReaction sr = new SimpleReaction(pm.getInsideFeature(), "ReactionStep0");
        sr.addReactant(model.getSpeciesContext("ca_cyt"), 1);
        sr.addProduct(model.getSpeciesContext("ca2_cyt"), 1);

        FluxReaction fr =
            new FluxReaction(pm, calcium, model, "L-Type calcium channel");
        fr.setChargeCarrierValence(2);

        Kinetics kt1 = KineticsDescription.HMM_irreversible.createKinetics(sr);
        //		sr.addReactant(model.getSpeciesContext("ca_cyt"),1);
        //		sr.addProduct(model.getSpeciesContext("ca2_cyt"),1);
        System.out.println("Rate = " + kt1.getRateExpression());
        System.out.println("Current = " + kt1.getCurrentExpression());
        System.out.println(kt1.getVCML());

        Kinetics kt2 = KineticsDescription.GeneralCurrent.createKinetics(fr);
        fr.setKinetics(kt2);
        kt2.setParameterValue("inwardCurrent_L_Type_calcium_c", "10.0");
        //		sr.addReactant(model.getSpeciesContext("ca_cyt"),1);
        //		sr.addProduct(model.getSpeciesContext("ca2_cyt"),1);
        System.out.println("Rate = " + kt2.getRateExpression());
        System.out.println("Current = " + kt2.getCurrentExpression());
        System.out.println(kt2.getVCML());
        cbit.vcell.math.CommentStringTokenizer tokens =
            new cbit.vcell.math.CommentStringTokenizer(kt2.getVCML());
        tokens.nextToken();
        String kineticType = tokens.nextToken();
        Kinetics newkt2 = KineticsDescription.fromVCMLKineticsName(kineticType).createKinetics(fr);
        newkt2.fromTokens(tokens);
        System.out.println(newkt2.getVCML());
        */


        for (int i = 0; i < model.getReactionSteps().length; i++){
	        testReactionStep(model.getReactionSteps(i));
        }
    } catch (Throwable e) {
        e.printStackTrace(System.out);
    }
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 3:28:51 PM)
 * @param reactionStep cbit.vcell.model.ReactionStep
 */
private static void testReactionStep(ReactionStep reactionStep) throws ExpressionException, java.beans.PropertyVetoException {
	Kinetics k = reactionStep.getKinetics();
	
	System.out.println("\n\n\n      TEST FOR ReactionStep "+reactionStep.getName()+" ************* ");
	System.out.println("Rate = " + k.getRateParameter());
	System.out.println("Current = " + k.getCurrentParameter());
	System.out.println(reactionStep.getVCML());
	org.vcell.util.CommentStringTokenizer tokens = new org.vcell.util.CommentStringTokenizer(k.getVCML());
	tokens.nextToken();
	String kineticType = tokens.nextToken();
	Kinetics newK = KineticsDescription.fromVCMLKineticsName(kineticType).createKinetics(reactionStep);
	newK.fromTokens(tokens);
	System.out.println(newK.getVCML());
	if (k.compareEqual(newK)==false){
		throw new RuntimeException("kinetics didn't parse properly");
	}
}
}

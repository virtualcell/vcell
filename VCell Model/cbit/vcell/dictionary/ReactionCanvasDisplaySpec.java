package cbit.vcell.dictionary;

import java.util.Vector;

import cbit.vcell.model.Catalyst;
import cbit.vcell.model.Flux;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.Kinetics.KineticsParameter;


/**
 * Insert the type's description here.
 * Creation date: (4/14/2005 11:36:38 AM)
 * @author: Jim Schaff
 */
public class ReactionCanvasDisplaySpec {
	public static final int ARROW_RIGHT = 0;
	public static final int ARROW_BOTH = 1;
	
	private String leftText = null;
	private String rightText = null;
	private String topText = null;
	private String bottomText = null;
	private int arrowType = -1;

/**
 * ReactionDisplayText constructor comment.
 */
public ReactionCanvasDisplaySpec(String argLeftText, String argRightText, String argTopText, String argBottomText, int argArrowType) {
	super();
	this.leftText = argLeftText;
	this.rightText = argRightText;
	this.topText = argTopText;
	this.bottomText = argBottomText;
	this.arrowType = argArrowType;
}

public static ReactionCanvasDisplaySpec fromReactionStep(ReactionStep reactionStep){
	
	String forwardRateString = null;
	String reverseRateString = null;
	String reactantString = null;
	String productString = null;
	String fluxString = null;
	boolean bReversible = false;

	//
	// get list of reactants, products, catalysts
	//
	Vector reactantList = new Vector();
	Vector productList = new Vector();
	Vector catalystList = new Vector();
	ReactionParticipant rp_Array[] = reactionStep.getReactionParticipants();
	for (int i = 0; i < rp_Array.length; i++) {
		if (rp_Array[i] instanceof Reactant){
			reactantList.addElement(rp_Array[i]);
		}else if (rp_Array[i] instanceof Product){
			productList.addElement(rp_Array[i]);
		}else if (rp_Array[i] instanceof Catalyst){
			catalystList.addElement(rp_Array[i]);
		}
	}
	
	//
	// default for ForwardRateString is to display catalysts, if present
	//
	//           E1
	// (e.g. a -----> b    )
	//
	//
	forwardRateString = "";
	if (catalystList.size()>0){
		for (int i = 0; i < catalystList.size(); i++){
			if (i>0){
				forwardRateString += ",";
			}
			forwardRateString += ((Catalyst)catalystList.elementAt(i)).getSpeciesContext().getName();						
		}
	}
	
	if (reactionStep instanceof SimpleReaction){
		SimpleReaction simpleReaction = (SimpleReaction)reactionStep;
		//
		// get rate expression strings
		//
		if (simpleReaction.getKinetics() instanceof MassActionKinetics) {
			Kinetics.KineticsParameter forwardRateParam = ((MassActionKinetics)simpleReaction.getKinetics()).getForwardRateParameter();
			if (forwardRateParam!=null){
				forwardRateString = forwardRateParam.getName();
			}
			Kinetics.KineticsParameter reverseRateParam = ((MassActionKinetics)simpleReaction.getKinetics()).getReverseRateParameter();
			if (reverseRateParam!=null){
				reverseRateString = reverseRateParam.getName();
				bReversible = true;
			}
		} else {
			reverseRateString = null;
		}
		//
		// form reactant string
		//
		reactantString = "";
		for (int i=0;i<reactantList.size();i++){
			if (i>0){
				reactantString += " + ";
			}
			if (((Reactant)reactantList.elementAt(i)).getStoichiometry() != 1.0){
				reactantString += ((Reactant)reactantList.elementAt(i)).getStoichiometry() + " ";
			}	
			reactantString += ((Reactant)reactantList.elementAt(i)).getSpeciesContext().getName();
		}		
		//
		// form product string
		//
		productString = "";
		for (int i=0;i<productList.size();i++){
			if (i>0){
				productString += " + ";
			}	
			if (((Product)productList.elementAt(i)).getStoichiometry() != 1.0){
				productString += ((Product)productList.elementAt(i)).getStoichiometry() + " ";
			}	
			productString += ((Product)productList.elementAt(i)).getSpeciesContext().getName();
		}		
	}else if (reactionStep instanceof FluxReaction){
		bReversible = false;
		FluxReaction fluxReaction = (FluxReaction)reactionStep;
		Species fluxCarrier = fluxReaction.getFluxCarrier();
		reverseRateString = null;			
		if (fluxCarrier==null){
			fluxString = "unspecified flux carrier";
			reactantString = fluxString;
			productString = fluxString;
		}else{
			Membrane membrane = (Membrane)fluxReaction.getStructure();
			Flux outFlux = fluxReaction.getFlux(membrane.getOutsideFeature());
			Flux inFlux = fluxReaction.getFlux(membrane.getInsideFeature());
			String outSCName = (outFlux!=null)?outFlux.getSpeciesContext().getName():"unknown";
			String inSCName = (inFlux!=null)?inFlux.getSpeciesContext().getName():"unknown";
			fluxString = outSCName+"   > > > > > > > > > >   "+inSCName;
			reactantString = outSCName;
			productString = inSCName;
		}
	}
	int arrowType = (bReversible)?(ReactionCanvasDisplaySpec.ARROW_BOTH):(ReactionCanvasDisplaySpec.ARROW_RIGHT);

	return new ReactionCanvasDisplaySpec(reactantString,productString,forwardRateString,reverseRateString,arrowType);
	
}

/**
 * Insert the method's description here.
 * Creation date: (4/14/2005 11:44:47 AM)
 * @return int
 */
public int getArrowType() {
	return arrowType;
}


/**
 * Insert the method's description here.
 * Creation date: (4/14/2005 11:44:47 AM)
 * @return java.lang.String
 */
public java.lang.String getBottomText() {
	return bottomText;
}


/**
 * Insert the method's description here.
 * Creation date: (4/14/2005 11:44:47 AM)
 * @return java.lang.String
 */
public java.lang.String getLeftText() {
	return leftText;
}


/**
 * Insert the method's description here.
 * Creation date: (4/14/2005 11:44:47 AM)
 * @return java.lang.String
 */
public java.lang.String getRightText() {
	return rightText;
}


/**
 * Insert the method's description here.
 * Creation date: (4/14/2005 11:44:47 AM)
 * @return java.lang.String
 */
public java.lang.String getTopText() {
	return topText;
}
}
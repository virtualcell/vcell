package cbit.vcell.model;

import java.beans.PropertyVetoException;

import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;
import org.vcell.units.VCUnitDefinition;
import org.vcell.util.*;
/**
 * Insert the type's description here.
 * Creation date: (2/18/2002 5:07:08 PM)
 * @author: Anuradha Lakshminarayana
 */
public class GHKKinetics extends Kinetics {
/**
 * NernstKinetics constructor comment.
 * @param reactionStep cbit.vcell.model.ReactionStep
 * @exception java.lang.Exception The exception description.
 */
public GHKKinetics(ReactionStep reactionStep) throws ExpressionException {
	super(KineticsDescription.GHK.getName(),reactionStep);
	try {
		KineticsParameter currentParm = new KineticsParameter(getDefaultParameterName(ROLE_Current),ExpressionFactory.createExpression(0.0),ROLE_Current,null);
		KineticsParameter rateParm = new KineticsParameter(getDefaultParameterName(ROLE_Rate),ExpressionFactory.createExpression(0.0),ROLE_Rate,null);
		KineticsParameter permeabilityParm = new KineticsParameter(getDefaultParameterName(ROLE_Permeability),ExpressionFactory.createExpression(0.0),ROLE_Permeability,null);

		setKineticsParameters(new KineticsParameter[] { currentParm, rateParm, permeabilityParm });
		updateGeneratedExpressions();
		refreshUnits();
	}catch (PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException("unexpected exception: "+e.getMessage());
	}
}
/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj == this){
		return true;
	}
	if (!(obj instanceof GHKKinetics)){
		return false;
	}
	
	GHKKinetics ghk = (GHKKinetics)obj;

	if (!compareEqual0(ghk)){
		return false;
	}
	
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 3:11:16 PM)
 * @return cbit.util.Issue[]
 */
public void gatherIssues(java.util.Vector issueList) {
	
	super.gatherIssues(issueList);

	//
	// check for correct number of reactants and products
	//
	int reactantCount=0;
	int productCount=0;
	ReactionParticipant reactionParticipants[] = getReactionStep().getReactionParticipants();
	for (int i = 0; i < reactionParticipants.length; i++){
		if (reactionParticipants[i] instanceof Flux && 
			reactionParticipants[i].getStructure().compareEqual(((Membrane)getReactionStep().getStructure()).getInsideFeature())){
			if (reactionParticipants[i].getStructure()!=((Membrane)getReactionStep().getStructure()).getInsideFeature()){
				issueList.add(new Issue(this,"ASSERTION","multiple instantiations of feature '"+reactionParticipants[i].getStructure(),Issue.SEVERITY_WARNING));
			}
			reactantCount++;
		}
		if (reactionParticipants[i] instanceof Flux && 
			reactionParticipants[i].getStructure().compareEqual(((Membrane)getReactionStep().getStructure()).getOutsideFeature())){
			if (reactionParticipants[i].getStructure()!=((Membrane)getReactionStep().getStructure()).getOutsideFeature()){
				issueList.add(new Issue(this,"ASSERTION","multiple instantiations of feature '"+reactionParticipants[i].getStructure(),Issue.SEVERITY_WARNING));
			}
			productCount++;
		}
	}
	if (reactantCount!=1){
		issueList.add(new Issue(this,ISSUECATEGORY_KineticsApplicability,"GHK Kinetics must have exactly one reactant",Issue.SEVERITY_ERROR));
	}
	if (productCount!=1){
		issueList.add(new Issue(this,ISSUECATEGORY_KineticsApplicability,"GHK Kinetics must have exactly one product",Issue.SEVERITY_WARNING));
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 9:52:55 AM)
 * @return cbit.vcell.model.KineticsDescription
 */
public KineticsDescription getKineticsDescription() {
	return KineticsDescription.GHK;
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 3:44:55 PM)
 * @return cbit.vcell.model.KineticsParameter
 */
public KineticsParameter getPermeabilityParameter() {
	return getKineticsParameterFromRole(ROLE_Permeability);
}
/**
 * Insert the method's description here.
 * Creation date: (3/31/2004 3:56:05 PM)
 */
protected void refreshUnits() {
	if (bRefreshingUnits){
		return;
	}
	try {
		bRefreshingUnits=true;
		Kinetics.KineticsParameter rateParm = getRateParameter();
		if (rateParm != null){
			rateParm.setUnitDefinition(VCUnitDefinition.UNIT_uM_um_per_s);
		}
		Kinetics.KineticsParameter currentParm = getCurrentParameter();
		if (currentParm != null){
			currentParm.setUnitDefinition(VCUnitDefinition.UNIT_pA_per_um2);
		}
		Kinetics.KineticsParameter permeabilityParm = getPermeabilityParameter();
		if (permeabilityParm != null){
			permeabilityParm.setUnitDefinition(VCUnitDefinition.UNIT_um_per_s);
		}
	}finally{
		bRefreshingUnits=false;
	}	
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2003 12:05:14 AM)
 * @exception org.vcell.expression.ExpressionException The exception description.
 */
protected void updateGeneratedExpressions() throws org.vcell.expression.ExpressionException, PropertyVetoException {
	KineticsParameter currentParm = getKineticsParameterFromRole(ROLE_Current);
	KineticsParameter rateParm = getKineticsParameterFromRole(ROLE_Rate);
	if (currentParm==null && rateParm==null){
		return;
	}
	
	KineticsParameter P = getKineticsParameterFromRole(ROLE_Permeability);
	ReservedSymbol F = ReservedSymbol.FARADAY_CONSTANT;
	ReservedSymbol F_nmol = ReservedSymbol.FARADAY_CONSTANT_NMOLE;
	ReservedSymbol K_GHK = ReservedSymbol.K_GHK;
	ReservedSymbol R = ReservedSymbol.GAS_CONSTANT;
	ReservedSymbol T = ReservedSymbol.TEMPERATURE;
	MembraneVoltage V = ((Membrane)getReactionStep().getStructure()).getMembraneVoltage();
	int z = (int)getReactionStep().getChargeCarrierValence().getConstantValue();
	
	ReactionParticipant reactionParticipants[] = getReactionStep().getReactionParticipants();
	Flux R0 = null;
	Flux P0 = null;
	for (int i = 0; i < reactionParticipants.length; i++){
		if (reactionParticipants[i] instanceof Flux && 
			reactionParticipants[i].getStructure().compareEqual(((Membrane)getReactionStep().getStructure()).getInsideFeature()) &&
			R0 == null){
			R0 = (Flux)reactionParticipants[i];
		}
		if (reactionParticipants[i] instanceof Flux && 
			reactionParticipants[i].getStructure().compareEqual(((Membrane)getReactionStep().getStructure()).getOutsideFeature()) &&
			P0 == null){
			P0 = (Flux)reactionParticipants[i];
		}
	}
	
	//"-A0*pow("+VALENCE_SYMBOL+",2)*"+VOLTAGE_SYMBOL+"*pow("+F+",2)/("+R+"*"+T+")*(R0-(P0*exp(-"+VALENCE_SYMBOL+"*"+F+"*"+VOLTAGE_SYMBOL+"/("+R+"*"+T+"))))/(1 - exp(-"+VALENCE_SYMBOL+"*"+F+"*"+VOLTAGE_SYMBOL+"/("+R+"*"+T+")))"
	if (R0!=null && P0!=null && P!=null){
		IExpression newCurrExp = ExpressionFactory.createExpression("-"+P.getName()+"*"+K_GHK.getName()+"*pow("+z+",2)*"+V.getName()+"*pow("+F.getName()+",2)/("+R.getName()+"*"+T.getName()+")*("+R0.getName()+"-("+P0.getName()+"*exp(-"+z+"*"+F.getName()+"*"+V.getName()+"/("+R.getName()+"*"+T.getName()+"))))/(1 - exp(-"+z+"*"+F.getName()+"*"+V.getName()+"/("+R.getName()+"*"+T.getName()+")))");
		newCurrExp.bindExpression(getReactionStep());
		currentParm.setExpression(newCurrExp);
		if (getReactionStep().getPhysicsOptions() == ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL){
			IExpression tempRateExpression = null;
			if (getReactionStep() instanceof SimpleReaction){
				ReservedSymbol N_PMOLE = ReservedSymbol.N_PMOLE;
				tempRateExpression = ExpressionFactory.mult(ExpressionFactory.createExpression("("+N_PMOLE.getName()+"/("+z+"*"+F.getName()+"))"), ExpressionFactory.createExpression(currentParm.getName()));
			}else{
				tempRateExpression = ExpressionFactory.createExpression(currentParm.getName()+"/("+z+"*"+F_nmol.getName()+")");
			}
			tempRateExpression.bindExpression(getReactionStep());
			if (rateParm == null){
				addKineticsParameter(new KineticsParameter(getDefaultParameterName(ROLE_Rate),tempRateExpression,ROLE_Rate,VCUnitDefinition.UNIT_molecules_per_um2_per_s));
			}else{
				rateParm.setExpression(tempRateExpression);
			}
		}else{
			if (rateParm != null && !rateParm.getExpression().isZero()){
				//removeKineticsParameter(rateParm);
				rateParm.setExpression(ExpressionFactory.createExpression(0.0));
			}
		}
	}
}
}

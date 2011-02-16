package cbit.vcell.model;

import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (2/18/2002 5:07:08 PM)
 * @author: Anuradha Lakshminarayana
 */
public class GeneralPermeabilityKinetics extends DistributedKinetics {
/**
 * GeneralPermeabilityKinetics constructor comment.
 * @param reactionStep cbit.vcell.model.ReactionStep
 * @exception java.lang.Exception The exception description.
 */
public GeneralPermeabilityKinetics(ReactionStep reactionStep) throws ExpressionException {
	super(KineticsDescription.GeneralPermeability.getName(),reactionStep);
	try {
		KineticsParameter currentParm = new KineticsParameter(getDefaultParameterName(ROLE_CurrentDensity),new Expression(0.0),ROLE_CurrentDensity,null);
		KineticsParameter rateParm = new KineticsParameter(getDefaultParameterName(ROLE_ReactionRate),new Expression(0.0),ROLE_ReactionRate,null);
		KineticsParameter permeabilityParam = new KineticsParameter(getDefaultParameterName(ROLE_Permeability),new Expression(0.0),ROLE_Permeability,null);

		setKineticsParameters(new KineticsParameter[] { currentParm, rateParm, permeabilityParam });
		updateGeneratedExpressions();
		refreshUnits();
	}catch (java.beans.PropertyVetoException e){
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
	if (!(obj instanceof GeneralPermeabilityKinetics)){
		return false;
	}
	
	GeneralPermeabilityKinetics gpk = (GeneralPermeabilityKinetics)obj;

	if (!compareEqual0(gpk)){
		return false;
	}
	
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 3:26:54 PM)
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
				issueList.add(new Issue(this,IssueCategory.InternalError,"multiple instantiations of feature '"+reactionParticipants[i].getStructure(),Issue.SEVERITY_WARNING));
			}
			reactantCount++;
		}
		if (reactionParticipants[i] instanceof Flux && 
			reactionParticipants[i].getStructure().compareEqual(((Membrane)getReactionStep().getStructure()).getOutsideFeature())){
			if (reactionParticipants[i].getStructure()!=((Membrane)getReactionStep().getStructure()).getOutsideFeature()){
				issueList.add(new Issue(this,IssueCategory.InternalError,"multiple instantiations of feature '"+reactionParticipants[i].getStructure(),Issue.SEVERITY_WARNING));
			}
			productCount++;
		}
	}
	if (reactantCount!=1){
		issueList.add(new Issue(this,IssueCategory.KineticsApplicability,"GeneralPermeability Kinetics must have exactly one reactant",Issue.SEVERITY_ERROR));
	}
	if (productCount!=1){
		issueList.add(new Issue(this,IssueCategory.KineticsApplicability,"GeneralPermeability Kinetics must have exactly one product",Issue.SEVERITY_WARNING));
	}
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
 * Creation date: (8/6/2002 9:52:55 AM)
 * @return cbit.vcell.model.KineticsDescription
 */
public KineticsDescription getKineticsDescription() {
	return KineticsDescription.GeneralPermeability;
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
		
		Kinetics.KineticsParameter rateParm = getReactionRateParameter();
		if (rateParm != null){
			rateParm.setUnitDefinition(cbit.vcell.units.VCUnitDefinition.UNIT_uM_um_per_s);
		}
		Kinetics.KineticsParameter currentDensityParm = getCurrentDensityParameter();
		if (currentDensityParm != null){
			currentDensityParm.setUnitDefinition(cbit.vcell.units.VCUnitDefinition.UNIT_pA_per_um2);
		}
		Kinetics.KineticsParameter permeabilityParm = getPermeabilityParameter();
		if (permeabilityParm != null){
			permeabilityParm.setUnitDefinition(cbit.vcell.units.VCUnitDefinition.UNIT_um_per_s);
		}
	}finally{
		bRefreshingUnits=false;
	}	
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2003 12:05:14 AM)
 * @exception cbit.vcell.parser.ExpressionException The exception description.
 */
protected void updateGeneratedExpressions() throws cbit.vcell.parser.ExpressionException, java.beans.PropertyVetoException {
	KineticsParameter rateParm = getKineticsParameterFromRole(ROLE_ReactionRate);
	KineticsParameter currentParm = getKineticsParameterFromRole(ROLE_CurrentDensity);
	KineticsParameter permeability = getKineticsParameterFromRole(ROLE_Permeability);
	if (currentParm==null && rateParm==null){
		return;
	}
	
	ReactionParticipant reactionParticipants[] = getReactionStep().getReactionParticipants();
	Flux fInside = null;
	Flux fOutside = null;
	for (int i = 0; i < reactionParticipants.length; i++){
		if (reactionParticipants[i] instanceof Flux && 
			reactionParticipants[i].getStructure().compareEqual(((Membrane)getReactionStep().getStructure()).getInsideFeature()) &&
			fInside == null){
			fInside = (Flux)reactionParticipants[i];
		}
		if (reactionParticipants[i] instanceof Flux && 
			reactionParticipants[i].getStructure().compareEqual(((Membrane)getReactionStep().getStructure()).getOutsideFeature()) &&
			fOutside == null){
			fOutside = (Flux)reactionParticipants[i];
		}
	}
	
	if (fInside!=null && fOutside!=null){
		Expression z = new Expression(getReactionStep().getChargeCarrierValence().getConstantValue());
		Expression outside_exp = getSymbolExpression(fOutside.getSpeciesContext());
		Expression inside_exp = getSymbolExpression(fInside.getSpeciesContext());
		Expression permeability_exp = getSymbolExpression(permeability);
		
		// P * (fOutside - fInside)
		Expression newRateExp = Expression.mult(permeability_exp, Expression.add(outside_exp, Expression.negate(inside_exp)));		
		rateParm.setExpression(newRateExp);

		if (getReactionStep().getPhysicsOptions() == ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL){
			Expression tempCurrentExpression = null;
			Expression rate = getSymbolExpression(rateParm);
			Expression F_nmol = getSymbolExpression(ReservedSymbol.FARADAY_CONSTANT_NMOLE);
			tempCurrentExpression = Expression.mult(rate, z, F_nmol);
			if (currentParm == null){
				addKineticsParameter(new KineticsParameter(getDefaultParameterName(ROLE_CurrentDensity),tempCurrentExpression,ROLE_CurrentDensity,null));
			}else{
				currentParm.setExpression(tempCurrentExpression);
			}
		}else{
			if (currentParm != null && !currentParm.getExpression().isZero()){
				currentParm.setExpression(new Expression(0.0));
			}
		}
	}
}
}

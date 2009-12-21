package cbit.vcell.model;
import java.beans.PropertyVetoException;
import java.util.Vector;

import org.vcell.util.Issue;
import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.units.VCUnitDefinition;
/**
 * Insert the type's description here.
 * Creation date: (8/9/2006 5:45:48 PM)
 * @author: Anuradha Lakshminarayana
 */
public class GeneralCurrentLumpedKinetics extends LumpedKinetics {
/**
 * GeneralTotalKinetics constructor comment.
 * @param name java.lang.String
 * @param reactionStep cbit.vcell.model.ReactionStep
 */
public GeneralCurrentLumpedKinetics(ReactionStep reactionStep) throws ExpressionException {
	super(KineticsDescription.GeneralCurrentLumped.getName(), reactionStep);
	try {
		KineticsParameter lumpedCurrentParm = new KineticsParameter(getDefaultParameterName(ROLE_LumpedCurrent),new Expression(0.0),ROLE_LumpedCurrent,null);
		KineticsParameter lumpedReactionRateParm = new KineticsParameter(getDefaultParameterName(ROLE_LumpedReactionRate),new Expression(0.0),ROLE_LumpedReactionRate,null);

		setKineticsParameters(new KineticsParameter[] { lumpedCurrentParm, lumpedReactionRateParm });
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
public boolean compareEqual(Matchable obj) {
	if (obj == this){
		return true;
	}
	if (!(obj instanceof GeneralCurrentLumpedKinetics)){
		return false;
	}
	
	GeneralCurrentLumpedKinetics gck = (GeneralCurrentLumpedKinetics)obj;

	if (!compareEqual0(gck)){
		return false;
	}
	
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 3:08:25 PM)
 * @return cbit.util.Issue[]
 */
public void gatherIssues(Vector<Issue> issueList){

	super.gatherIssues(issueList);
	
	if (getReactionStep() instanceof SimpleReaction){
		issueList.add(new Issue(this,ISSUECATEGORY_KineticsApplicability,"General Current Kinetics expected within a flux reaction only",Issue.SEVERITY_ERROR));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 5:45:48 PM)
 * @return cbit.vcell.model.KineticsDescription
 */
public KineticsDescription getKineticsDescription() {
	return KineticsDescription.GeneralCurrentLumped;
}



/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 5:45:48 PM)
 */
protected void refreshUnits() {
	if (bRefreshingUnits){
		return;
	}
	try {
		bRefreshingUnits=true;
		
		Kinetics.KineticsParameter rateParm = getLumpedReactionRateParameter();
		if (getReactionStep() instanceof FluxReaction){
			if (rateParm != null){
				rateParm.setUnitDefinition(VCUnitDefinition.UNIT_molecules_per_s);
			}
		}else if (getReactionStep() instanceof SimpleReaction){
			throw new RuntimeException("General Current Lumped Kinetics not expected within a flux reaction only");
		}
		Kinetics.KineticsParameter currentParm = getLumpedCurrentParameter();
		if (currentParm != null){
			currentParm.setUnitDefinition(VCUnitDefinition.UNIT_pA);
		}
	}finally{
		bRefreshingUnits=false;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 5:45:48 PM)
 * @exception cbit.vcell.parser.ExpressionException The exception description.
 */
protected void updateGeneratedExpressions() throws ExpressionException, java.beans.PropertyVetoException {
	KineticsParameter lumpedCurrentParm = getLumpedCurrentParameter();
	KineticsParameter lumpedReactionRate = getLumpedReactionRateParameter();
	
	if (lumpedCurrentParm==null && lumpedReactionRate==null){
		return;
	}
	
	if (getReactionStep().getPhysicsOptions() == ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL){
		Expression z = new Expression(getReactionStep().getChargeCarrierValence().getConstantValue());
		Expression F = getSymbolExpression(ReservedSymbol.FARADAY_CONSTANT);
		Expression N_PMOLE = getSymbolExpression(ReservedSymbol.N_PMOLE);
		Expression lumpedCurrent = getSymbolExpression(lumpedCurrentParm);
//		Expression tempRateExpression = Expression.mult(new Expression("("+N_PMOLE.getName()+"/("+z+"*"+F.getName()+"))"), new Expression(lumpedCurrentParm.getName()));
		Expression tempRateExpression = Expression.mult(Expression.div(N_PMOLE, Expression.mult(z, F)), lumpedCurrent);
		
		if (lumpedReactionRate == null){
			addKineticsParameter(new KineticsParameter(getDefaultParameterName(ROLE_LumpedReactionRate),tempRateExpression,ROLE_LumpedReactionRate,VCUnitDefinition.UNIT_molecules_per_s));
		}else{
			lumpedReactionRate.setExpression(tempRateExpression);
		}
	}else{
		if (lumpedReactionRate != null && !lumpedReactionRate.getExpression().isZero()){
			//removeKineticsParameter(rateParm);
			lumpedReactionRate.setExpression(new Expression(0.0));
		}
	}
}
}
package cbit.vcell.model;
import java.beans.PropertyVetoException;

import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.units.VCUnitDefinition;
/**
 * Insert the type's description here.
 * Creation date: (8/9/2006 5:45:48 PM)
 * @author: Anuradha Lakshminarayana
 */
public class GeneralLumpedKinetics extends LumpedKinetics {
/**
 * GeneralTotalKinetics constructor comment.
 * @param name java.lang.String
 * @param reactionStep cbit.vcell.model.ReactionStep
 */
public GeneralLumpedKinetics(ReactionStep reactionStep) throws cbit.vcell.parser.ExpressionException {
	super(KineticsDescription.GeneralLumped.getName(), reactionStep);
	try {
		KineticsParameter lumpedCurrentParm = new KineticsParameter(getDefaultParameterName(ROLE_LumpedCurrent),new Expression(0.0),ROLE_LumpedCurrent,null);
		KineticsParameter lumpedReactionRateParm = new KineticsParameter(getDefaultParameterName(ROLE_LumpedReactionRate),new Expression(0.0),ROLE_LumpedReactionRate,null);

		if (reactionStep.getStructure() instanceof Membrane){
			setKineticsParameters(new KineticsParameter[] { lumpedCurrentParm, lumpedReactionRateParm });
		}else{
			setKineticsParameters(new KineticsParameter[] { lumpedReactionRateParm });
		}
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
public boolean compareEqual(cbit.util.Matchable obj) {
	if (obj == this){
		return true;
	}
	if (!(obj instanceof GeneralLumpedKinetics)){
		return false;
	}
	
	GeneralLumpedKinetics gck = (GeneralLumpedKinetics)obj;

	if (!compareEqual0(gck)){
		return false;
	}
	
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 5:45:48 PM)
 * @return cbit.vcell.model.KineticsDescription
 */
public KineticsDescription getKineticsDescription() {
	return KineticsDescription.GeneralLumped;
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
		
		Kinetics.KineticsParameter lumpedReactionRateParm = getLumpedReactionRateParameter();
		Kinetics.KineticsParameter lumpedCurrentParm = getLumpedCurrentParameter();
	
		if (getReactionStep().getStructure() instanceof Membrane){
			if (lumpedCurrentParm!=null){
				lumpedCurrentParm.setUnitDefinition(VCUnitDefinition.UNIT_pA);
			}
		}
		if (lumpedReactionRateParm!=null){
			lumpedReactionRateParm.setUnitDefinition(VCUnitDefinition.UNIT_molecules_per_s);
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
protected void updateGeneratedExpressions() throws cbit.vcell.parser.ExpressionException, java.beans.PropertyVetoException {
	KineticsParameter lumpedCurrentParm = getLumpedCurrentParameter();
	KineticsParameter lumpedReactionRate = getLumpedReactionRateParameter();
	
	if (lumpedCurrentParm==null && lumpedReactionRate==null){
		return;
	}
	
	if (getReactionStep().getPhysicsOptions() == ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL){
		Expression tempCurrentExpression = null;
		int z = (int)getReactionStep().getChargeCarrierValence().getConstantValue();
		ReservedSymbol F = ReservedSymbol.FARADAY_CONSTANT;
		ReservedSymbol N_PMOLE = ReservedSymbol.N_PMOLE;

		tempCurrentExpression = Expression.mult(new Expression("("+z+"*"+F.getName()+"/"+N_PMOLE.getName()+")"), new Expression(lumpedReactionRate.getName()));
		tempCurrentExpression.bindExpression(getReactionStep());
		if (lumpedCurrentParm == null){
			addKineticsParameter(new KineticsParameter(getDefaultParameterName(ROLE_LumpedCurrent),tempCurrentExpression,ROLE_LumpedCurrent,cbit.vcell.units.VCUnitDefinition.UNIT_pA));
		}else{
			lumpedCurrentParm.setExpression(tempCurrentExpression);
		}
	}else{
		if (lumpedCurrentParm != null && !lumpedCurrentParm.getExpression().isZero()){
			//removeKineticsParameter(currentParm);
			lumpedCurrentParm.setExpression(new Expression(0.0));
		}
	}
}
}
package cbit.vcell.model;
import java.beans.PropertyVetoException;

import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;

import org.vcell.expression.IExpression;
import org.vcell.units.VCUnitDefinition;
/**
 * Insert the type's description here.
 * Creation date: (8/9/2006 5:45:48 PM)
 * @author: Anuradha Lakshminarayana
 */
public class GeneralTotalKinetics extends Kinetics {
/**
 * GeneralTotalKinetics constructor comment.
 * @param name java.lang.String
 * @param reactionStep cbit.vcell.model.ReactionStep
 */
public GeneralTotalKinetics(ReactionStep reactionStep) throws ExpressionException {
	super(KineticsDescription.GeneralTotal.getName(), reactionStep);
	try {
		KineticsParameter rateParm = new KineticsParameter(getDefaultParameterName(ROLE_Rate),ExpressionFactory.createExpression(0.0),ROLE_Rate,null);
		KineticsParameter currentParm = new KineticsParameter(getDefaultParameterName(ROLE_Current),ExpressionFactory.createExpression(0.0),ROLE_Current,null);
		KineticsParameter totalRateParm = new KineticsParameter(getDefaultParameterName(ROLE_TotalRate),ExpressionFactory.createExpression(0.0),ROLE_TotalRate,null);
		KineticsParameter compartmentSizeParm = new KineticsParameter(getDefaultParameterName(ROLE_AssumedCompartmentSize),ExpressionFactory.createExpression(0.0),ROLE_AssumedCompartmentSize,null);

		if (reactionStep.getStructure() instanceof Membrane){
			setKineticsParameters(new KineticsParameter[] { rateParm, currentParm, totalRateParm, compartmentSizeParm });
		}else{
			setKineticsParameters(new KineticsParameter[] { rateParm, totalRateParm, compartmentSizeParm });
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
public boolean compareEqual(org.vcell.util.Matchable obj) {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 3:37:07 PM)
 * @return cbit.vcell.model.KineticsParameter
 */
public KineticsParameter getAssumedCompartmentSizeParamter() {
	return getKineticsParameterFromRole(ROLE_AssumedCompartmentSize);
}


/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 5:45:48 PM)
 * @return cbit.vcell.model.KineticsDescription
 */
public KineticsDescription getKineticsDescription() {
	return KineticsDescription.GeneralTotal;
}


/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 3:37:07 PM)
 * @return cbit.vcell.model.KineticsParameter
 */
public KineticsParameter getTotalRateParamter() {
	return getKineticsParameterFromRole(ROLE_TotalRate);
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
		
		Kinetics.KineticsParameter rateParm = getRateParameter();
		Kinetics.KineticsParameter currentParm = getCurrentParameter();
		Kinetics.KineticsParameter totalRateParm = getTotalRateParamter();
		Kinetics.KineticsParameter compartmentSizeParm = getAssumedCompartmentSizeParamter();
		
		if (getReactionStep().getStructure() instanceof Membrane){
			if (rateParm!=null){
				rateParm.setUnitDefinition(VCUnitDefinition.UNIT_molecules_per_um2_per_s);
			}
			if (currentParm!=null){
				currentParm.setUnitDefinition(VCUnitDefinition.UNIT_pA_per_um2);
			}
			if (compartmentSizeParm!=null){
				compartmentSizeParm.setUnitDefinition(VCUnitDefinition.UNIT_um2);
			}
			if (totalRateParm!=null){
				VCUnitDefinition totalRateUnitDefn = rateParm.getUnitDefinition().multiplyBy(compartmentSizeParm.getUnitDefinition());
				totalRateParm.setUnitDefinition(totalRateUnitDefn);
			}
		}else{
			if (rateParm!=null){
				rateParm.setUnitDefinition(VCUnitDefinition.UNIT_uM_per_s);
			}
			if (compartmentSizeParm!=null){
				compartmentSizeParm.setUnitDefinition(VCUnitDefinition.UNIT_um3);
			}
			if (totalRateParm!=null){
				// the units for the total rate parameter will be molecules/sec, but it is more understandable to
				// express it in terms of rateParam, compartmentSize. To convert it we need to use the factor : KMOLE from ReservedSymbols (uM_um3_per_molecules).
				// rate_Units = uM_per_sec
				// compartmentSize_Units = um3
				// KMOLE_Units = uM_um3_per_molecules
				// 		totalRate_Units = (rateParam_Units * compartmentSize_Units)/KMOLE_Units = molecules_per_sec OR items_per_sec.
				
				totalRateParm.setUnitDefinition((rateParm.getUnitDefinition().multiplyBy(compartmentSizeParm.getUnitDefinition())).divideBy(ReservedSymbol.KMOLE.getUnitDefinition()));
			}
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
	KineticsParameter rateParm = getKineticsParameterFromRole(ROLE_Rate);
	KineticsParameter currentParm = getKineticsParameterFromRole(ROLE_Current);
	KineticsParameter totalRate = getKineticsParameterFromRole(ROLE_TotalRate);
	KineticsParameter compartmentSize = getKineticsParameterFromRole(ROLE_AssumedCompartmentSize);
	
	if (currentParm==null && rateParm==null){
		return;
	}
	
	//	new rate Expression(totalRate/compartmentSize), 
	IExpression newRateExp = ExpressionFactory.createExpression(totalRate.getName() + "/" + compartmentSize.getName());
	newRateExp.bindExpression(getReactionStep());
	rateParm.setExpression(newRateExp);
	
	if (getReactionStep().getPhysicsOptions() != ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL){
		if (currentParm != null && !currentParm.getExpression().isZero()){
			//removeKineticsParameter(currentParm);
			currentParm.setExpression(ExpressionFactory.createExpression(0.0));
		}
	}
}
}
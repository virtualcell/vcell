package cbit.vcell.model;

import java.beans.PropertyVetoException;
import java.util.Vector;

import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;


/**
 * DistributedKinetics is the abstract superclass of all reaction kinetics that operate locally (can be defined at a point)
 * and form the basis for distributed parameter, spatial modeling.  This is the "text-book" description of chemical 
 * kinetics (in terms of time rate of change of local concentration).
 * 
 * For electrical transport, current density describes the local charge transport across a unit area of membrane rather than
 * the "lumped" description (total current crossing the entire membrane).
 * 
 * For nonspatial descriptions, this is a less convenient form for some users because it requires separately defining
 * compartment size and time rate of change of concentrations, where only their product matters.  For spatial applications,
 * this is the only form that can give rise to spatially inhomogeneous behavior.
 * 
 * A DistributedKinetics may be formed from a corresponding LumpedKinetics by assuming that the LumpedKinetics can be
 * uniformly distributed within a compartment of known size. 
 *
 * @see LumpedKinetics
 *
 */
public abstract class DistributedKinetics extends Kinetics {

	public DistributedKinetics(String name, ReactionStep reactionStep) {
		super(name, reactionStep);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/14/2003 8:53:00 AM)
	 * @return cbit.vcell.model.Parameter
	 */
	public final KineticsParameter getCurrentDensityParameter() {
		return getKineticsParameterFromRole(ROLE_CurrentDensity);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/14/2003 8:53:00 AM)
	 * @return cbit.vcell.model.Parameter
	 */
	public final KineticsParameter getReactionRateParameter() {
		return getKineticsParameterFromRole(ROLE_ReactionRate);
	}
	
	public final KineticsParameter getAuthoritativeParameter(){
		if (getKineticsDescription().isElectrical()){
			return getCurrentDensityParameter();
		}else{
			return getReactionRateParameter();
		}
	}

	public static DistributedKinetics toDistributedKinetics(LumpedKinetics origLumpedKinetics, double size){
		KineticsParameter[] origLumpedKineticsParms = origLumpedKinetics.getKineticsParameters();
		try {
			Vector<KineticsParameter> parmsToAdd = new Vector<KineticsParameter>();
			
			DistributedKinetics newDistributedKinetics = null;
			if (origLumpedKinetics.getKineticsDescription().isElectrical()){
				newDistributedKinetics = new GeneralCurrentKinetics(origLumpedKinetics.getReactionStep());
				Expression distributionFactor = Expression.invert(new Expression(size)); // from pA to pA.um-2 (current to current density)
				KineticsParameter newDistCurrentDensityParam = newDistributedKinetics.getCurrentDensityParameter();
				KineticsParameter origLumpedCurrentParam = origLumpedKinetics.getLumpedCurrentParameter();
				Expression newDistributedCurrentExp = Expression.mult(distributionFactor,origLumpedCurrentParam.getExpression()).flatten();
				parmsToAdd.add(newDistributedKinetics.new KineticsParameter(newDistCurrentDensityParam.getName(),newDistributedCurrentExp,newDistCurrentDensityParam.getRole(),newDistCurrentDensityParam.getUnitDefinition()));
			}else{
				newDistributedKinetics = new GeneralKinetics(origLumpedKinetics.getReactionStep());
				Expression distributionFactor = null;
				if (origLumpedKinetics.getReactionStep().getStructure() instanceof Membrane){
					if (origLumpedKinetics.getReactionStep() instanceof FluxReaction){
						// KMOLE/size  (from molecules.s-1 to uM.um.s-1)
						distributionFactor = Expression.mult(new Expression(ReservedSymbol.KMOLE.getName()),Expression.invert(new Expression(size)));
					}else if (origLumpedKinetics.getReactionStep() instanceof SimpleReaction){
						// 1/size (from molecules.s-1 to molecules.um-2.s-1)
						distributionFactor = Expression.invert(new Expression(size));
					}else{
						throw new RuntimeException("unexpected ReactionStep type "+origLumpedKinetics.getReactionStep().getClass().getName());
					}
				}else if (origLumpedKinetics.getReactionStep().getStructure() instanceof Feature){
					// KMOLE/size (from molecules.s-1 to uM.s-1)
					distributionFactor = Expression.mult(new Expression(ReservedSymbol.KMOLE.getName()),Expression.invert(new Expression(size)));
				}else{
					throw new RuntimeException("unexpected structure type "+origLumpedKinetics.getReactionStep().getStructure().getClass().getName());
				}
				KineticsParameter distReactionRateParam = newDistributedKinetics.getReactionRateParameter();
				KineticsParameter lumpedReactionRateParm = origLumpedKinetics.getLumpedReactionRateParameter();
				Expression newDistributedReactionRateExp = Expression.mult(distributionFactor,lumpedReactionRateParm.getExpression()).flatten();
				parmsToAdd.add(newDistributedKinetics.new KineticsParameter(distReactionRateParam.getName(),newDistributedReactionRateExp,distReactionRateParam.getRole(),distReactionRateParam.getUnitDefinition()));
			}
			for (int i = 0; i < origLumpedKineticsParms.length; i++) {
				if (origLumpedKineticsParms[i].getRole()!=Kinetics.ROLE_LumpedReactionRate &&
						origLumpedKineticsParms[i].getRole()!=Kinetics.ROLE_LumpedCurrent){
					parmsToAdd.add(newDistributedKinetics.new KineticsParameter(origLumpedKineticsParms[i].getName(),new Expression(origLumpedKineticsParms[i].getExpression()),Kinetics.ROLE_UserDefined,origLumpedKineticsParms[i].getUnitDefinition()));
				}
			}
			newDistributedKinetics.addKineticsParameters(parmsToAdd.toArray(new KineticsParameter[parmsToAdd.size()]));
			return newDistributedKinetics;
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create distributed Kinetics for reaction: \""+origLumpedKinetics.getReactionStep().getName()+"\": "+e.getMessage());
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create distributed Kinetics for reaction: \""+origLumpedKinetics.getReactionStep().getName()+"\": "+e.getMessage());
		}
	}

}

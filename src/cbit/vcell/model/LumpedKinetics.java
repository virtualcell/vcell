package cbit.vcell.model;

import java.beans.PropertyVetoException;
import java.util.Vector;

import org.vcell.util.BeanUtils;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;


/**
 * LumpedKinetics is the abstract superclass of all reaction kinetics that operate on pools of molecules 
 * and describe the rate of change of the total number of molecules or total current across a membrane.
 * 
 * For electrical transport, total current (rather than current density) is the "lumped" description of
 * charge transport.
 * 
 * For nonspatial descriptions, this can be a convenient form.  However, for spatial models either the
 * LumpedKinetics has to be translated to a corresponding DistributedKinetics (describing a distributed 
 * parameter system) or these will map to Region variables and Region equations that described lumped quantities.
 * 
 * A LumpedKinetics may be formed from a corresponding DistributedKinetics by integrating the local behavior 
 * over a given compartment of known size.  For nonspatial models, no assumptions are necessary, for spatial models
 * an assumption of uniform behavior over the compartment is required (e.g. no gradients or inhomogenieties).
 *
 * @see DistributedKinetics
 *
 */
public abstract class LumpedKinetics extends Kinetics {

	public LumpedKinetics(String name, ReactionStep reactionStep) {
		super(name, reactionStep);
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (8/6/2002 3:37:07 PM)
	 * @return cbit.vcell.model.KineticsParameter
	 */
	public KineticsParameter getLumpedReactionRateParameter() {
		return getKineticsParameterFromRole(ROLE_LumpedReactionRate);
	}

	public final KineticsParameter getAuthoritativeParameter(){
		if (getKineticsDescription().isElectrical()){
			return getLumpedCurrentParameter();
		}else{
			return getLumpedReactionRateParameter();
		}
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (8/6/2002 3:37:07 PM)
	 * @return cbit.vcell.model.KineticsParameter
	 */
	public KineticsParameter getLumpedCurrentParameter() {
		return getKineticsParameterFromRole(ROLE_LumpedCurrent);
	}

	public static LumpedKinetics toLumpedKinetics(DistributedKinetics distributedKinetics, double size){
		KineticsParameter[] distKineticsParms = distributedKinetics.getKineticsParameters();
		try {
			Vector<KineticsParameter> parmsToAdd = new Vector<KineticsParameter>();
			
			LumpedKinetics lumpedKinetics = null;
			if (distributedKinetics.getKineticsDescription().isElectrical()){
				lumpedKinetics = new GeneralCurrentLumpedKinetics(distributedKinetics.getReactionStep());
				Expression lumpingFactor = new Expression(size); // from pA.um-2 to pA (current density to current)
				KineticsParameter distCurrentDensityParam = distributedKinetics.getCurrentDensityParameter();
				KineticsParameter lumpedCurrentParam = lumpedKinetics.getLumpedCurrentParameter();
				Expression newLumpedCurrentExp = Expression.mult(lumpingFactor,distCurrentDensityParam.getExpression()).flatten();
				parmsToAdd.add(lumpedKinetics.new KineticsParameter(lumpedCurrentParam.getName(),newLumpedCurrentExp,lumpedCurrentParam.getRole(),lumpedCurrentParam.getUnitDefinition()));
			}else{
				lumpedKinetics = new GeneralLumpedKinetics(distributedKinetics.getReactionStep());
				Expression lumpingFactor = null;
				if (distributedKinetics.getReactionStep().getStructure() instanceof Membrane){
					if (distributedKinetics.getReactionStep() instanceof FluxReaction){
						// size/KMOLE  (from uM.um.s-1 to molecules.s-1)
						lumpingFactor = Expression.mult(new Expression(size),Expression.invert(new Expression(ReservedSymbol.KMOLE.getName())));
					}else if (distributedKinetics.getReactionStep() instanceof SimpleReaction){
						// size (from molecules.um-2.s-1 to molecules.s-1)
						lumpingFactor = new Expression(size);
					}else{
						throw new RuntimeException("unexpected ReactionStep type "+distributedKinetics.getReactionStep().getClass().getName());
					}
				}else if (distributedKinetics.getReactionStep().getStructure() instanceof Feature){
					// size/KMOLE (from uM.s-1 to molecules.s-1)
					lumpingFactor = Expression.mult(new Expression(size),Expression.invert(new Expression(ReservedSymbol.KMOLE.getName())));
				}else{
					throw new RuntimeException("unexpected structure type "+distributedKinetics.getReactionStep().getStructure().getClass().getName());
				}
				KineticsParameter distReactionRateParam = distributedKinetics.getReactionRateParameter();
				KineticsParameter lumpedReactionRateParm = lumpedKinetics.getLumpedReactionRateParameter();
				Expression newLumpedRateExp = Expression.mult(lumpingFactor,distReactionRateParam.getExpression()).flatten();
				parmsToAdd.add(lumpedKinetics.new KineticsParameter(lumpedReactionRateParm.getName(),newLumpedRateExp,lumpedReactionRateParm.getRole(),lumpedReactionRateParm.getUnitDefinition()));
			}
			for (int i = 0; i < distKineticsParms.length; i++) {
				if (distKineticsParms[i].getRole()!=Kinetics.ROLE_ReactionRate &&
					distKineticsParms[i].getRole()!=Kinetics.ROLE_CurrentDensity){
					parmsToAdd.add(lumpedKinetics.new KineticsParameter(distKineticsParms[i].getName(),new Expression(distKineticsParms[i].getExpression()),Kinetics.ROLE_UserDefined,distKineticsParms[i].getUnitDefinition()));
				}
			}
			lumpedKinetics.addKineticsParameters(parmsToAdd.toArray(new KineticsParameter[parmsToAdd.size()]));
			return lumpedKinetics;
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create lumped Kinetics for reaction: \""+distributedKinetics.getReactionStep().getName()+"\": "+e.getMessage());
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create lumped Kinetics for reaction: \""+distributedKinetics.getReactionStep().getName()+"\": "+e.getMessage());
		}
	}

}

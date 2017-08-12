package org.vcell.sbml;

import org.vcell.util.BeanUtils;

import cbit.vcell.model.DistributedKinetics;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.parser.Expression;

/**
 * adapt {@link Kinetics} class hiearchy for SBML
 * @author GWeatherby
 */
public abstract class KineticsAdapter {
	
	public abstract Expression getExpression( );
	
	public abstract boolean isLocal( );
	
	/**
	 * @param k not null
	 * @return adapter for type
	 * @throws SbmlException if type not recognized
	 * @throws {@link NullPointerException} if k null
	 */
	public static KineticsAdapter create(Kinetics k) throws SbmlException {
		DistributedKinetics dk = BeanUtils.downcast(DistributedKinetics.class,k);
		if (dk != null) {
			return new Distributed(dk);
		}
		
		LumpedKinetics lk = BeanUtils.downcast(LumpedKinetics.class, k);
		if (lk != null) {
			return new Lumped(lk);
		}
		throw new SbmlException("Unknown Kinetics subclass " + k.getClass().getName());
	}
	
	private static class Distributed extends KineticsAdapter {
		final DistributedKinetics dk;
		
		Distributed(DistributedKinetics dk) {
			this.dk = dk;
		}

		@Override
		public Expression getExpression() {
			return dk.getReactionRateParameter().getExpression();
		}

		@Override
		public boolean isLocal() {
			return true;
		}
	}
	
	private static class Lumped extends KineticsAdapter {
		final LumpedKinetics lk;

		Lumped(LumpedKinetics lk) {
			this.lk = lk;
		}

		@Override
		public Expression getExpression() {
			return lk.getLumpedReactionRateParameter().getExpression();
		}

		@Override
		public boolean isLocal() {
			return false;
		}
	}

}

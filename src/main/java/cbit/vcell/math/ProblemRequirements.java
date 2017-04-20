package cbit.vcell.math;

import java.util.function.Predicate;

import org.vcell.util.BeanUtils;


/**
 * boolean getters of {@link MathDescription} that
 * determine which Solver to select
 * @author gweatherby
 *
 */
public interface ProblemRequirements {

	public abstract boolean isSpatial();

	public abstract boolean isSpatialHybrid();

	public abstract boolean isSpatialStoch();

	public abstract boolean isNonSpatialStoch();
	
	public abstract boolean hasDirichletAtMembrane();
	
	public abstract boolean hasFastSystems();
	
	public abstract boolean isRuleBased();
	
	public abstract boolean isMovingMembrane();

	/**
	 * validator
	 */
	public static class Checker {
		
		/**
		 * return true if SolverSelector state is valid
		 * @param ss to check
		 */
		public static boolean isValid(ProblemRequirements ss) {
			return validateLogic(ss) == null;
		}
		
		/**
		 * throw exception if not valid
		 * @param ss to check
		 * @throws IllegalStateException
		 */
		public  static void validate(ProblemRequirements ss) {
			 String msg = validateLogic(ss);
			 if (msg != null) {
				 throw new IllegalStateException(ss + msg);
			 }
		}
		/**
		 * common implementation of {@link #isValid(ProblemRequirements)} and {@link #validate(ProblemRequirements)} logic
		 * This is out of date, should include rule based states
		 * @param ss
		 * @return null if good, error message if not
		 */
		private static String validateLogic(ProblemRequirements ss) {
			if (ss.isNonSpatialStoch() && ss.isSpatial()) {
				return ": invalid state: non spatial stochastic may not be spatial";
			}
			if (ss.isSpatialStoch() && !ss.isSpatial()) {
				return ": invalid state: spatial stochastic must be spatial";
			}
			if (ss.isSpatialHybrid() && !ss.isSpatial()) {
				return ": invalid state: spatial hybrid must be spatial";
			}
			if (ss.isMovingMembrane() && ( !ss.isSpatial() || ss.isRuleBased() ) ) {
				return ": invalid state: moving boundary must be spatial and not rule based"; 
			}
			
			
			return null;
		}
	}
	/**
	 * Facade to provide human readable String 
	 */
	public static class Explain {
		public static String describe(ProblemRequirements selector) {
			String desc = selector.isSpatial() ? "Spatial " : "Non-spatial";
			if (selector.isSpatialHybrid()) {
				desc += " hybrid";
			}
			if (selector.isSpatialStoch() || selector.isNonSpatialStoch()) {
				desc += " stoch";
			}
			if (selector.hasFastSystems()) {
				desc += " fast";
			}
			if (selector.hasDirichletAtMembrane()) {
				desc += " dirichlet";
			}
			return desc; 
		}
	}
}
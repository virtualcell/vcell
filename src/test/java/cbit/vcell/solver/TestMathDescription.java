package cbit.vcell.solver;

import org.junit.Test;

import cbit.vcell.math.ProblemRequirements;

public class TestMathDescription implements ProblemRequirements {

	/* (non-Javadoc)
	 * @see cbit.vcell.solver.SolverSelector#isSpatial()
	 */
	@Override
	public boolean isSpatial() {
		int masked = bits&1;
		return masked != 0;
	}

	/* (non-Javadoc)
	 * @see cbit.vcell.solver.SolverSelector#isSpatialHybrid()
	 */
	@Override
	public boolean isSpatialHybrid() {
		int masked = bits&2;
		return masked != 0;
	}

	/* (non-Javadoc)
	 * @see cbit.vcell.solver.SolverSelector#hasFastSystems()
	 */
	@Override
	public boolean hasFastSystems() {
		int masked = bits&4;
		return masked != 0;
	}

	/* (non-Javadoc)
	 * @see cbit.vcell.solver.SolverSelector#isSpatialStoch()
	 */
	@Override
	public boolean isSpatialStoch() {
		int masked = bits&8;
		return masked != 0;
	}

	/* (non-Javadoc)
	 * @see cbit.vcell.solver.SolverSelector#hasDirichletAtMembrane()
	 */
	@Override
	public boolean hasDirichletAtMembrane() {
		int masked = bits&16;
		return masked != 0;
	}
	
	public void reset( ) {
		bits = 0;
	}
	
	public void nextState( ) { 
		++bits;
		//skip invalid states
		while (!Checker.isValid(this)) {
			++bits;
		}
	}
	
	public boolean hasMoreStates( ) {
		return bits < 65;
		
	}
	
	public int getStateNum( ) {
		return bits;
	}
	
	private int bits;
	
	@Test
	public void showStates( ) {
		TestMathDescription tmd = new TestMathDescription();
		System.out.println (tmd.isSpatial() + " " + tmd.isSpatialHybrid() + " " + tmd.hasFastSystems() + " " + tmd.isSpatialStoch() + " " + tmd.hasDirichletAtMembrane());
		while (tmd.hasMoreStates()) {
			tmd.nextState();
			Checker.validate(tmd);
			System.out.println (tmd.isSpatial() + " " + tmd.isSpatialHybrid() + " " + tmd.hasFastSystems() + " " + tmd.isSpatialStoch() + " " + tmd.hasDirichletAtMembrane()
					+ " " + tmd.isNonSpatialStoch() + " " + tmd.isMovingMembrane());
		}
	}
	

	@Override
	public String toString() {
		return "TMD: spatial " + isSpatial() 
				 + " sp hybrid  " + isSpatialHybrid()
				 + " sp stoch " + isSpatialStoch()
				 + "  ns stoch " + isNonSpatialStoch()
				 + " fast  " + hasFastSystems()
				 + " diri  " + hasDirichletAtMembrane()
				 + " moving  " + isMovingMembrane(); 
	}

	/* (non-Javadoc)
	 * @see cbit.vcell.solver.SolverSelector#isNonSpatialStoch()
	 */
	@Override
	public boolean isNonSpatialStoch() {
		int masked = bits&32;
		return masked != 0;
	}
	
	public boolean isMovingMembrane() {
		int masked = bits&64;
		return masked != 0;
	}

	@Override
	public boolean isRuleBased() {
		//throw new UnsupportedOperationException("isRuleBased is not implemented in this JUnit test.");
		return false;
	}


}

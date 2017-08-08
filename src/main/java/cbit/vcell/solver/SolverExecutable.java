package cbit.vcell.solver;

/**
 * enum that maps Solvers to property names to executable file stubs
 * @author gweatherby
 *
 */
public enum SolverExecutable {
	VCellChombo("VCellChombo2D","VCellChombo3D"),
	FiniteVolume("FiniteVolume" ),
	SundialsOde("SundialsSolverStandalone" ),
	Gibson("VCellStoch" ),
	Hybrid_EM("Hybrid_EM" ),
	Hybrid_Mil("Hybrid_MIL" ),
	Hybrid_Mil_Adaptive("Hybrid_MIL_Adaptive" ),
	Smoldyn("smoldyn" ),
	NFSIM("NFsim"),
	MOVING_B("MovingBoundary")
	;
	
	public static class NameInfo {
		/**
		 * executable name without OS specific extensions
		 */
		public final String exeName;
		
		private NameInfo(String exeName) {
			this.exeName = exeName;
		}
		
	}
	private final NameInfo ni[]; 

	/**
	 * supports one executable solver
	 * @param prop
	 * @param exe
	 */
	SolverExecutable(String exe) {
		ni = new NameInfo[1];
		ni[0] = new NameInfo(exe);
	}
	
	/**
	 * supports two executable solver
	 * @param prop
	 * @param exe
	 * @param prop1
	 * @param exe1
	 */
	SolverExecutable(String exe, String exe1) {
		ni = new NameInfo[2];
		ni[0] = new NameInfo(exe);
		ni[1] = new NameInfo(exe1);
	}
	
	public NameInfo[] getNameInfo() {
		return ni;
	}
	
}
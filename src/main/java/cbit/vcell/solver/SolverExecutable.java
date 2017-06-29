package cbit.vcell.solver;

import cbit.vcell.resource.PropertyLoader;

/**
 * enum that maps Solvers to property names to executable file stubs
 * @author gweatherby
 *
 */
public enum SolverExecutable {
	VCellChombo(PropertyLoader.VCellChomboExecutable2D,"VCellChombo2D", 
			PropertyLoader.VCellChomboExecutable3D,"VCellChombo3D"),
	FiniteVolume(PropertyLoader.finiteVolumeExecutableProperty, "FiniteVolume" ),
	SundialsOde(PropertyLoader.sundialsSolverExecutableProperty, "SundialsSolverStandalone" ),
	Gibson(PropertyLoader.stochExecutableProperty, "VCellStoch" ),
	Hybrid_EM(PropertyLoader.hybridEMExecutableProperty,"Hybrid_EM" ),
	Hybrid_Mil(PropertyLoader.hybridMilExecutableProperty, "Hybrid_MIL" ),
	Hybrid_Mil_Adaptive(PropertyLoader.hybridMilAdaptiveExecutableProperty,"Hybrid_MIL_Adaptive" ),
	Smoldyn(PropertyLoader.smoldynExecutableProperty, "smoldyn" ),
	NFSIM(PropertyLoader.nfsimExecutableProperty, "NFsim"),
	MOVING_B(PropertyLoader.MOVING_BOUNDARY_EXE, "MovingBoundary")
	;
	
	public static class NameInfo {
		/**
		 * property name for executable
		 */
		public final String propertyName;
		/**
		 * executable name without OS specific extensions
		 */
		public final String exeName;
		
		private NameInfo(String propertyName, String exeName) {
			this.propertyName = propertyName;
			this.exeName = exeName;
		}
		
	}
	private final NameInfo ni[]; 

	/**
	 * supports one executable solver
	 * @param prop
	 * @param exe
	 */
	SolverExecutable(String prop, String exe) {
		ni = new NameInfo[1];
		ni[0] = new NameInfo(prop,exe);
	}
	
	/**
	 * supports two executable solver
	 * @param prop
	 * @param exe
	 * @param prop1
	 * @param exe1
	 */
	SolverExecutable(String prop, String exe, String prop1, String exe1) {
		ni = new NameInfo[2];
		ni[0] = new NameInfo(prop,exe);
		ni[1] = new NameInfo(prop1,exe1);
	}
	
	public NameInfo[] getNameInfo() {
		return ni;
	}
	
}
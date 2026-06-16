package cbit.vcell.solver;

import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.ResourceUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * enum that maps Solvers to property names to executable file stubs
 * @author gweatherby
 *
 */
public enum SolverExecutable {

	VCellChombo("VCellChombo2D","VCellChombo3D"),
	FiniteVolume("FiniteVolume" ),
	FiniteVolume_PETSc("FiniteVolume_PETSc" ),
	SundialsOde("SundialsSolverStandalone" ),
	Gibson("VCellStoch" ),
	Hybrid_EM("Hybrid_EM" ),
	Hybrid_Mil("Hybrid_MIL" ),
	Hybrid_Mil_Adaptive("Hybrid_MIL_Adaptive" ),
	Smoldyn("smoldyn" ),
	NFSIM("NFsim"),
	LANGEVIN("langevin"),
	MOVING_B("MovingBoundary")
	;

	private final NameInfo[] nameInfo;

	public static class NameInfo {
		/**
		 * executable name without OS specific extensions
		 */
		private final String exeName;

		public String getExecutableName() {
			return this.exeName;
		}

		private NameInfo(String exeName) {
			this.exeName = exeName;
		}
	}

	/**
	 * supports one executable solver
	 * @param exe
	 */
	SolverExecutable(String exe) {
		this.nameInfo = new NameInfo[1];
		this.nameInfo[0] = new NameInfo(exe);
	}
	
	/**
	 * supports two executable solver
	 * @param exeName0
	 * @param exeName1
	 */
	SolverExecutable(String exeName0, String exeName1) {
		nameInfo = new NameInfo[2];
		nameInfo[0] = new NameInfo(exeName0);
		nameInfo[1] = new NameInfo(exeName1);
	}
	
	public NameInfo[] getNameInfo() {
		return nameInfo;
	}

	public List<File> getFullyQualifiedExecutables(){
		NameInfo[] nameInfoArr = this.getNameInfo();
		List<File> qualifiedExes = new ArrayList<>();
		for (NameInfo nameInfo : nameInfoArr) {
			String exeName = nameInfo.getExecutableName() + OperatingSystemInfo.getInstance().getExeBitSuffix();
			File parentDirectory = new File(ResourceUtil.getLocalSolversDirectory(), SolverFamily.getSolverFamilyFromSolver(this).getName());
			qualifiedExes.add(new File(parentDirectory, exeName));
		}
		return qualifiedExes;
	}
	
}
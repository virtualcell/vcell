package cbit.vcell.solver;

import java.util.EnumMap;
import java.util.Map;

public enum SolverFamily {
	FVSOLVER("fvsolver"),
	CVODES("cvodes"),
	LANGEVIN("langevin"),
	OTHER("other");

	private static Map<SolverExecutable, SolverFamily> mapping = new EnumMap<>(SolverExecutable.class);

	static {
		SolverFamily.mapping.put(SolverExecutable.VCellChombo, SolverFamily.OTHER);
		SolverFamily.mapping.put(SolverExecutable.FiniteVolume, SolverFamily.FVSOLVER);
		SolverFamily.mapping.put(SolverExecutable.FiniteVolume_PETSc, SolverFamily.OTHER);
		SolverFamily.mapping.put(SolverExecutable.SundialsOde, SolverFamily.CVODES);
		SolverFamily.mapping.put(SolverExecutable.Gibson, SolverFamily.OTHER);
		SolverFamily.mapping.put(SolverExecutable.Hybrid_EM, SolverFamily.OTHER);
		SolverFamily.mapping.put(SolverExecutable.Hybrid_Mil, SolverFamily.OTHER);
		SolverFamily.mapping.put(SolverExecutable.Hybrid_Mil_Adaptive, SolverFamily.OTHER);
		SolverFamily.mapping.put(SolverExecutable.Smoldyn, SolverFamily.FVSOLVER);
		SolverFamily.mapping.put(SolverExecutable.NFSIM, SolverFamily.OTHER);
		SolverFamily.mapping.put(SolverExecutable.LANGEVIN, SolverFamily.LANGEVIN);
		SolverFamily.mapping.put(SolverExecutable.MOVING_B, SolverFamily.OTHER);
	}

	public static SolverFamily getSolverFamilyFromSolver(SolverExecutable solver) {
		if (solver == null) throw new IllegalArgumentException("solver cannot be null");
		if (!SolverFamily.mapping.containsKey(solver))
			throw new IllegalStateException(solver.name() + " is not mapped properly in class `SolverFamily`");
		return SolverFamily.mapping.get(solver);
	}

	private final String name;
	SolverFamily(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
}

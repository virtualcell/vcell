package cbit.vcell.opt;

import cbit.vcell.opt.CopasiOptSettings.CopasiOptProgressType;
import cbit.vcell.opt.CopasiOptimizationParameter.CopasiOptimizationParameterType;

public class CopasiOptimizationMethod implements java.io.Serializable {

	public enum CopasiOptimizationMethodType {
		EvolutionaryProgram("Evolutionary Programming", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Number_of_Generations, 200),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Population_Size, 20),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0)},
				true,
				CopasiOptSettings.Label_Progress,
				CopasiOptProgressType.Progress
		),
		SRES("Evolution Strategy (SRES)", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Number_of_Generations, 200),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Population_Size, 20),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Pf, 0.475)},
				true,
				CopasiOptSettings.Label_Progress,
				CopasiOptProgressType.Progress
		),
		GeneticAlgorithm("Genetic Algorithm", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Number_of_Generations, 200),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Population_Size, 20),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0)},
				true,
				CopasiOptSettings.Label_Progress,
				CopasiOptProgressType.Progress
		),
		GeneticAlgorithmSR("Genetic Algorithm SR", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Number_of_Generations, 200),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Population_Size, 20),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Pf, 0.475)},
				true,
				CopasiOptSettings.Label_Progress,
				CopasiOptProgressType.Progress
		),
		HookeJeeves("Hooke & Jeeves", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.IterationLimit, 50),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Tolerance, 1e-5),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Rho, 0.2)},
				false,
				null,
				CopasiOptProgressType.NO_Progress
		),
		LevenbergMarquardt("Levenberg - Marquardt", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.IterationLimit, 200),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Tolerance, 1e-5)},
				false,
				null,
				CopasiOptProgressType.NO_Progress
		),
		NelderMead("Nelder - Mead", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.IterationLimit, 200),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Tolerance, 1e-5),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Scale, 10)},
				false,
				null,
				CopasiOptProgressType.NO_Progress
		),
		ParticleSwarm("Particle Swarm", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.IterationLimit, 2000),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Swarm_Size, 50),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Std_Deviation, 1e-6),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0)},
				true,
				CopasiOptSettings.Label_Progress,
				CopasiOptProgressType.Progress),
	    RandomSearch("Random Search", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Number_of_Iterations, 100000),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0)},
				true,
				null,
				CopasiOptProgressType.NO_Progress
		),
	    SimulatedAnnealing("Simulated Annealing", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Start_Temperature, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Cooling_Factor, 0.85),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Tolerance, 1e-6),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0)},
				true,
				"Current Temperature",
				CopasiOptProgressType.Current_Value
		),
	    SteepestDescent("Steepest Descent", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.IterationLimit, 100),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Tolerance, 1e-6)},
				false,
				null,
				CopasiOptProgressType.NO_Progress
		),
	    Praxis("Praxis", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Tolerance, 1e-5)},
				false,
				null,
				CopasiOptProgressType.NO_Progress
		),
	    TruncatedNewton("Truncated Newton", new CopasiOptimizationParameter[0],
				false,
	    		null,
				CopasiOptProgressType.NO_Progress
		);
		
		private String name;
		private String displayName;
		private CopasiOptimizationParameter[] defaultParameters;
		private boolean bStochastic;
		private String progressLabel;
		private CopasiOptProgressType progressType;
		
		CopasiOptimizationMethodType(String name, CopasiOptimizationParameter[] parameters, boolean bStochastic, String progressLabel, CopasiOptProgressType progressType) {
			this.name = name;
			displayName = name;
			this.defaultParameters = parameters;
			this.bStochastic = bStochastic; 
			this.progressLabel = progressLabel;
			this.progressType = progressType;
		}
		public final String getName() {
			return name;
		}
		public final String getDisplayName() {
			return displayName;
		}
		public final CopasiOptimizationParameter[] getDefaultParameters() {
			return defaultParameters;
		}
		public String getProgressLabel() {
			return progressLabel;
		}
		public CopasiOptProgressType getProgressType() {
			return progressType;
		}
		public boolean isStochasticMethod(){
			return bStochastic;
		}
	}
	
	private CopasiOptimizationMethodType type;
	private CopasiOptimizationParameter[] realParameters;
	
	public CopasiOptimizationMethod(CopasiOptimizationMethodType type) {
		this.type = type;
		CopasiOptimizationParameter[] defaultParameters = type.getDefaultParameters();
		this.realParameters = new CopasiOptimizationParameter[defaultParameters.length];
		for (int i = 0; i < defaultParameters.length; i ++) {
			realParameters[i] = new CopasiOptimizationParameter(defaultParameters[i]);
		}
	}
	
	public CopasiOptimizationMethod(CopasiOptimizationMethodType type, CopasiOptimizationParameter[] solverParams)
	{
		this.type = type;
		realParameters = solverParams;
	}
	
	public final CopasiOptimizationMethodType getType() {
		return type;
	}
	public final CopasiOptimizationParameter[] getParameters() {
		return realParameters;
	}
	public final Double getEndValue() {
		for (CopasiOptimizationParameter cop : realParameters) {
			if (cop.getType() == CopasiOptimizationParameterType.Number_of_Generations
					|| cop.getType() == CopasiOptimizationParameterType.IterationLimit)
			return new Double(cop.getValue());
		}
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CopasiOptimizationMethod) {
			CopasiOptimizationMethod com = (CopasiOptimizationMethod)obj;
			if (com.type != type) {
				return false;
			}
			if (realParameters.length != com.realParameters.length) {
				return false;
			}
			for (int i = 0; i < realParameters.length; i ++) {
				if (!realParameters[i].equals(com.realParameters[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
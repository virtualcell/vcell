namespace java org.vcell.optimization.thrift
namespace py vcellopt

typedef i32 int
typedef i64 long
typedef string FilePath

struct ParameterDescription {
	1: required string name;
	2: required double scale;
	3: required double minValue;
	4: required double maxValue;
	5: required double initialValue;
}
typedef list<ParameterDescription> ParameterDescriptionList

enum ReferenceVariableType {
	independent,
	dependent
}

struct ReferenceVariable {
	1: required string varName;
	2: required ReferenceVariableType varType;
}
typedef list<ReferenceVariable> ReferenceVariableList

// ----------------------------------------------------

enum OptimizationParameterType {
	Number_of_Generations,
	Number_of_Iterations,
	Population_Size,
	Random_Number_Generator,
	Seed,
	IterationLimit,
	Tolerance,
	Rho,
	Scale,
	Swarm_Size,
	Std_Deviation,
	Start_Temperature,
	Cooling_Factor,
	Pf
}

struct CopasiOptimizationParameter {
	1: required OptimizationParameterType dataType;
	2: required double value;
}
typedef list<CopasiOptimizationParameter> CopasiOptimizationParameterList

enum OptimizationMethodType {
	EvolutionaryProgram,
	SRES,
	GeneticAlgorithm,
	GeneticAlgorithmSR,
	HookeJeeves,
	LevenbergMarquardt,
	NelderMead,
	ParticleSwarm,
    RandomSearch,
	SimulatedAnnealing,
	SteepestDescent,
	Praxis,
	TruncatedNewton
}


struct CopasiOptimizationMethod {
	1: required OptimizationMethodType optimizationMethodType;
	2: required CopasiOptimizationParameterList optimizationParameterList;
}

// -------------------------------------------------------

struct OptProblem {
	1: required FilePath mathModelSbmlFile;
	2: required int numberOfOptimizationRuns;
	3: required ParameterDescriptionList parameterDescriptionList;
	4: required ReferenceVariableList referenceVariableList;
	5: required FilePath experimentalDataFile;
	6: required CopasiOptimizationMethod optimizationMethod;
}

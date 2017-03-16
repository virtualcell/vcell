namespace java org.vcell.optimization.thrift
namespace py vcellopt

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

enum OptimizationParameterDataType {
	dataTypeInt,
	dataTypeFloat
}

struct CopasiOptimizationParameter {
	1: required string optimizationParameterName;
	2: required OptimizationParameterDataType dataType;
	3: required double value;
}
typedef list<CopasiOptimizationParameter> CopasiOptimizationParameterList

struct CopasiOptimizationMethod {
	1: required string optimizationMethodName;
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


/*
struct ExperimentalDataRow {
	1: required list<double> aData;
}
struct ReferenceData {
	1: required list<ReferenceVariable> referenceVariableList;
	2: required list<ExperimentalDataRow> dataRowList;
}
*/

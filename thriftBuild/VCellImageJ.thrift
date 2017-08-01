namespace java org.vcell.vcellij.api
namespace py vcellij

typedef i32 int
typedef i64 long

enum DomainType {
   VOLUME
   MEMBRANE
}

typedef int TimeIndex
typedef double TimePoint
typedef double Datum 
typedef string DomainName
typedef string FilePath
typedef string SimDataID
typedef string VCML
typedef string SBML
typedef string ApplicationName

typedef list<TimePoint> TimePoints
typedef list<Datum> Data

struct SimulationInfo {
    1: required int id;
}

struct SBMLModel {
    1: required FilePath filepath;
}

struct VariableInfo{
   1: required string variableVtuName;
   2: required string variableDisplayName;
   3: required DomainName domainName;
   4: required DomainType variableDomainType;
}

struct SimulationSpec {
	1: required double outputTimeStep;
	2: required double totalTime;
}

typedef list<VariableInfo> VariableList

exception ThriftDataAccessException {
   1: required string message;
}

enum SimulationState {
	notRun,
	running,
	done,
	failed
}

struct SimulationStatus {
	1: required SimulationState simState;
}

//struct ModelInfo {
//    1: required int id;
//}

service SimulationService {

//    ModelInfo openInVCell(
//        1: SBMLModel sbmlModel)
//        throws (1:ThriftDataAccessException dataAccessException);

	SBML getSBML(
		1: VCML vcml,
		2: ApplicationName applicationName)
		throws (1:ThriftDataAccessException dataAccessException)

    Data getData(
    	1:SimulationInfo simInfo, 
    	2:VariableInfo varInfo, 
    	3:TimeIndex timeIndex) 
    	throws (1:ThriftDataAccessException dataAccessException)
    
    TimePoints getTimePoints(
    	1: SimulationInfo simInfo) 
    	throws (1:ThriftDataAccessException dataAccessException);    
    
    VariableList getVariableList(
    	1: SimulationInfo simInfo) 
    	throws (1: ThriftDataAccessException dataAccessException);
        
    SimulationStatus getStatus(
    	1: SimulationInfo simInfo)
    	throws (1:ThriftDataAccessException dataAccessException);

    SimulationInfo computeModel(
        1: SBMLModel model,
        2: SimulationSpec simSpec)
        throws (1:ThriftDataAccessException dataAccessException);
}



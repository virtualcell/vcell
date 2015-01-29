namespace java cbit.vcell.client.pyvcellproxy
namespace py pyvcell

typedef i32 int
typedef i64 long

enum DomainType {
   VOLUME
   MEMBRANE
}

typedef int TimeIndex
typedef double TimePoint 
typedef string DomainName
typedef string FilePath

typedef list<TimePoint> TimePoints

struct VariableInfo{
   1: required string variableVtuName;
   2: required string variableDisplayName;
   3: required DomainName domainName;
   4: required DomainType variableDomainType;
   5: required string unitsLabel;
}

typedef list<VariableInfo> VariableList

struct SimulationDataSetRef {
   1: required string simId;
   2: required string simName;
   4: required string modelId;
}

typedef list<SimulationDataSetRef> SimulationDataSetRefList

exception DataAccessException {
   1: required string message;
}

service VCellProxy {

    FilePath getDataSetFileOfDomainAtTimeIndex(1:SimulationDataSetRef simulationDataSetRef, 2:DomainName domainName, 3:TimeIndex timeIndex) throws (1:DataAccessException dataAccessException)
    
    SimulationDataSetRefList getSimsFromOpenModels() throws (1: DataAccessException dataAccessException);
    
    TimePoints getTimePoints(1: SimulationDataSetRef simulationDataSetRef) throws (1:DataAccessException dataAccessException);    
    
    VariableList getVariableList(1: SimulationDataSetRef simulationDataSetRef) throws (1: DataAccessException dataAccessException);
}

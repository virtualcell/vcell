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
typedef double Datum 
typedef string DomainName
typedef string FilePath

typedef list<TimePoint> TimePoints
typedef list<Datum> Data

struct VariableInfo{
   1: required string variableVtuName;
   2: required string variableDisplayName;
   3: required DomainName domainName;
   4: required DomainType variableDomainType;
   5: required string unitsLabel;
   6: required bool isMeshVar;
   7: optional string expressionString;
}

typedef list<VariableInfo> VariableList

struct SimulationDataSetRef {
   1: required string simId;
   2: required string simName;
   3: required string modelId;
   4: required string username;
   5: required string userkey;
   6: required int jobIndex;
   7: required bool isMathModel;
   8: optional string simulationContextName;
   9: required string modelName;
  10: required list<double> originXYZ;
  11: required list<double> extentXYZ;
}
typedef list<SimulationDataSetRef> SimulationDataSetRefList

struct PlotData {
	1: required TimePoints timePoints;
	2: required Data data;
}

struct PostProcessingData {
	1: required VariableList variableList;
	2: required list<PlotData> plotData ;
}

exception ThriftDataAccessException {
   1: required string message;
}

service VCellProxy {

    FilePath getDataSetFileOfVariableAtTimeIndex(1:SimulationDataSetRef simulationDataSetRef, 2:VariableInfo varInfo, 3:TimeIndex timeIndex) throws (1:ThriftDataAccessException dataAccessException)
    
    SimulationDataSetRefList getSimsFromOpenModels() throws (1: ThriftDataAccessException dataAccessException);
    
    TimePoints getTimePoints(1: SimulationDataSetRef simulationDataSetRef) throws (1:ThriftDataAccessException dataAccessException);    
    
    VariableList getVariableList(1: SimulationDataSetRef simulationDataSetRef) throws (1: ThriftDataAccessException dataAccessException);
    
    PostProcessingData getPostProcessingData(1: SimulationDataSetRef simulationDataSetRef) throws (1: ThriftDataAccessException dataAccessException);
    
    void displayPostProcessingDataInVCell(1: SimulationDataSetRef simulationDataSetRef) throws (1: ThriftDataAccessException dataAccessException);
}

namespace java org.vcell.vcellij.api
namespace py vcellij

typedef i32 int
typedef i64 long

typedef string FilePath


struct SimulationInfo {
    1: required int id;
}

struct SBMLModel {
    1: required FilePath filepath;
}

struct Dataset {
    1: required FilePath filepath;
}

exception ThriftDataAccessException {
   1: required string message;
}

struct SimulationSpec {

}

enum SimulationState {
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


    Dataset getDataset(
        1: SimulationInfo simInfo)
        throws (1:ThriftDataAccessException dataAccessException);
        
    SimulationStatus getStatus(
    	1: SimulationInfo simInfo)
    	throws (1:ThriftDataAccessException dataAccessException);

    SimulationInfo computeModel(
        1: SBMLModel model,
        2: SimulationSpec simSpec)
        throws (1:ThriftDataAccessException dataAccessException);
}



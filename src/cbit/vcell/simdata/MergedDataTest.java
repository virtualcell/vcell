package cbit.vcell.simdata;

import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.ode.ODESolverResultSetTest;
import cbit.vcell.server.StdoutSessionLog;
import cbit.vcell.server.VCDataIdentifier;

import java.io.File;
import cbit.vcell.server.User;
/**
 * Insert the type's description here.
 * Creation date: (10/10/2003 11:48:54 AM)
 * @author: Anuradha Lakshminarayana
 */
public class MergedDataTest {
/**
 * Insert the method's description here.
 * Creation date: (10/10/2003 11:49:18 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {

	final User user = new cbit.vcell.server.User("anu",new cbit.sql.KeyValue("2302355"));
	File userFile = new File("\\\\fs2\\RAID\\vcell\\users");
	cbit.vcell.server.VCDataIdentifier vcData1 = new cbit.vcell.server.VCDataIdentifier() {
		public String getID() {
			return "SimID_6389673";
		}
		public cbit.vcell.server.User getOwner() {
			return user;
		}
	};
	cbit.vcell.server.VCDataIdentifier vcData2 = new cbit.vcell.server.VCDataIdentifier() {
		public String getID() {
			return "SimID_6383968";
		}
		public cbit.vcell.server.User getOwner() {
			return user;
		}
	};
	SimulationData simData1 = null;
	SimulationData simData2 = null;
	// SimulationData simData2 = new SimulationData(user, userFile, "SimID_6384031");

	cbit.vcell.server.SessionLog sessionLog = new StdoutSessionLog(cbit.vcell.server.PropertyLoader.ADMINISTRATOR_ACCOUNT);
	Cachetable dataCachetable = new Cachetable(10*Cachetable.minute);
	DataSetControllerImpl dscImpl = null;
	try {
		simData1 = new SimulationData(vcData1, userFile, null);
		simData2 = new SimulationData(vcData2, userFile, null);
		dscImpl = new DataSetControllerImpl(sessionLog,dataCachetable,userFile, null);
	} catch (java.io.IOException e) {
		e.printStackTrace(System.out);
	} catch (cbit.vcell.server.DataAccessException e) {
		e.printStackTrace(System.out);
	}

//	MergedData MergedData = new MergedData("MergedData1", dscImpl, new SimulationData[] {simData1, simData2});
	try {
		VCDataIdentifier[] vcIdentifierArray = new cbit.vcell.server.VCDataIdentifier[] {simData1.getResultsInfoObject(), simData2.getResultsInfoObject()};
		MergedDataInfo mergedInfo = new MergedDataInfo(user,vcIdentifierArray ,MergedDataInfo.createDefaultPrefixNames(vcIdentifierArray.length));
		MergedData mergedData = (MergedData)dscImpl.getVCData(mergedInfo);
		ODEDataBlock combinedODEDataBlk = mergedData.getODEDataBlock();
		ODESimData combinedODESimData = combinedODEDataBlk.getODESimData();
		ODESolverResultSetTest.plot(combinedODESimData);
	} catch (cbit.vcell.server.DataAccessException e1) {
		e1.printStackTrace(System.out);
	} catch (java.io.IOException e2) {
		e2.printStackTrace(System.out);
	} catch (cbit.vcell.parser.ExpressionException e3) {
		e3.printStackTrace(System.out);
	} 
}
}

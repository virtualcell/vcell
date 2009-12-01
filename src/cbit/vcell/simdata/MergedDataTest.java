package cbit.vcell.simdata;

import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.ode.ODESolverResultSetTest;

import java.io.File;

import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
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

	final User user = new org.vcell.util.document.User("anu",new org.vcell.util.document.KeyValue("2302355"));
	File userFile = new File("\\\\fs2\\RAID\\vcell\\users");
	org.vcell.util.document.VCDataIdentifier vcData1 = new org.vcell.util.document.VCDataIdentifier() {
		public String getID() {
			return "SimID_6389673";
		}
		public org.vcell.util.document.User getOwner() {
			return user;
		}
	};
	org.vcell.util.document.VCDataIdentifier vcData2 = new org.vcell.util.document.VCDataIdentifier() {
		public String getID() {
			return "SimID_6383968";
		}
		public org.vcell.util.document.User getOwner() {
			return user;
		}
	};
	SimulationData simData1 = null;
	SimulationData simData2 = null;
	// SimulationData simData2 = new SimulationData(user, userFile, "SimID_6384031");

	org.vcell.util.SessionLog sessionLog = new StdoutSessionLog(org.vcell.util.PropertyLoader.ADMINISTRATOR_ACCOUNT);
	Cachetable dataCachetable = new Cachetable(10*Cachetable.minute);
	DataSetControllerImpl dscImpl = null;
	try {
		simData1 = new SimulationData(vcData1, userFile, null);
		simData2 = new SimulationData(vcData2, userFile, null);
		dscImpl = new DataSetControllerImpl(sessionLog,dataCachetable,userFile, null);
	} catch (java.io.IOException e) {
		e.printStackTrace(System.out);
	} catch (org.vcell.util.DataAccessException e) {
		e.printStackTrace(System.out);
	}

//	MergedData MergedData = new MergedData("MergedData1", dscImpl, new SimulationData[] {simData1, simData2});
	try {
		VCDataIdentifier[] vcIdentifierArray = new org.vcell.util.document.VCDataIdentifier[] {simData1.getResultsInfoObject(), simData2.getResultsInfoObject()};
		MergedDataInfo mergedInfo = new MergedDataInfo(user,vcIdentifierArray ,MergedDataInfo.createDefaultPrefixNames(vcIdentifierArray.length));
		MergedData mergedData = (MergedData)dscImpl.getVCData(mergedInfo);
		ODEDataBlock combinedODEDataBlk = mergedData.getODEDataBlock();
		ODESimData combinedODESimData = combinedODEDataBlk.getODESimData();
		ODESolverResultSetTest.plot(combinedODESimData);
	} catch (org.vcell.util.DataAccessException e1) {
		e1.printStackTrace(System.out);
	} catch (java.io.IOException e2) {
		e2.printStackTrace(System.out);
	} catch (cbit.vcell.parser.ExpressionException e3) {
		e3.printStackTrace(System.out);
	} 
}
}

package cbit.vcell.simdata;

import org.vcell.util.VCDataIdentifier;

/**
 * Insert the type's description here.
 * Creation date: (6/23/2004 1:39:55 PM)
 * @author: Jim Schaff
 */
public class SimulationDataTest {
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 11:13:56 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	final org.vcell.util.document.User user = new org.vcell.util.document.User("fgao15",new org.vcell.util.document.KeyValue("4123431"));	
	VCDataIdentifier vcData1 = new VCDataIdentifier() {
		public String getID() {
			return "SimID_8483780";
		}
		public org.vcell.util.document.User getOwner() {
			return user;
		}
	};
	cbit.vcell.simdata.SimulationData simData1 = null;
	java.io.File userFile = new java.io.File("D:\\VSS\\Project\\VCell\\VCell\\", vcData1.getOwner().getName());
	int runs = 0;
	while (true) {
		try {
			simData1 = new cbit.vcell.simdata.SimulationData(vcData1, userFile);
			double a[] = simData1.getDataTimes();
			if (a == null) {
				continue;
			}
			for (int i = a.length - 1 ; i >= 0; i --) {
				try {
					SimDataBlock sdb = simData1.getSimDataBlock("Ca", a[i]);
					System.out.println(runs + "---Timepoint@" + a[i]  + "--- Timestamp: " + new java.util.Date(sdb.getPDEDataInfo().getTimeStamp()));
				} catch (java.io.IOException e) {
					System.out.println(runs + "---Timepoint@" + a[i]  + "--- ******IOException*******:");
					e.printStackTrace();
				} catch (org.vcell.util.DataAccessException e) {
					System.out.println(runs + "---Timepoint@" + a[i]  + "--- ******DataAccessException*******:");
					e.printStackTrace();
				}
			}
		} catch (java.io.IOException e) {
			e.printStackTrace(System.out);
		} catch (org.vcell.util.DataAccessException e) {
			e.printStackTrace(System.out);
		} 
		runs ++;
		if (runs == 2000) {
			break;
		}
	}
}
}
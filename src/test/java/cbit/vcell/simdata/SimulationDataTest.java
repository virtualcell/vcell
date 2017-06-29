/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;

import org.junit.Test;
import org.vcell.util.document.KeyValue;

import cbit.vcell.util.AmplistorUtils;

/**
 * Insert the type's description here.
 * Creation date: (6/23/2004 1:39:55 PM)
 * @author: Jim Schaff
 */
public class SimulationDataTest {
	@SuppressWarnings("unused")
	private static int jIndexes[] = {0,2,4,7,16,32,64};
	
	/*
	@Test
	public void nameTest( ) {
		KeyValue kv = new KeyValue("8675904");
		for (int j : jIndexes) {
			for (int i = 1; i < 10000; i++) {
				String old = SimulationData.createCanonicalSmoldynOutputFileNameOld(kv, j, i);
				String now = SimulationData.createCanonicalSmoldynOutputFileName(kv, j, i);
				if (!old.equals(now)) {
					System.err.println(old + " and " + now);
				}
				assertTrue(old.equals(now));
			}
		}
	}
	*/
	
	@Test(expected = IllegalArgumentException.class)
	public void noZero(  ) {
		KeyValue kv = new KeyValue("8675904");
		String now = SimulationData.createCanonicalSmoldynOutputFileName(kv, 0, 0);
		System.out.println("never see " + now);
	}
	
	
	
	
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 11:13:56 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	final org.vcell.util.document.User user = new org.vcell.util.document.User("fgao15",new org.vcell.util.document.KeyValue("4123431"));	
	org.vcell.util.document.VCDataIdentifier vcData1 = new org.vcell.util.document.VCDataIdentifier() {
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
			SimulationData.SimDataAmplistorInfo simDataAmplistorInfo = AmplistorUtils.getSimDataAmplistorInfoFromPropertyLoader();
			simData1 = new SimulationData(vcData1, userFile, null, simDataAmplistorInfo);
			double a[] = simData1.getDataTimes();
			if (a == null) {
				continue;
			}
			for (int i = a.length - 1 ; i >= 0; i --) {
				try {
					SimDataBlock sdb = simData1.getSimDataBlock(null,"Ca", a[i]);
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

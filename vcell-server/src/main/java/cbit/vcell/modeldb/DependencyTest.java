/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modeldb;

import org.vcell.util.document.VersionableFamily;
import org.vcell.util.document.VersionableRelationship;
import org.vcell.util.document.VersionableType;
import org.vcell.util.document.VersionableTypeVersion;
/**
 * This type was created in VisualAge.
 */
public class DependencyTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	if (args.length != 2) {
		throw new RuntimeException("Usage: DependencyTest VersionableType.toString() keyValue.toString()");
	}
	java.sql.Connection con = null;
	try {
		String versionableTypeS = args[0];
		String keyValueS = args[1];
		org.vcell.util.document.KeyValue rootKey = new org.vcell.util.document.KeyValue(keyValueS); //new cbit.sql.KeyValue("1368");
		org.vcell.util.document.VersionableType rootType = null; //cbit.sql.VersionableType.VCImage;
		if (VersionableType.VCImage.toString().equals(versionableTypeS)) {
			rootType = VersionableType.VCImage;
		} else
			if (VersionableType.Geometry.toString().equals(versionableTypeS)) {
				rootType = VersionableType.Geometry;
			} else
				if (VersionableType.Model.toString().equals(versionableTypeS)) {
					rootType = VersionableType.Model;
				} else
					if (VersionableType.MathDescription.toString().equals(versionableTypeS)) {
						rootType = VersionableType.MathDescription;
					} else
						if (VersionableType.SimulationContext.toString().equals(versionableTypeS)) {
							rootType = VersionableType.SimulationContext;
						} else {
							throw new RuntimeException("Improper argument for VersionableType " + versionableTypeS);
						}
		Class.forName("oracle.jdbc.driver.OracleDriver");
		String url = "jdbc:oracle:thin:@nrcamdb.uchc.edu:1521:orc0";
		con = java.sql.DriverManager.getConnection(url, "nrcamdbdev", "bogus");
		System.out.println("Search for References to " + rootType.toString() + " id=" + rootKey.toString());
		VersionableFamily vf = cbit.vcell.modeldb.DbDriver.getAllReferences(con, rootType, rootKey);
		VersionableRelationship[] dependants = vf.getDependantRelationships();
		for (int c = 0; c < dependants.length; c += 1) {
			VersionableRelationship verrel = dependants[c];
			VersionableTypeVersion from = verrel.from();
			VersionableTypeVersion to = verrel.to();
			System.out.println(from.getVType() + " " + from.getVersion().getVersionKey() + "   ==>   " + to.getVType() + " " + to.getVersion().getVersionKey());
		}
		System.out.println("------------------Top Down List-------------------");
		VersionableTypeVersion[] topDown = vf.getDependantsTopDown();
		byte[] spaces = {32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32};
		for (int c = 0; c < topDown.length; c += 1) {
			System.out.print(topDown[c].getVType());
			System.out.write(spaces,0,30 - topDown[c].getVType().toString().length());
			System.out.print(topDown[c].getVersion().getName());
			System.out.write(spaces,0,40 - topDown[c].getVersion().getName().length());			
			System.out.println(topDown[c].getVersion().getVersionKey());
		}
		
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	} finally {
		try {
			if (con != null) {
				con.close();
			}
		} catch (java.sql.SQLException e) {
		}
	}
}
}

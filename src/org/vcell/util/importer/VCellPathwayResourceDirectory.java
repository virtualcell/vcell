/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VCellPathwayResourceDirectory {
	
	public static final String DIR = "/sbpax3/examples/";
	
	protected static final List<String> pathList = new ArrayList<String>();
	protected static final List<String> descriptionList = new ArrayList<String>();
	protected static final Map<String, String> path2description = new HashMap<String, String>();
	protected static final Map<String, String> description2path = new HashMap<String, String>();
	
	public static void addEntry(String path, String description) {
		pathList.add(path);
		descriptionList.add(description);
		path2description.put(path, description);
		description2path.put(description, path);
	}
	
	static {
		String fromSGMP = "from Signaling Gateway Molecule Pages";
		addEntry(DIR + "sgmp/sgmpA001750modified.owl", "A001750 (modified) " + fromSGMP);
		addEntry(DIR + "sgmp/sgmpA000037.owl", "A000037 " + fromSGMP);
		addEntry(DIR + "sgmp/sgmpA001046.owl", "A001046 " + fromSGMP);
		addEntry(DIR + "sgmp/sgmpA001750.owl", "A001750 " + fromSGMP);
		addEntry(DIR + "sgmp/sgmpA001778.owl", "A001778 " + fromSGMP);
		addEntry(DIR + "sgmp/sgmpA001852.owl", "A001852 " + fromSGMP);
		addEntry(DIR + "sabio/biopax_test_R00659_modified.xml", "Sample file from SABIO-RK, modified");
		addEntry(DIR + "sabio/biopax_test_R00659.xml", "Sample file from SABIO-RK");
		addEntry(DIR + "sgmp/example1receptorLigand.owl", "Receptor-Ligand binding " + fromSGMP);
		addEntry(DIR + "sgmp/example2aMichaelisMenten.owl", "Michaelis-Menten kinetics " + fromSGMP);
		addEntry(DIR + "sgmp/example2bPositiveCooperativity.owl", "Positive cooperativity " + fromSGMP);
		addEntry(DIR + "sgmp/example3selectiveChannel.owl", "Selective ion channel " + fromSGMP);
		addEntry(DIR + "sgmp/example4transport.owl", "Transport " + fromSGMP);
		addEntry(DIR + "metacyc/deoxyfutalosine.owl", "Deoxyfutalosine from MetcCyc");
	}
	
	public static List<String> getPathList() { return pathList; }
	public static List<String> getDescriptionList() { return descriptionList; }
	public static String getDescription(String path) { return path2description.get(path); }
	public static String getPath(String description) { return description2path.get(description); }
	
}

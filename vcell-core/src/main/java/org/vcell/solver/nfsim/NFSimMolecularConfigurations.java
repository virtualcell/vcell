/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.solver.nfsim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.vcell.util.Pair;

public class NFSimMolecularConfigurations implements Serializable
{

	// database representation:
	// Double timepoint, String simId, String expression, Integer count
	
	private String simId = "0";
	private Double timePoint = (double) 0;
	private Map<String, Integer> molecularConfigurations;
	
	public static Map<String, Integer> getSampleTimepoint() {
		
		Map<String, Integer> timePointMap = new LinkedHashMap<>();

		timePointMap.put("C(AAA~cyt,AAB~0,site,Y1~u,Y2~u,Y3~u)", 464);
		timePointMap.put("C(AAA~nuc,AAB~0,site,Y1~u,Y2~u,Y3~u)", 521);
		timePointMap.put("Ran(AAA~cyt,AAB~0,cargo!1).C(AAA~cyt,AAB~0,site!1,Y1~u,Y2~u,Y3~u)", 9);
		timePointMap.put("Ran(AAA~cyt,AAB~0,cargo)", 464);
		timePointMap.put("Ran(AAA~nuc,AAB~0,cargo!1).C(AAA~nuc,AAB~0,site!1,Y1~u,Y2~u,Y3~u)", 6);
		timePointMap.put("Ran(AAA~nuc,AAB~0,cargo)", 521);
		
		return timePointMap;
	}
	
	public void setMolecularConfigurations(Map<String, Integer> molecularConfigurations) {
		this.molecularConfigurations = molecularConfigurations;
	}
	public Map<String, Integer> getMolecularConfigurations() {
		return molecularConfigurations;
	}
	
	public static Map<String, Integer> getTimePointMap(File file) {
		Map<String, Integer> timePointMap = new LinkedHashMap<>();
		String line = null;
		String ls = System.getProperty("line.separator");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader (file));
			while((line = reader.readLine()) != null) {
				if(line.startsWith("#")) {
					continue;
				}
				StringTokenizer lineTokenizer = new StringTokenizer(line," \t");
				while(lineTokenizer.hasMoreTokens()) {
					String expression = lineTokenizer.nextToken();
					Integer count = Integer.parseInt(lineTokenizer.nextToken());
					MolecularConfigurationEntry mce = new MolecularConfigurationEntry(expression, count);
					timePointMap.put(mce.expression, mce.count);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return timePointMap;
	}

}

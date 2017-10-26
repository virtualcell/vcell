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

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.vcell.util.Pair;

public class NFSimMolecularConfigurations implements Serializable
{
	
	public class MolecularConfigurationEntry
	{
		protected String expression;
		protected Integer count;
		
		public MolecularConfigurationEntry(String expression, Integer count) {
			this.expression = expression;
			this.count = count;
		}
		
	}
	
	// --------------------------------------------------------------------------

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
	
	public Pair<Integer, Map<String, Integer>> getTimepoint(Double timePoint, String simId) {
		Map<String, Integer> timePointMap = new LinkedHashMap<>();
		
		
		// TODO: do stuff
		
		Integer numberOfSimulations = 1;
		Pair<Integer, Map<String, Integer>> pair = new Pair<>(numberOfSimulations, timePointMap);
		return pair;
	}
	
	
	public void addTimepoint(Double timePoint, File timePointFile) {
		// extract simId from file name
		// transform file into string
		String simId = "";
		String timePointString = "";
		addTimepoint(timePoint, simId, timePointString);
	}
	private void addTimepoint(Double timePoint, String simId, String timePointString) {
		// TODO: parse here and write to database
	}

	public Map<String, Integer> getMolecularConfigurations() {
		return molecularConfigurations;
	}


}

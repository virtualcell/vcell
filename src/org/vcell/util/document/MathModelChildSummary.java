/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.document;

import java.util.StringTokenizer;
import java.util.Vector;

import org.vcell.util.TokenMangler;
import org.vcell.util.document.BioModelChildSummary.MathType;
/**
 * Insert the type's description here.
 * Creation date: (8/20/2004 2:11:48 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class MathModelChildSummary implements java.io.Serializable {
	private String geoName = null;
	private int geoDim = 0;
	
	private String simNames[] = null;
	private String simAnnots[] = null;
	//math model type deterministic or stochastic
	private MathType modelType = null;
/**
 * Insert the method's description here.
 * Creation date: (8/23/2004 10:13:07 PM)
 */
private MathModelChildSummary() {}

public MathModelChildSummary(MathType arg_modelType, String arg_geoName, int arg_geoDim, String[] arg_simNames, String[] arg_simAnnots){
	this.modelType = arg_modelType;
	this.geoName = arg_geoName;
	this.geoDim = arg_geoDim;
	this.simNames = arg_simNames;
	this.simAnnots = arg_simAnnots;
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2004 3:01:10 PM)
 * @return java.lang.String
 * @param str java.lang.String
 */
private String emptyConvention(String str) {
	
	if(str != null && str.length() > 0){
		return str;
	}
	return " ";
}


/**
 * Insert the method's description here.
 * Creation date: (8/23/2004 1:30:48 PM)
 * @return cbit.vcell.biomodel.BioModelChildSummary
 * @param databaseSerialization java.lang.String
 */
public static MathModelChildSummary fromDatabaseSerialization(String databaseSerialization) {
	
	MathModelChildSummary mmcs = new MathModelChildSummary();
//	if(BioModelChildSummary.debug) return mmcs;
	StringTokenizer st = null;
	try{
		//Assumes there is a non-empty string for every element
		st = new java.util.StringTokenizer(databaseSerialization,"\n",false);
		//check if tere is new field(model type), if so, read it and next stringTokenizer will be geometry name.
		//otherwise this tokenStr is geometry name.
		String tokenStr = (String)st.nextElement();
		if(tokenStr.startsWith(BioModelChildSummary.TYPE_TOKEN))
		{
			mmcs.modelType = MathType.valueOf(TokenMangler.getChildSummaryElementRestoredString(tokenStr.substring(BioModelChildSummary.TYPE_TOKEN.length())));
			mmcs.geoName = TokenMangler.getChildSummaryElementRestoredString((String)st.nextElement());
		}
		else
		{
			mmcs.modelType = BioModelChildSummary.MathType.Unknown;
			mmcs.geoName = TokenMangler.getChildSummaryElementRestoredString(tokenStr);
		}
//		mmcs.modelType = (String)TokenMangler.getChildSummaryElementRestoredString((String)st.nextElement());
//		mmcs.geoName = (String)TokenMangler.getChildSummaryElementRestoredString((String)st.nextElement());
		
		// can be 2D or 2 (without D)
		String nextline = (String)st.nextElement();
		nextline = nextline.replace(BioModelChildSummary.GEOMETRY_DIMENSION_SUFFIX, "");
		mmcs.geoDim = Integer.parseInt(nextline);
		
		Vector<String> simNamesV = new Vector<String>();
		Vector<String> simAnnotsV = new Vector<String>();
		int numSims = Integer.parseInt((String)st.nextElement());
		while(st.hasMoreElements()){
			for(int j=0;j<numSims;j+= 1){
				simNamesV.add(TokenMangler.getChildSummaryElementRestoredString((String)st.nextElement()));
				simAnnotsV.add(TokenMangler.getChildSummaryElementRestoredString((String)st.nextElement()));
			}
		}
	
		mmcs.simNames = (String[])simNamesV.toArray(new String[simNamesV.size()]);
		mmcs.simAnnots = (String[])simAnnotsV.toArray(new String[simAnnotsV.size()]);
	}catch(Exception e)
	{
		System.out.println("Failed reading MathModelChildSummary info..." + st.toString());
		e.printStackTrace(System.out);
	}
	return mmcs;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2004 9:52:25 PM)
 * @return cbit.vcell.biomodel.BioModelChildSummary
 */
public static MathModelChildSummary getExample() {
	MathModelChildSummary mmChildSummary = new MathModelChildSummary();
	mmChildSummary.geoName = "geo1";
	mmChildSummary.geoDim = 0;
	mmChildSummary.simNames = new String[]{ "sim11",	"sim12",	"sim13" };
	mmChildSummary.simAnnots = new String[]{ null,		null,		null };
	return mmChildSummary;
}
/**
 * Insert the method's description here.
 * Creation date: (8/20/2004 2:23:12 PM)
 * @return int[]
 */
public int getGeometryDimension() {
	return geoDim;
}
/**
 * Insert the method's description here.
 * Creation date: (8/20/2004 2:23:12 PM)
 * @return int[]
 */
public String getGeometryName() {
	return geoName;
}

public MathType getModelType()
{
	return modelType;
}

/**
 * Insert the method's description here.
 * Creation date: (8/20/2004 2:18:34 PM)
 * @return java.lang.String[]
 */
public String[] getSimulationAnnotations() {
	return simAnnots;
}
/**
 * Insert the method's description here.
 * Creation date: (8/20/2004 2:18:34 PM)
 * @return java.lang.String[]
 */
public String[] getSimulationNames() {
	return simNames;
}
/**
 * Insert the method's description here.
 * Creation date: (8/20/2004 2:17:14 PM)
 * @return java.lang.String
 */
public String toDatabaseSerialization() {
	
	StringBuffer sb = new StringBuffer();
	//add modeltype 
	sb.append(emptyConvention(TokenMangler.getChildSummaryElementEscapedString(BioModelChildSummary.TYPE_TOKEN+modelType))+"\n");
	sb.append(emptyConvention(TokenMangler.getChildSummaryElementEscapedString(geoName))+"\n");

	sb.append(geoDim+BioModelChildSummary.GEOMETRY_DIMENSION_SUFFIX+"\n");
	
	//Simulations
	int numSims = (simNames!=null)?(simNames.length):(0);
	sb.append(numSims+"\n");//num simulations
	for(int j=0;j<numSims;j+= 1){
		sb.append(emptyConvention(TokenMangler.getChildSummaryElementEscapedString(simNames[j]))+"\n");
		sb.append(emptyConvention(TokenMangler.getChildSummaryElementEscapedString(simAnnots[j]))+"\n");
	}
	
	return sb.toString();
}
}

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.Vector;

import org.vcell.util.TokenMangler;
/**
 * Insert the type's description here.
 * Creation date: (8/20/2004 2:11:48 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class BioModelChildSummary implements java.io.Serializable {
//	public static final boolean debug = true;
	private String scNames[] = new String[0];
	private String scAnnots[] = new String[0];
	private String geoNames[] = new String[0];
	private int geoDims[] = new int[0];
	private MathType appTypes[] = new MathType[0]; //deterministic application or stochastic application

	private String simNames[][] = new String[0][];
	private String simAnnots[][] = new String[0][];

	private final static String NOCHILDREN = "NOCHILDREN";
	public final static String COMPARTMENTAL_GEO_STR = "Compartmental";
	public final static String TYPE_TOKEN = "__TYPE__:";
	
	public enum MathType {
		RuleBased("RuleBased"),
		Stochastic("Stochastic"),
		Deterministic("Deterministic"),
		Unknown("Unknown");
		
		private String description = null;
		private MathType(String description){
			this.description = description;
		}
		public String getDescription() {
			return description;
		}
	}
	public final static String GEOMETRY_DIMENSION_SUFFIX = "D";
/**
 * Insert the method's description here.
 * Creation date: (8/23/2004 9:58:44 PM)
 */
private BioModelChildSummary() {}

public BioModelChildSummary(String[] arg_scNames, MathType[] arg_appType, String[] arg_scAnnots, String[][] arg_simNames, String[][] arg_simAnnots, String[] arg_geoNames, int[] arg_geoDims){
	this.scNames = arg_scNames;
	this.appTypes = arg_appType;
	this.scAnnots = arg_scAnnots;
	this.geoNames = arg_geoNames;
	this.geoDims = arg_geoDims;
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
public static BioModelChildSummary fromDatabaseSerialization(String databaseSerialization) {

	BioModelChildSummary bmcs = new BioModelChildSummary();
//	if(debug) return bmcs;
	if (databaseSerialization.equals(NOCHILDREN)){
		return bmcs;
	}
	StringTokenizer st = null;
	try{
		//Assumes there is a non-empty string for every element
		st = new java.util.StringTokenizer(databaseSerialization,"\n",false);
		Vector<String> scNamesV = new Vector<String>();
		Vector<MathType> appTypesV = new Vector<MathType>();
		Vector<String> scAnnotsV = new Vector<String>();
		Vector<String> geoNamesV = new Vector<String>();
		int[] geoDimsArr = new int[0];
		
		Vector<String[]> simNamesV = new Vector<String[]>();
		Vector<String[]> simAnnotsV = new Vector<String[]>();
	
		while(st.hasMoreElements()){
			scNamesV.add(TokenMangler.getChildSummaryElementRestoredString((String)st.nextElement()));
			//check if tere is new field(app type), if so, read it and next stringTokenizer will be annotation.
			//otherwise this tokenStr is annotation.
			String tokenStr = (String)st.nextElement();
			if(tokenStr.startsWith(TYPE_TOKEN))
			{
				appTypesV.add(MathType.valueOf(TokenMangler.getChildSummaryElementRestoredString(tokenStr.substring(TYPE_TOKEN.length()))));
				scAnnotsV.add(TokenMangler.getChildSummaryElementRestoredString((String)st.nextElement()));
			}
			else
			{
				appTypesV.add(MathType.Unknown);
				scAnnotsV.add(TokenMangler.getChildSummaryElementRestoredString(tokenStr));
			}
//			scAnnotsV.add(TokenMangler.getChildSummaryElementRestoredString((String)st.nextElement()));
			geoNamesV.add(TokenMangler.getChildSummaryElementRestoredString((String)st.nextElement()));
			int[] temp = new int[geoDimsArr.length + 1];
			System.arraycopy(geoDimsArr,0,temp,0,geoDimsArr.length);
			// can be 2D or 2 (without D)
			String nextline = (String)st.nextElement();
			nextline = nextline.replace(GEOMETRY_DIMENSION_SUFFIX, "");
			temp[temp.length-1] = Integer.parseInt(nextline);
			geoDimsArr = temp;
			
			int numSims = Integer.parseInt((String)st.nextElement());
			Vector<String> currentSimNamesV = new Vector<String>();
			Vector<String> currentSimAnnotsV= new Vector<String>();
			for(int j=0;j<numSims;j+= 1){
				currentSimNamesV.add(TokenMangler.getChildSummaryElementRestoredString((String)st.nextElement()));
				currentSimAnnotsV.add(TokenMangler.getChildSummaryElementRestoredString((String)st.nextElement()));
			}
			simNamesV.add((String[])currentSimNamesV.toArray(new String[currentSimNamesV.size()]));
			simAnnotsV.add((String[])currentSimAnnotsV.toArray(new String[currentSimAnnotsV.size()]));
			
		}
	
		bmcs.scNames = (String[])scNamesV.toArray(new String[scNamesV.size()]);
		bmcs.appTypes = (MathType[])appTypesV.toArray(new MathType[appTypesV.size()]);
		bmcs.scAnnots = (String[])scAnnotsV.toArray(new String[scAnnotsV.size()]);
		bmcs.geoNames = (String[])geoNamesV.toArray(new String[geoNamesV.size()]); 
		bmcs.geoDims = geoDimsArr;
		bmcs.simNames = (String[][])simNamesV.toArray(new String[simNamesV.size()][]);
		bmcs.simAnnots = (String[][])simAnnotsV.toArray(new String[simAnnotsV.size()][]);
	}catch(Exception e)
	{
		System.out.println("Failed reading BioModelChildSummary info..." + st.toString());
		e.printStackTrace(System.out);
	}
	return bmcs;
}


/**
 * Insert the method's description here.
 * Creation date: (8/23/2004 9:52:25 PM)
 * @return cbit.vcell.biomodel.BioModelChildSummary
 */
public static BioModelChildSummary getExample() {
	BioModelChildSummary bmChildSummary = new BioModelChildSummary();
	bmChildSummary.scNames = new String[]	{	"App1",		"App2",		"App3"	};
	bmChildSummary.scAnnots = new String[]	{	null,		"hello2",	null	};
	bmChildSummary.geoNames = new String[]	{	"geo1",		"geo2",		"geo3"	};
	bmChildSummary.geoDims = new int[]		{	0,			2,			3		};
	bmChildSummary.simNames = new String[][]{{ "sim11" }, { "sim2" }, { "sim3" }};
	bmChildSummary.simAnnots = new String[][]{{ null },    { "an\nnot2" }, { null }};
	return bmChildSummary;
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2004 2:23:12 PM)
 * @return int[]
 */
public int[] getGeometryDimensions() {
	return geoDims;
}

/**
 * Insert the method's description here.
 * Creation date: (8/20/2004 2:23:12 PM)
 * @return int[]
 */
public String[] getGeometryNames() {
	return geoNames;
}

public MathType[] getAppTypes() {
	return appTypes;
}

/**
 * Insert the method's description here.
 * Creation date: (8/20/2004 2:18:34 PM)
 * @return java.lang.String[]
 */
public String[] getSimulationAnnotations(String simulationContextName) {
	for (int i = 0; i < scNames.length; i++){
		if (scNames[i].equals(simulationContextName)){
			return simAnnots[i];
		}
	}
	throw new RuntimeException("application '"+simulationContextName+"' not found");
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2004 2:20:38 PM)
 * @return java.lang.String[]
 */
public String[] getSimulationContextAnnotations() {
	return scAnnots;
}

public String[]getSimulationContextNames()
{
	return scNames;
}

/**
 * Insert the method's description here.
 * Creation date: (8/20/2004 2:18:34 PM)
 * @return java.lang.String[]
 */
public String[] getSimulationNames(String simulationContextName) {
	for (int i = 0; i < scNames.length; i++){
		if (scNames[i].equals(simulationContextName)){
			return simNames[i];
		}
	}
	throw new RuntimeException("application '"+simulationContextName+"' not found");
}


/**
 * Insert the method's description here.
 * Creation date: (8/23/2004 1:30:12 PM)
 * @return java.lang.String
 */
public String toDatabaseSerialization() {

	if (scNames.length==0){
		return NOCHILDREN;
	}
	StringBuffer sb = new StringBuffer();
	for(int i = 0; i < scNames.length;i+= 1){
		//Application (SimulationContext)
		sb.append(emptyConvention(TokenMangler.getChildSummaryElementEscapedString(scNames[i]))+"\n");
		//application type: deterministic or stochastic
		sb.append(emptyConvention(TokenMangler.getChildSummaryElementEscapedString(BioModelChildSummary.TYPE_TOKEN+appTypes[i]))+"\n");
		sb.append(emptyConvention(TokenMangler.getChildSummaryElementEscapedString(scAnnots[i]))+"\n");
		sb.append(emptyConvention(TokenMangler.getChildSummaryElementEscapedString(geoNames[i]))+"\n");
		sb.append(geoDims[i]+GEOMETRY_DIMENSION_SUFFIX+"\n");
		//Simulations
		sb.append(simNames[i].length+"\n");//num simulations
		for(int j=0;j<simNames[i].length;j+= 1){
			sb.append(emptyConvention(TokenMangler.getChildSummaryElementEscapedString(simNames[i][j]))+"\n");
			sb.append(emptyConvention(TokenMangler.getChildSummaryElementEscapedString(simAnnots[i][j]))+"\n");
		}
	}
	return sb.toString();
}

/**
 * bundled information about Application / SimContext
 */
public static class ApplicationInfo {
	public final String name;
	public final MathType type; 
	public final int dimensions;
	public final String geometryName;
	ApplicationInfo(String name, MathType type, int dimensions,
			String geometryName) {
		super();
		this.name = name;
		this.type = type;
		this.dimensions = dimensions;
		this.geometryName = geometryName;
	}
	
	@Override
	public String toString() {
		return name + '(' + type + ' ' + dimensions + "D " + geometryName + ')';
	} 
}

/**
 * bundled {@link ApplicationInfo} information. Recreated on each method call.
 * @return non-null Collection
 */
public Collection<ApplicationInfo> getApplicationInfo( ) {
	ArrayList<ApplicationInfo> rval = new ArrayList<>();
	String[] appNames = getSimulationContextNames();
	MathType[] appTypeArray = getAppTypes();
	int[] dimSets = getGeometryDimensions();
	String[] gnames = getGeometryNames();
	for (int i = 0; i < appNames.length; i++) {
		ApplicationInfo ai = new ApplicationInfo(appNames[i],appTypeArray[i],dimSets[i],gnames[i]);
		rval.add(ai);
	}
	return rval;
}





































}

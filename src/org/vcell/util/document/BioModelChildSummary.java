package org.vcell.util.document;
import java.util.Vector;

/**
 * Insert the type's description here.
 * Creation date: (8/20/2004 2:11:48 PM)
 * @author: Jim Schaff
 */
public class BioModelChildSummary implements java.io.Serializable {
	private String scNames[] = new String[0];
	private String scAnnots[] = new String[0];
	private String geoNames[] = new String[0];
	private int geoDims[] = new int[0];

	private String simNames[][] = new String[0][];
	private String simAnnots[][] = new String[0][];

	private final static String NOCHILDREN = "NOCHILDREN";

/**
 * Insert the method's description here.
 * Creation date: (8/23/2004 9:58:44 PM)
 */
private BioModelChildSummary() {}

public BioModelChildSummary(String[] arg_scNames, String[] arg_scAnnots, String[][] arg_simNames, String[][] arg_simAnnots, String[] arg_geoNames, int[] arg_geoDims){
	this.scNames = arg_scNames;
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
	if (databaseSerialization.equals(NOCHILDREN)){
		return bmcs;
	}
	
	//Assumes there is a non-empty string for every element
	java.util.StringTokenizer st = new java.util.StringTokenizer(databaseSerialization,"\n",false);
	Vector<String> scNamesV = new Vector<String>();
	Vector<String> scAnnotsV = new Vector<String>();
	Vector<String> geoNamesV = new Vector<String>();
	int[] geoDimsArr = new int[0];

	Vector<String[]> simNamesV = new Vector<String[]>();
	Vector<String[]> simAnnotsV = new Vector<String[]>();

	while(st.hasMoreElements()){
		scNamesV.add(org.vcell.util.TokenMangler.getChildSummaryElementRestoredString((String)st.nextElement()));
		scAnnotsV.add(org.vcell.util.TokenMangler.getChildSummaryElementRestoredString((String)st.nextElement()));
		geoNamesV.add(org.vcell.util.TokenMangler.getChildSummaryElementRestoredString((String)st.nextElement()));
		int[] temp = new int[geoDimsArr.length + 1];
		System.arraycopy(geoDimsArr,0,temp,0,geoDimsArr.length);
		temp[temp.length-1] = Integer.parseInt((String)st.nextElement());
		geoDimsArr = temp;
		
		int numSims = Integer.parseInt((String)st.nextElement());
		Vector<String> currentSimNamesV = new Vector<String>();
		Vector<String> currentSimAnnotsV= new Vector<String>();
		for(int j=0;j<numSims;j+= 1){
			currentSimNamesV.add(org.vcell.util.TokenMangler.getChildSummaryElementRestoredString((String)st.nextElement()));
			currentSimAnnotsV.add(org.vcell.util.TokenMangler.getChildSummaryElementRestoredString((String)st.nextElement()));
		}
		simNamesV.add((String[])currentSimNamesV.toArray(new String[currentSimNamesV.size()]));
		simAnnotsV.add((String[])currentSimAnnotsV.toArray(new String[currentSimAnnotsV.size()]));
	}

	bmcs.scNames = (String[])scNamesV.toArray(new String[scNamesV.size()]);
	bmcs.scAnnots = (String[])scAnnotsV.toArray(new String[scAnnotsV.size()]);
	bmcs.geoNames = (String[])geoNamesV.toArray(new String[geoNamesV.size()]);
	bmcs.geoDims = geoDimsArr;
	bmcs.simNames = (String[][])simNamesV.toArray(new String[simNamesV.size()][]);
	bmcs.simAnnots = (String[][])simAnnotsV.toArray(new String[simAnnotsV.size()][]);
	
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


/**
 * Insert the method's description here.
 * Creation date: (8/20/2004 2:18:57 PM)
 * @return java.lang.String[]
 */
public String[] getSimulationContextNames() {
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
		sb.append(emptyConvention(org.vcell.util.TokenMangler.getChildSummaryElementEscapedString(scNames[i]))+"\n");
		sb.append(emptyConvention(org.vcell.util.TokenMangler.getChildSummaryElementEscapedString(scAnnots[i]))+"\n");
		sb.append(emptyConvention(org.vcell.util.TokenMangler.getChildSummaryElementEscapedString(geoNames[i]))+"\n");
		sb.append(geoDims[i]+"\n");
		//Simulations
		sb.append(simNames[i].length+"\n");//num simulations
		for(int j=0;j<simNames[i].length;j+= 1){
			sb.append(emptyConvention(org.vcell.util.TokenMangler.getChildSummaryElementEscapedString(simNames[i][j]))+"\n");
			sb.append(emptyConvention(org.vcell.util.TokenMangler.getChildSummaryElementEscapedString(simAnnots[i][j]))+"\n");
		}
	}
	return sb.toString();
}
}
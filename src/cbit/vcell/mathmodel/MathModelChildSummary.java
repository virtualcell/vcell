package cbit.vcell.mathmodel;

import java.util.StringTokenizer;
import java.util.Vector;

import cbit.util.TokenMangler;
import cbit.vcell.biomodel.BioModelChildSummary;
import cbit.vcell.math.MathDescription;
/**
 * Insert the type's description here.
 * Creation date: (8/20/2004 2:11:48 PM)
 * @author: Jim Schaff
 */
public class MathModelChildSummary implements java.io.Serializable {
	private String geoName = null;
	private int geoDim = 0;
	
	private String simNames[] = null;
	private String simAnnots[] = null;
	//math model type deterministic or stochastic
	private String modelType = null;
/**
 * Insert the method's description here.
 * Creation date: (8/23/2004 10:13:07 PM)
 */
private MathModelChildSummary() {}
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
 * Creation date: (8/23/2004 11:41:14 AM)
 * @param savedMathModel cbit.vcell.mathmodel.MathModel
 */
public static MathModelChildSummary fromDatabaseMathModel(MathModel savedMathModel) {

	MathModelChildSummary mmcs = new MathModelChildSummary();
	mmcs.modelType = getModelType(savedMathModel.getMathDescription());
	mmcs.geoName = savedMathModel.getMathDescription().getGeometry().getName();
	mmcs.geoDim = savedMathModel.getMathDescription().getGeometry().getDimension();
	
	cbit.vcell.solver.Simulation[] sims = savedMathModel.getSimulations();
	mmcs.simNames = new String[sims.length];
	mmcs.simAnnots = new String[sims.length];
	for(int i=0;i<sims.length;i+= 1){
		mmcs.simNames[i] = sims[i].getName();
		mmcs.simAnnots[i] = sims[i].getDescription();
	}
	return mmcs;
}

public static String getModelType(MathDescription md)
{
	String result = BioModelChildSummary.TYPE_DETER_STR;
	//stoch/determ 
	if(md.isStoch()) //deterministic spatial
	{
		result = BioModelChildSummary.TYPE_STOCH_STR;
	}
	
	return BioModelChildSummary.TYPE_TOKEN + result;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2004 1:30:48 PM)
 * @return cbit.vcell.biomodel.BioModelChildSummary
 * @param databaseSerialization java.lang.String
 */
public static MathModelChildSummary fromDatabaseSerialization(String databaseSerialization) {
	
	MathModelChildSummary mmcs = new MathModelChildSummary();
	StringTokenizer st = null;
	try{
		//Assumes there is a non-empty string for every element
		st = new java.util.StringTokenizer(databaseSerialization,"\n",false);
		//check if tere is new field(model type), if so, read it and next stringTokenizer will be geometry name.
		//otherwise this tokenStr is geometry name.
		String tokenStr = (String)st.nextElement();
		if(tokenStr.startsWith(BioModelChildSummary.TYPE_TOKEN))
		{
			mmcs.modelType = TokenMangler.getChildSummaryElementRestoredString(tokenStr);
			mmcs.geoName = TokenMangler.getChildSummaryElementRestoredString((String)st.nextElement());
		}
		else
		{
			mmcs.modelType = BioModelChildSummary.TYPE_UNKNOWN_STR;
			mmcs.geoName = TokenMangler.getChildSummaryElementRestoredString(tokenStr);
		}
//		mmcs.modelType = (String)TokenMangler.getChildSummaryElementRestoredString((String)st.nextElement());
//		mmcs.geoName = (String)TokenMangler.getChildSummaryElementRestoredString((String)st.nextElement());
		mmcs.geoDim = Integer.parseInt((String)st.nextElement());
		
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

public String getModelType()
{
	return modelType;
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
	sb.append(emptyConvention(TokenMangler.getChildSummaryElementEscapedString(modelType))+"\n");
	sb.append(emptyConvention(TokenMangler.getChildSummaryElementEscapedString(geoName))+"\n");

	sb.append(geoDim+"\n");
	
	//Simulations
	sb.append(simNames.length+"\n");//num simulations
	for(int j=0;j<simNames.length;j+= 1){
		sb.append(emptyConvention(TokenMangler.getChildSummaryElementEscapedString(simNames[j]))+"\n");
		sb.append(emptyConvention(TokenMangler.getChildSummaryElementEscapedString(simAnnots[j]))+"\n");
	}
	
	return sb.toString();
}
}

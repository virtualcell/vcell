package cbit.vcell.simdata;
import java.util.StringTokenizer;

import cbit.vcell.math.CommentStringTokenizer;
import cbit.vcell.parser.MathMLTags;
import cbit.vcell.server.User;
import cbit.vcell.server.VCDataIdentifier;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.sql.KeyValue;
/**
 * Insert the type's description here.
 * Creation date: (9/18/2006 12:55:46 PM)
 * @author: Jim Schaff
 */
public class ExternalDataIdentifier implements java.io.Serializable,VCDataIdentifier  {
	private cbit.sql.KeyValue key;
	private cbit.vcell.server.User owner;
	private String name;

/**
 * FieldDataIdentifier constructor comment.
 */
public ExternalDataIdentifier(KeyValue arg_key, cbit.vcell.server.User argOwner,String argName) {
	super();
	key = arg_key;
	owner = argOwner;
	name = argName;
}

public static ExternalDataIdentifier fromTokens(StringTokenizer st){
	return
		new ExternalDataIdentifier(
				KeyValue.fromString(st.nextToken()),
				new User(st.nextToken(),KeyValue.fromString(st.nextToken())),
				st.nextToken()
				);
}
public String toCSVString(){
	return key.toString()+","+owner.getName()+","+owner.getID().toString()+","+name;
}
public String getID() {
	return "SimID_"+getKey().toString()+"_0_";
}


public String getName(){
	return name;
}

/**
 * Insert the method's description here.
 * Creation date: (9/18/2006 12:56:35 PM)
 * @return cbit.sql.KeyValue
 */
public cbit.sql.KeyValue getKey() {
	return key;
}


/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 12:33:44 PM)
 * @return cbit.util.Extent
 */
public cbit.vcell.server.User getOwner() {
	return owner;
}


/**
 * FieldDataIdentifier constructor comment.
 */


public static String createCanonicalSimFilePathName(KeyValue fieldDataKey,int timeIndex,int jobIndex,boolean isOldStyle){
	return
		createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+
		(timeIndex>=1000?timeIndex+"":"")+
		(timeIndex>=100 && timeIndex<1000?"0"+timeIndex:"")+
		(timeIndex>=10 && timeIndex<100?"00"+timeIndex:"")+
		(timeIndex<10?"000"+timeIndex:"")+SimDataConstants.PDE_DATA_EXTENSION;
}

public static String createCanonicalFieldFunctionSyntax(ExternalDataIdentifier edi,String varName,double beginTime,double endtime){
	return MathMLTags.FIELD+"("+edi.getName()+","+varName+","+beginTime+")";

}

public static String createCanonicalSimZipFileName(KeyValue fieldDataKey,int zipIndex,int jobIndex,boolean isOldStyle){
	return
	createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+
	(zipIndex<10?"0":"")+zipIndex+".zip";
}

public static String createCanonicalSimLogFileName(KeyValue fieldDataKey,int jobIndex,boolean isOldStyle){
	return
	createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+
	SimDataConstants.LOGFILE_EXTENSION;
}

public static String createCanonicalMeshFileName(KeyValue fieldDataKey,int jobIndex,boolean isOldStyle){
	return
	createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+
	SimDataConstants.MESHFILE_EXTENSION;
}
public static String createCanonicalFunctionsFileName(KeyValue fieldDataKey,int jobIndex,boolean isOldStyle){
	return
		createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+
		SimDataConstants.FUNCTIONFILE_EXTENSION;
}

private static String createSimIDWithJobIndex(KeyValue fieldDataKey,int jobIndex,boolean isOldStyle){
	if(isOldStyle && jobIndex != 0){
		throw new IllegalArgumentException("Job index must be 0 for Old Style names");
	}
	String name = Simulation.createSimulationID(fieldDataKey);
	if(!isOldStyle){
		name = SimulationJob.createSimulationJobID(name, jobIndex);
	}
	return name;
}
@Override
public String toString() {
	// TODO Auto-generated method stub
	return getName()+" "+getKey().toString()+" "+getOwner().toString();
}
}
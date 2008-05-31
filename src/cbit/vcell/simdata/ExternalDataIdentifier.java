package cbit.vcell.simdata;
import java.util.StringTokenizer;

import cbit.util.Matchable;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.SimResampleInfoProvider;
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
public class ExternalDataIdentifier implements java.io.Serializable,VCDataIdentifier,Matchable{
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

public static String createCanonicalFieldDataLogFileName(KeyValue fieldDataKey){
	return
	createSimIDWithJobIndex(fieldDataKey,0,false)+
	SimDataConstants.LOGFILE_EXTENSION;
}

public static String createCanonicalFieldFunctionSyntax(String externalDataIdentifierName,String varName,double beginTime,String extDataIdVariableTypeName){	
	VariableType vt = VariableType.getVariableTypeFromVariableTypeName(extDataIdVariableTypeName);
	return MathMLTags.FIELD+"("+
		externalDataIdentifierName+","+varName+","+beginTime+
		(vt.equals(VariableType.UNKNOWN)?"": ","+vt.getTypeName())+")";
}

public static String createCanonicalSimZipFileName(KeyValue fieldDataKey,int zipIndex,int jobIndex,boolean isOldStyle){
	return
	createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+
	(zipIndex<10?"0":"")+zipIndex+SimDataConstants.ZIPFILE_EXTENSION;
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

public static String createCanonicalResampleFileName(SimResampleInfoProvider simResampleInfoProvider,FieldFunctionArguments fieldFuncArgs){
	return
		createSimIDWithJobIndex(
				simResampleInfoProvider.getSimulationKey(),
				simResampleInfoProvider.getJobIndex(),
				!simResampleInfoProvider.isParameterScanType())+
		FieldDataIdentifierSpec.getDefaultFieldDataFileNameForSimulation(fieldFuncArgs);
}

public static String createSimIDWithJobIndex(KeyValue fieldDataKey,int jobIndex,boolean isOldStyle){
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
	return getName()+" "+getKey().toString()+" "+getOwner().toString();
}

@Override
public boolean equals(Object obj) {

	if(obj instanceof ExternalDataIdentifier){
		return
		((ExternalDataIdentifier)obj).getKey().equals(getKey())
		&&
		((ExternalDataIdentifier)obj).getName().equals(getName())
		&&
		((ExternalDataIdentifier)obj).getOwner().equals(getOwner());
	}
	return false;
		
}

@Override
public int hashCode() {
	return getKey().hashCode();
}

public boolean compareEqual(Matchable obj) {
	if(this == obj){
		return true;
	}
	if(obj instanceof ExternalDataIdentifier){
		ExternalDataIdentifier compareToExtDataID = (ExternalDataIdentifier)obj;
		if(key.compareEqual(compareToExtDataID.key)){
			if(owner.compareEqual(compareToExtDataID.owner)){
				if(name.equals(compareToExtDataID.name)){
					return true;
				}
			}
		}
	}
	return false;
}
}
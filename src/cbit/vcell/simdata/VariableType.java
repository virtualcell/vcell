package cbit.vcell.simdata;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (10/3/00 2:07:11 PM)
 * @author: 
 */
public class VariableType implements java.io.Serializable, cbit.util.Matchable {

	private int type = -1;

	private static final int MIN_PDE_TYPE = 1;
	private static final int MAX_PDE_TYPE = 6;
	
	private static final int UNKNOWN_TYPE = 0;
	private static final int VOLUME_TYPE = 1;
	private static final int MEMBRANE_TYPE = 2;
	private static final int CONTOUR_TYPE = 3;
	private static final int VOLUME_REGION_TYPE = 4;
	private static final int MEMBRANE_REGION_TYPE = 5;
	private static final int CONTOUR_REGION_TYPE = 6;
	private static final int NONSPATIAL_TYPE = 7;
	
	private static final String[] NAMES = {"Unknown","Volume","Membrane","Contour","Volume_Region","Membrane_Region","Contour_Region","Nonspatial"};
	private static final String[] LABEL = {"Unknown","Conc","Density","Density","Conc","Density","Density","Conc"};
	private static final String[] UNITS = {"Unknown","uM","molecules/um^2","molecules/um","uM","molecules/um^2","molecules/um","uM"};
	
	public static final VariableType UNKNOWN = new VariableType(UNKNOWN_TYPE);
	public static final VariableType VOLUME = new VariableType(VOLUME_TYPE);
	public static final VariableType MEMBRANE = new VariableType(MEMBRANE_TYPE);
	public static final VariableType CONTOUR = new VariableType(CONTOUR_TYPE);
	public static final VariableType VOLUME_REGION = new VariableType(VOLUME_REGION_TYPE);
	public static final VariableType MEMBRANE_REGION = new VariableType(MEMBRANE_REGION_TYPE);
	public static final VariableType CONTOUR_REGION = new VariableType(CONTOUR_REGION_TYPE);
	public static final VariableType NONSPATIAL = new VariableType(NONSPATIAL_TYPE);
/**
 * PDEVariableType constructor comment.
 */
private VariableType(int varType) {
	super();
	this.type = varType;
}
/**
 * Insert the method's description here.
 * Creation date: (4/28/2005 11:44:25 AM)
 */
public static cbit.vcell.simdata.DataIdentifier[] collectSimilarDataTypes(String variableName,cbit.vcell.simdata.DataIdentifier[] dataIDs){
	
	cbit.vcell.simdata.VariableType vt = null;
	for(int i=0;i<dataIDs.length;i+= 1){
		if(dataIDs[i].getName().equals(variableName)){
			vt = dataIDs[i].getVariableType();
			break;
		}
	}
	if(vt == null){
		throw new IllegalArgumentException("couldn't find variable "+variableName+" in dataIdentifiers list");
	}

	//Sort variable names, ignore case
	java.util.TreeSet treeSet = new java.util.TreeSet(
		new java.util.Comparator(){
			public int compare(Object o1, Object o2){
				int ignoreCaseB = ((cbit.vcell.simdata.DataIdentifier)o1).getName().compareToIgnoreCase(((cbit.vcell.simdata.DataIdentifier)o2).getName());
				if(ignoreCaseB == 0){
					return ((cbit.vcell.simdata.DataIdentifier)o1).getName().compareTo(((cbit.vcell.simdata.DataIdentifier)o2).getName());
				}
				return ignoreCaseB;
			}
		}
	);
	for(int i=0;i<dataIDs.length;i+= 1){
		if( 
			(
				(vt.equals(cbit.vcell.simdata.VariableType.VOLUME) ||
				vt.equals(cbit.vcell.simdata.VariableType.VOLUME_REGION))
				&&
				(dataIDs[i].getVariableType().equals(cbit.vcell.simdata.VariableType.VOLUME) ||
				dataIDs[i].getVariableType().equals(cbit.vcell.simdata.VariableType.VOLUME_REGION))
			)
			||
			(
				(vt.equals(cbit.vcell.simdata.VariableType.MEMBRANE) ||
				vt.equals(cbit.vcell.simdata.VariableType.MEMBRANE_REGION))
				&&
				(dataIDs[i].getVariableType().equals(cbit.vcell.simdata.VariableType.MEMBRANE) ||
				dataIDs[i].getVariableType().equals(cbit.vcell.simdata.VariableType.MEMBRANE_REGION))
			)
			){
				
			treeSet.add(dataIDs[i]);
		}
	}

	cbit.vcell.simdata.DataIdentifier[] results = new cbit.vcell.simdata.DataIdentifier[treeSet.size()];
	treeSet.toArray(results);
	return results;
}
/**
 * Insert the method's description here.
 * Creation date: (5/24/2001 9:28:51 PM)
 * @return boolean
 * @param obj cbit.util.Matchable
 */
public boolean compareEqual(cbit.util.Matchable obj) {
	return equals(obj);
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:42:33 PM)
 */
public boolean equals(Object obj) {
	if (obj == null) {
		return false;
	}
	if (!(obj instanceof VariableType)) {
		return false;
	}
	VariableType pdeVT = (VariableType) obj;
	if (type!=pdeVT.type) {
		return false;
	}
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:10:25 PM)
 * @return java.lang.String
 */
public String getDefaultLabel() {
	return LABEL[type];
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:10:37 PM)
 * @return java.lang.String
 */
public String getDefaultUnits() {
	return UNITS[type];
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 2:38:35 PM)
 * @return int
 */
public int getType() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:13:10 PM)
 * @return java.lang.String
 */
public String getTypeName() {
	return NAMES[type];
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:48:55 PM)
 * @return cbit.vcell.simdata.PDEVariableType
 * @param mesh cbit.vcell.solvers.CartesianMesh
 * @param dataLength int
 */
public static final VariableType getVariableTypeFromInteger(int varType) {
	if (varType==VOLUME.type){
		return VOLUME;
	}else if (varType==MEMBRANE.type){
		return MEMBRANE;
	}else if (varType==CONTOUR.type){
		return CONTOUR;
	}else if (varType==VOLUME_REGION.type){
		return VOLUME_REGION;
	}else if (varType==MEMBRANE_REGION.type){
		return MEMBRANE_REGION;
	}else if (varType==CONTOUR_REGION.type){
		return CONTOUR_REGION;
	}else{
		throw new IllegalArgumentException("varType="+varType+" is undefined");
	}
}

public static final VariableType getVariableTypeFromString(String type) {	
	for (int i = 0; i < NAMES.length; i ++) {
		if (type.equals(NAMES[i])) {
			return new VariableType(i);
		}		
	}
	
	throw new IllegalArgumentException("varType="+type+" is undefined");	
}

/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:48:55 PM)
 * @return cbit.vcell.simdata.PDEVariableType
 * @param mesh cbit.vcell.solvers.CartesianMesh
 * @param dataLength int
 */
public static final VariableType getVariableTypeFromLength(cbit.vcell.solvers.CartesianMesh mesh, int dataLength) {
	VariableType result = null;
	if (mesh.getDataLength(VOLUME) == dataLength) {
		result = VOLUME;
	} else if (mesh.getDataLength(MEMBRANE) == dataLength) {
		result = MEMBRANE;
	} else if (mesh.getDataLength(CONTOUR) == dataLength) {
		result = CONTOUR;
	} else if (mesh.getDataLength(VOLUME_REGION) == dataLength) {
		result = VOLUME_REGION;
	} else if (mesh.getDataLength(MEMBRANE_REGION) == dataLength) {
		result = MEMBRANE_REGION;
	} else if (mesh.getDataLength(CONTOUR_REGION) == dataLength) {
		result = CONTOUR_REGION;
	}
	return result;
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 11:01:55 AM)
 * @return int
 */
public int hashCode() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (7/6/01 4:05:42 PM)
 * @return boolean
 * @param varType cbit.vcell.simdata.VariableType
 */
public boolean isExpansionOf(VariableType varType) {
	//
	// an enclosing domain (e.g. VOLUME) is an expansion of an enclosed region (e.g. VOLUME_REGION).
	//
	// example: if VOLUME_REGION and VOLUME data are used in same function,
	// then function must be evaluated at each volume index (hence VOLUME wins).
	//
	if (type == VOLUME_TYPE && varType.type == VOLUME_REGION_TYPE) return true;
	if (type == MEMBRANE_TYPE && varType.type == MEMBRANE_REGION_TYPE) return true;
	if (type == CONTOUR_TYPE && varType.type == CONTOUR_REGION_TYPE) return true;
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (5/8/01 2:12:01 PM)
 * @return java.lang.String
 */
public String toString() {
	return getTypeName()+"_VariableType";
}
}

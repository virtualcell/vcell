package cbit.vcell.simdata;

import cbit.util.User;
import cbit.util.VCDataIdentifier;
/**
 * Insert the type's description here.
 * Creation date: (10/7/2003 4:21:41 PM)
 * @author: Anuradha Lakshminarayana
 */
public class MergedDataInfo implements VCDataIdentifier, java.io.Serializable, cbit.util.Matchable {
	private java.lang.String datasetName = null;
	private User datasetUser = null;
	private VCDataIdentifier[] dataIDs = null;
/**
 * CompositeDataInfo constructor comment.
 */
public MergedDataInfo(User argOwner, VCDataIdentifier[] argdataIDs) {
	super();
	String compDataName = argOwner.getName();
	for (int i = 0; i < argdataIDs.length; i++) {
		compDataName = compDataName+"_"+argdataIDs[i].getID();
	}
	datasetName = compDataName;
	datasetUser = argOwner;
	dataIDs = argdataIDs;
}
/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(cbit.util.Matchable obj) {
	MergedDataInfo mergedDataInfo = null;
	if (obj == null) {
		return false;
	}
	if (!(obj instanceof MergedDataInfo)) {
		return false;
	} else {
		mergedDataInfo = (MergedDataInfo) obj;
	}

	if (getDatasetName().equals(mergedDataInfo.getDatasetName())) {
		return false;
	}
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/01 12:24:41 PM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	if (object instanceof MergedDataInfo){
		MergedDataInfo mergedInfo = (MergedDataInfo)object;
		if (!getID().equals(mergedInfo.getID())){
			return false;
		}
		return true;
	}
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (10/21/2003 12:47:21 PM)
 * @return cbit.vcell.server.VCDataIdentifier[]
 */
public VCDataIdentifier[] getDataIDs() {
	return dataIDs;
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/2003 4:22:40 PM)
 * @return java.lang.String
 */
public java.lang.String getDatasetName() {
	return datasetName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2003 11:22:01 AM)
 * @return cbit.vcell.server.User
 */
public cbit.util.User getDatasetUser() {
	return datasetUser;
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/2003 4:22:40 PM)
 * @return java.lang.String
 */
public String getID() {
	return datasetName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/2003 4:22:40 PM)
 * @return java.lang.String
 */
public User getOwner() {
	return getDatasetUser();
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/01 12:28:06 PM)
 * @return int
 */
public int hashCode() {
	return getID().hashCode();
}
}

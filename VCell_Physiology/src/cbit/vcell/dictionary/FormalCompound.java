package cbit.vcell.dictionary;

/**
 * Insert the type's description here.
 * Creation date: (2/18/2003 6:10:04 PM)
 * @author: Frank Morgan
 */
public class FormalCompound extends DBFormalSpecies {

	private CompoundInfo compoundInfo = null;

/**
 * Compound constructor comment.
 * @param argKey cbit.sql.KeyValue
 * @param argDBFormalSpeciesInfo DBFormalSpeciesInfo
 */
public FormalCompound(cbit.util.document.KeyValue argCompoundInfoKey, CompoundInfo argCompoundInfo) {
	
	super(argCompoundInfoKey,argCompoundInfo);

	
	this.compoundInfo = argCompoundInfo;
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 6:51:31 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 */
public CompoundInfo getCompoundInfo() {
	return compoundInfo;
}
}

package cbit.vcell.dictionary;

/**
 * Insert the type's description here.
 * Creation date: (2/18/2003 6:10:04 PM)
 * @author: Frank Morgan
 */
public class BoundCompound extends DBSpecies {

	private FormalCompound formalCompound = null;

/**
 * Compound constructor comment.
 * @param argKey cbit.sql.KeyValue
 * @param argDBFormalSpeciesInfo DBFormalSpeciesInfo
 */
public BoundCompound(cbit.sql.KeyValue argDBSpeciesKey,FormalCompound argFormalCompound) {
	
	super(argDBSpeciesKey,argFormalCompound);

	
	this.formalCompound = argFormalCompound;
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 6:51:31 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 */
public CompoundInfo getCompoundInfo() {
	return formalCompound.getCompoundInfo();
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 6:51:31 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 */
public cbit.sql.KeyValue getCompoundKey() {
	return formalCompound.getDBFormalSpeciesKey();
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 6:51:31 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 */
public FormalCompound getFormalCompound() {
	return formalCompound;
}
}

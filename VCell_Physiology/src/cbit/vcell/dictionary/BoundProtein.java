package cbit.vcell.dictionary;

/**
 * Insert the type's description here.
 * Creation date: (2/18/2003 6:10:04 PM)
 * @author: Frank Morgan
 */
public class BoundProtein extends DBSpecies {

	private FormalProtein formalProtein = null;

/**
 * Compound constructor comment.
 * @param argKey cbit.sql.KeyValue
 * @param argDBFormalSpeciesInfo DBFormalSpeciesInfo
 */
public BoundProtein(cbit.util.KeyValue argDBSpeciesKey,FormalProtein argFormalProtein) {
	
	super(argDBSpeciesKey,argFormalProtein);

	
	this.formalProtein = argFormalProtein;
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 6:51:31 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 */
public FormalProtein getProteinEnzyme() {
	return formalProtein;
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 6:51:31 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 */
public ProteinInfo getProteinInfo() {
	return formalProtein.getProteinInfo();
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 6:51:31 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 */
public cbit.util.KeyValue getProteinKey() {
	return formalProtein.getDBFormalSpeciesKey();
}
}

package cbit.vcell.dictionary;

/**
 * Insert the type's description here.
 * Creation date: (2/18/2003 6:10:04 PM)
 * @author: Frank Morgan
 */
public class FormalProtein extends DBFormalSpecies {

	private ProteinInfo proteinInfo = null;

/**
 * Compound constructor comment.
 * @param argKey cbit.sql.KeyValue
 * @param argDBFormalSpeciesInfo DBFormalSpeciesInfo
 */
public FormalProtein(cbit.util.document.KeyValue argProteinInfoKey, ProteinInfo argProteinInfo) {
	
	super(argProteinInfoKey,argProteinInfo);

	
	this.proteinInfo = argProteinInfo;
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 6:51:31 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 */
public ProteinInfo getProteinInfo() {
	return proteinInfo;
}
}

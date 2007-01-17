package cbit.vcell.dictionary;

/**
 * Insert the type's description here.
 * Creation date: (2/18/2003 6:10:04 PM)
 * @author: Frank Morgan
 */
public class FormalEnzyme extends DBFormalSpecies implements cbit.sql.Cacheable{

	private EnzymeInfo enzymeInfo = null;

/**
 * Compound constructor comment.
 * @param argKey cbit.sql.KeyValue
 * @param argDBFormalSpeciesInfo DBFormalSpeciesInfo
 */
public FormalEnzyme(cbit.sql.KeyValue argEnzymeInfoKey, EnzymeInfo argEnzymeInfo) {
	
	super(argEnzymeInfoKey,argEnzymeInfo);

	
	this.enzymeInfo = argEnzymeInfo;
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 6:51:31 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 */
public EnzymeInfo getEnzymeInfo() {
	return enzymeInfo;
}
}

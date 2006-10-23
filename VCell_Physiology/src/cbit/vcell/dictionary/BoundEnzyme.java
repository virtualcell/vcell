package cbit.vcell.dictionary;

/**
 * Insert the type's description here.
 * Creation date: (2/18/2003 6:10:04 PM)
 * @author: Frank Morgan
 */
public class BoundEnzyme extends DBSpecies implements cbit.util.Cacheable{

	private FormalEnzyme formalEnzyme = null;

/**
 * Compound constructor comment.
 * @param argKey cbit.sql.KeyValue
 * @param argDBFormalSpeciesInfo DBFormalSpeciesInfo
 */
public BoundEnzyme(cbit.util.document.KeyValue argDBSpeciesKey,FormalEnzyme argFormalEnzyme) {
	
	super(argDBSpeciesKey,argFormalEnzyme);

	this.formalEnzyme = argFormalEnzyme;
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 6:51:31 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 */
public EnzymeInfo getEnzymeInfo() {
	return formalEnzyme.getEnzymeInfo();
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 6:51:31 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 */
public cbit.util.document.KeyValue getEnzymeKey() {
	return formalEnzyme.getDBFormalSpeciesKey();
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 6:51:31 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 */
public FormalEnzyme getFormalEnzyme() {
	return formalEnzyme;
}
}

package cbit.vcell.model;

import cbit.vcell.model.ReservedBioSymbolEntries.ReservedBioSymbolNameScope;
import cbit.vcell.parser.AbstractNameScope;
import cbit.vcell.parser.NameScope;
/**
 * Insert the type's description here.
 * Creation date: (7/31/2003 12:40:03 PM)
 * @author: Jim Schaff
 */
public abstract class BioNameScope extends AbstractNameScope {
/**
 * BioNameScope constructor comment.
 */
public BioNameScope() {
	super();
}

/**
 * the default for BioNameScope subclasses is to treat the ReservedSymbolNameScope as a peer
 * see ReservedSymbolNameScope.isPeer() which accepts any subclass of BioNameScope as a peer).
 */
public boolean isPeer(NameScope nameScope){
	if (nameScope instanceof ReservedBioSymbolNameScope){
		return true;
	}else{
		return false;
	}
}
}

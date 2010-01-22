package org.vcell.sybil.util.http.uniprot.box.imp;

/*   UniProtBox  --- by Oliver Ruebenacker, UCHC --- January 2010
 *   A wrapper for a UniProt entry
 */

import org.vcell.sybil.util.http.uniprot.box.UniProtBox;

public class ThingImp implements UniProtBox.Thing {

	protected final UniProtBox box;
	
	public ThingImp(UniProtBox box) { this.box = box; }

	public UniProtBox box() { return box; }

}

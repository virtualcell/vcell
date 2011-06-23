package org.vcell.sybil.models.sbbox.factories;

/*   SBBoxFactory  --- by Oliver Ruebenacker, UCHC --- October 2009
 *   Creates SBBox objects
 */

import org.openrdf.model.Graph;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.SBBoxImp;
import org.vcell.sybil.rdf.impl.HashGraph;

public class SBBoxFactory {

	public static SBBox create() {
		Graph data = new HashGraph();
		return new SBBoxImp(data);
	}
	
}

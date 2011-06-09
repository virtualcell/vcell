package org.vcell.sybil.models.sbbox.factories;

/*   SBBoxFactory  --- by Oliver Ruebenacker, UCHC --- October 2009
 *   Creates SBBox objects
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.SBBoxImp;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class SBBoxFactory {

	public static SBBox create() {
		Model data = ModelFactory.createDefaultModel();
		return new SBBoxImp(data);
	}
	
}

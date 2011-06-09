package org.vcell.sybil.models.sbbox.imp;

/*   SBBoxImp  --- by Oliver Ruebenacker, UCHC --- June 2008 to January 2010
 *   Organizes the RDF data and structures to edit it
 */

import java.io.Serializable;

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.factories.Factories;
import com.hp.hpl.jena.rdf.model.Model;

@SuppressWarnings("serial")
public class SBBoxImp implements SBBox, Serializable {
	
	protected Model rdf;
	
	protected Factories facs;
		
	public SBBoxImp(Model rdf) {
		this.rdf = rdf;
		facs = new FactoriesImp(this);
	}
	
	public Model getRdf() { return rdf; }
	
	public Factories factories() { return facs; }

}

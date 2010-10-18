package org.vcell.sybil.models.sbbox.imp;

/*   SBBoxImp  --- by Oliver Ruebenacker, UCHC --- June 2008 to January 2010
 *   Organizes the RDF data and structures to edit it
 */

import java.io.Serializable;

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.factories.Factories;
import org.vcell.sybil.models.sbbox.factories.LocationFactory;
import org.vcell.sybil.models.sbbox.factories.SubstanceFactory;
import org.vcell.sybil.util.label.LabelMan;
import com.hp.hpl.jena.rdf.model.Model;

@SuppressWarnings("serial")
public class SBBoxImp extends InfBoxImp implements SBBox, Serializable {
	
	protected String baseURI;
	protected LabelMan<SBBox.NamedThing> labelMan;
	
	protected Factories facs;
	
	protected LocationFactory locaFac = new LocationFactory(this);
	protected SubstanceFactory subsFac = new SubstanceFactory(this);
	
	public SBBoxImp(Model coreNew, Model schemaNew, String baseURI, LabelMan<SBBox.NamedThing> labelMan) {
		super(coreNew, schemaNew);
		this.baseURI = baseURI;
		this.labelMan = labelMan;
		facs = new FactoriesImp(this);
	}
	
	public String baseURI() { return baseURI; }
	public LabelMan<SBBox.NamedThing> labelMan() { return labelMan; }
	
	public Factories factories() { return facs; }

}

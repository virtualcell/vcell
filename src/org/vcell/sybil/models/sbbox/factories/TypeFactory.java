package org.vcell.sybil.models.sbbox.factories;

/*   TypeFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for types (i.e. OWL classes)
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.MutableRDFType;
import org.vcell.sybil.models.sbbox.imp.RDFTypeImp;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;

@SuppressWarnings("serial")
public class TypeFactory extends ThingFactory<MutableRDFType> {

	public TypeFactory(SBBox box) { super(box, OWL.Class); }
	public SBBox.MutableRDFType newThing(Resource node) { return new RDFTypeImp(box, node); }
	public String baseLabel() { return "Type"; }
	
}

package org.vcell.sybil.models.sbbox.imp;

/*   StoichiometryImp  --- by Oliver Ruebenacker, UCHC --- June to November 2009
 *   A view of a resource representing an SBPAX Stoichiometry
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class StoichiometryImp extends SBWrapper implements SBBox.MutableStoichiometry {

	public StoichiometryImp(SBBox box, Resource resource) { super(box, resource); }

	public float sc() { 
		float sc = 1;
		boolean foundOne = false;
		StmtIterator stmtIter = 
			box().getRdf().listStatements(this.resource(), SBPAX.hasNumber, (RDFNode) null);
		while(stmtIter.hasNext() && !foundOne) { 
			RDFNode scNode = stmtIter.nextStatement().getObject(); 
			if(scNode instanceof Literal) {
				Literal scLiteral = (Literal) scNode;
				float scFloat = scLiteral.getFloat();
				if(scFloat > 0) {
					sc = scFloat;
					foundOne = true;
				}
			}
		}
		return sc; 
	}

	public SBBox.Unit unit() { 
		Resource uni = null;
		StmtIterator stmtIter = 
			box().getRdf().listStatements(this.resource(), SBPAX.hasUnit, (RDFNode) null);
		while(stmtIter.hasNext() && uni == null) { 
			RDFNode uniNode = stmtIter.nextStatement().getObject(); 
			if(uniNode instanceof Resource) { uni = (Resource) uniNode; }
		}
		SBBox.Unit unit = null;
		if(uni != null) { unit = box().factories().unitFactory().open(uni); }
		return unit; 
	}

	public StoichiometryImp setSC(float sc) { 
		box().getRdf().removeAll(this.resource(), SBPAX.hasNumber, (RDFNode) null);
		box().getRdf().add(this.resource(), SBPAX.hasNumber, box().getRdf().createTypedLiteral(sc));
		return this;
	}

	public StoichiometryImp setUnit(SBBox.Unit unit) {
		SBBox box1 = box();
		box1.getRdf().removeAll(this.resource(), SBPAX.hasUnit, (RDFNode) null);
		if(unit != null) { 
			box1.getRdf().add(this.resource(), SBPAX.hasUnit, unit.resource()); 
		}
		return this;
	}

}
package org.vcell.sybil.models.sbbox.imp;

/*   LocationImp  --- by Oliver Ruebenacker, UCHC --- June 2009 to April 2010
 *   A view of a resource representing an SBPAX Location
 */

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.MutableLocation;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class LocationImp extends SBWrapper implements SBBox.MutableLocation {

	public LocationImp(SBBox box, Resource resource) { super(box, resource); }

	public int dims() { 
		int dimensions = 3;
		boolean foundOne = false;
		StmtIterator stmtIter = 
			box().getRdf().listStatements(this.resource(), SBPAX.hasDimensions, (RDFNode) null);
		while(stmtIter.hasNext() && !foundOne) { 
			RDFNode dimNode = stmtIter.nextStatement().getObject(); 
			if(dimNode instanceof Literal) {
				Literal dimLiteral = (Literal) dimNode;
				int dimInt = dimLiteral.getInt();
				if(dimInt > 0) {
					dimensions = dimInt;
					foundOne = true;
				}
			}
		}
		if(!foundOne) { 
			if(name().indexOf("Membrane") > 0 || name().indexOf("membrane") > 0) { 
				dimensions = 2; 
			} 
		}
		return dimensions; 
	}

	public Set<SBBox.Location> locationsSurrounded() { 
		Set<LocationImp> surroundeds = new HashSet<LocationImp>();
		ResIterator surroundedIter = 
			box().getRdf().listSubjectsWithProperty(SBPAX.surroundedBy, this.resource());
		while(surroundedIter.hasNext()) { 
			surroundeds.add(new LocationImp(box(), surroundedIter.nextResource())); 
		}
		return Collections.<SBBox.Location>unmodifiableSet(surroundeds); 
	}

	public SBBox.Location locationSurrounding() { 
		Resource surr = null;
		StmtIterator stmtIter = 
			box().getRdf().listStatements(resource(), SBPAX.surroundedBy, (RDFNode) null);
		while(stmtIter.hasNext() && surr == null) { 
			RDFNode dimNode = stmtIter.nextStatement().getObject(); 
			if(dimNode instanceof Resource) { surr = (Resource) dimNode; }
		}
		LocationImp surrounding1 = null;
		if(surr != null) { surrounding1 = new LocationImp(box(), surr); }
		return surrounding1; 
	}

	public MutableLocation setDims(int dims) { 
		box().getRdf().removeAll(this.resource(), SBPAX.hasDimensions, (RDFNode) null);
		box().getRdf().add(this.resource(), SBPAX.hasDimensions, box().getRdf().createTypedLiteral(dims));
		return this;
	}

	public MutableLocation setSurrounding(SBBox.Location surrounding) {
		box().getRdf().removeAll(this.resource(), SBPAX.surroundedBy, (RDFNode) null);
		if(surrounding != null) { 
			box().getRdf().add(this.resource(), SBPAX.surroundedBy, surrounding.resource()); 
		}
		return this;
	}

}
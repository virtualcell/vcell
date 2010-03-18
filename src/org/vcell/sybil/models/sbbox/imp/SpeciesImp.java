package org.vcell.sybil.models.sbbox.imp;

/*   SpeciesImp  --- by Oliver Ruebenacker, UCHC --- June to November 2009
 *   A view of a resource representing an SBPAX Species
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.Location;
import org.vcell.sybil.models.sbbox.SBBox.Substance;
import org.vcell.sybil.rdf.schemas.SBPAX;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class SpeciesImp extends SBWrapper implements SBBox.MutableSpecies {

	public SpeciesImp(SBBox man, Resource resource) { super(man, resource); }

	public SBBox.Substance substance() { 
		SBBox.Substance substance = null;
		StmtIterator stmtIter = 
			box().getRdf().listStatements(this.resource(), SBPAX.consistsOf, (RDFNode) null);
		while(stmtIter.hasNext() && substance == null) { 
			RDFNode dimNode = stmtIter.nextStatement().getObject(); 
			if(dimNode instanceof Resource) {
				substance = box().factories().substance().open((Resource)dimNode);
			}
		}
		return substance; 
	}

	public SBBox.Location location() { 
		SBBox.MutableLocation location = null;
		StmtIterator stmtIter = 
			box().getRdf().listStatements(this.resource(), SBPAX.locatedAt, (RDFNode) null);
		while(stmtIter.hasNext() && location == null) { 
			RDFNode locNode = stmtIter.nextStatement().getObject(); 
			if(locNode instanceof Resource) { 
				location = box().factories().location().open((Resource) locNode);
			}
		}
		return location; 
	}

	public SBBox.MutableSpecies setSubstance(SBBox.Substance substance) { 
		box().getRdf().removeAll(this.resource(), SBPAX.consistsOf, (RDFNode) null);
		box().getRdf().add(this.resource(), SBPAX.consistsOf, substance.resource());
		return this;
	}

	public SBBox.MutableSpecies setLocation(SBBox.Location surrounding) {
		box().getRdf().removeAll(this.resource(), SBPAX.locatedAt, (RDFNode) null);
		if(surrounding != null) { 
			box().getRdf().add(this.resource(), SBPAX.locatedAt, surrounding.resource()); 
		}
		return this;
	}
	
	public String name() { 
		String name;
		
		Substance substance = substance();
		Location location = location();
		if(substance != null && location != null) { 
			name = substance.name() + " in the " + location.name();					
		} else if(substance != null) {
			name = substance.name();			
		} else {
			name = super.name();
		}
		return name; 
	}
	
	public String shortName() { 
		String shortName;
		Substance substance = substance();
		Location location = location();
		if(substance != null && location != null) {
			shortName = substance.shortName() + " (" + location.shortName() + ")";			
		} else if (substance != null) {
			shortName = substance.shortName();			
		} else {
			shortName = super.shortName();
		}
		return shortName; 
	}

}
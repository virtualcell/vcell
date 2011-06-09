package org.vcell.sybil.models.sbbox.imp;

/*   FactoriesImp  --- by Oliver Ruebenacker, UCHC --- June 2008 to November 2009
 *   Organizes the RDF data and structures to edit it
 */

import java.io.Serializable;

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.factories.Factories;
import org.vcell.sybil.models.sbbox.factories.LocationFactory;
import org.vcell.sybil.models.sbbox.factories.ProcessModelFactory;
import org.vcell.sybil.models.sbbox.factories.SpeciesFactory;
import org.vcell.sybil.models.sbbox.factories.SubstanceFactory;
import org.vcell.sybil.models.sbbox.factories.SystemModelFactory;

@SuppressWarnings("serial")
public class FactoriesImp implements Factories, Serializable {
	
	protected LocationFactory locaFac;
	protected SubstanceFactory subsFac;
	protected SpeciesFactory specFac;
	protected ProcessModelFactory procModFac;
	protected SystemModelFactory systModFac;
	
	public FactoriesImp(SBBox box) {
		locaFac = new LocationFactory(box);
		subsFac = new SubstanceFactory(box);
		specFac = new SpeciesFactory(box);
		procModFac = new ProcessModelFactory(box);
		systModFac = new SystemModelFactory(box);
	}

	public LocationFactory locationFactory() { return locaFac; }
	public SubstanceFactory substanceFactory() { return subsFac; }
	public SpeciesFactory speciesFactory() { return specFac; }
	public ProcessModelFactory processModelFactory() { return procModFac; }
	public SystemModelFactory systemModelFactory() { return systModFac; }
	
}
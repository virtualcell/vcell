package org.vcell.sybil.models.sbbox.factories;

/*   Factories  --- by Oliver Ruebenacker, UCHC --- June 2008 to November 2009
 *   Organizes the RDF data and structures to edit it
 */

public interface Factories {
	public LocationFactory locationFactory();
	public SubstanceFactory substanceFactory();
	public SpeciesFactory speciesFactory();
	public ProcessModelFactory processModelFactory();
	public SystemModelFactory systemModelFactory();
}
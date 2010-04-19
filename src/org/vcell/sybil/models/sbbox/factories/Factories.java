package org.vcell.sybil.models.sbbox.factories;

/*   Factories  --- by Oliver Ruebenacker, UCHC --- June 2008 to November 2009
 *   Organizes the RDF data and structures to edit it
 */

import org.vcell.sybil.models.sbbox.SBBox;

public interface Factories {
	public SBBox box();
	public TypeFactory typeFactory();
	public LocationFactory locationFactory();
	public SubstanceFactory substanceFactory();
	public UnitFactory unitFactory();
	public StoichiometryFactory stoichiometryFactory();
	public SpeciesFactory speciesFactory();
	public ParticipantFactory participantFactory();
	public ParticipantLeftFactory participantLeftFactory();
	public ParticipantRightFactory participantRightFactory();
	public ParticipantCatalystFactory participantCatalystFactory();
	public InteractionFactory interactionFactory();
	public ProcessFactory processFactory();
	public ProcessModelFactory processModelFactory();
	public SystemModelFactory systemModelFactory();
	public USTAssumptionFactory ustAssumptionFactory();
}
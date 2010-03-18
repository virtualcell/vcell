package org.vcell.sybil.models.sbbox.factories;

/*   Factories  --- by Oliver Ruebenacker, UCHC --- June 2008 to November 2009
 *   Organizes the RDF data and structures to edit it
 */

import org.vcell.sybil.models.sbbox.SBBox;

public interface Factories {
	public SBBox box();
	public TypeFactory type();
	public LocationFactory location();
	public SubstanceFactory substance();
	public UnitFactory unit();
	public StoichiometryFactory stoichiometry();
	public SpeciesFactory species();
	public ParticipantFactory participant();
	public ParticipantLeftFactory participantLeft();
	public ParticipantRightFactory participantRight();
	public ParticipantCatalystFactory participantCatalyst();
	public InteractionFactory interaction();
	public ProcessFactory process();
	public ProcessModelFactory processModel();
	public SystemModelFactory systemModel();
	public USTAssumptionFactory ustAssumption();
}
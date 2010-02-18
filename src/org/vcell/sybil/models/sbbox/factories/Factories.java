package org.vcell.sybil.models.sbbox.factories;

/*   Factories  --- by Oliver Ruebenacker, UCHC --- June 2008 to November 2009
 *   Organizes the RDF data and structures to edit it
 */

import org.vcell.sybil.models.sbbox.SBBox;

public interface Factories {
	public SBBox box();
	public TypeFactory type();
	public LocationFactory loca();
	public SubstanceFactory subs();
	public UnitFactory unit();
	public StoichiometryFactory stoi();
	public SpeciesFactory spec();
	public ParticipantFactory part();
	public ParticipantLeftFactory partLeft();
	public ParticipantRightFactory partRight();
	public ParticipantCatalystFactory partCat();
	public InteractionFactory inte();
	public ProcessFactory proc();
	public ProcessModelFactory procMod();
	public SystemModelFactory systMod();
	public USTAssumptionFactory ustSump();
}
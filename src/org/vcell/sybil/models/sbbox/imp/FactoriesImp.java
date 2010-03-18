package org.vcell.sybil.models.sbbox.imp;

/*   FactoriesImp  --- by Oliver Ruebenacker, UCHC --- June 2008 to November 2009
 *   Organizes the RDF data and structures to edit it
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.factories.Factories;
import org.vcell.sybil.models.sbbox.factories.InteractionFactory;
import org.vcell.sybil.models.sbbox.factories.LocationFactory;
import org.vcell.sybil.models.sbbox.factories.ParticipantCatalystFactory;
import org.vcell.sybil.models.sbbox.factories.ParticipantFactory;
import org.vcell.sybil.models.sbbox.factories.ParticipantLeftFactory;
import org.vcell.sybil.models.sbbox.factories.ParticipantRightFactory;
import org.vcell.sybil.models.sbbox.factories.ProcessFactory;
import org.vcell.sybil.models.sbbox.factories.ProcessModelFactory;
import org.vcell.sybil.models.sbbox.factories.SpeciesFactory;
import org.vcell.sybil.models.sbbox.factories.StoichiometryFactory;
import org.vcell.sybil.models.sbbox.factories.SubstanceFactory;
import org.vcell.sybil.models.sbbox.factories.SystemModelFactory;
import org.vcell.sybil.models.sbbox.factories.TypeFactory;
import org.vcell.sybil.models.sbbox.factories.USTAssumptionFactory;
import org.vcell.sybil.models.sbbox.factories.UnitFactory;

public class FactoriesImp implements Factories {
	
	protected SBBox box;
	protected TypeFactory typeFac;
	protected LocationFactory locaFac;
	protected SubstanceFactory subsFac;
	protected UnitFactory unitFac;
	protected StoichiometryFactory stoiFac;
	protected SpeciesFactory specFac;
	protected ParticipantFactory partFac;
	protected ParticipantLeftFactory partLeftFac;
	protected ParticipantRightFactory partRightFac;
	protected ParticipantCatalystFactory partCatFac;
	protected InteractionFactory inteFac;
	protected ProcessFactory procFac;
	protected ProcessModelFactory procModFac;
	protected SystemModelFactory systModFac;
	protected USTAssumptionFactory USTSumpFac;
	
	public FactoriesImp(SBBox box) {
		this.box = box;
		typeFac = new TypeFactory(box);
		locaFac = new LocationFactory(box);
		subsFac = new SubstanceFactory(box);
		unitFac = new UnitFactory(box);
		stoiFac = new StoichiometryFactory(box);
		specFac = new SpeciesFactory(box);
		partFac = new ParticipantFactory(box);
		partLeftFac = new ParticipantLeftFactory(box);
		partRightFac = new ParticipantRightFactory(box);
		partCatFac = new ParticipantCatalystFactory(box);
		inteFac = new InteractionFactory(box);
		procFac = new ProcessFactory(box);
		procModFac = new ProcessModelFactory(box);
		systModFac = new SystemModelFactory(box);
		USTSumpFac = new USTAssumptionFactory(box);
	}

	public SBBox box() { return box; }

	public TypeFactory type() { return typeFac; }
	public LocationFactory location() { return locaFac; }
	public SubstanceFactory substance() { return subsFac; }
	public UnitFactory unit() { return unitFac; }
	public StoichiometryFactory stoichiometry() { return stoiFac; }
	public SpeciesFactory species() { return specFac; }
	public ParticipantFactory participant() { return partFac; }
	public ParticipantLeftFactory participantLeft() { return partLeftFac; }
	public ParticipantRightFactory participantRight() { return partRightFac; }
	public ParticipantCatalystFactory participantCatalyst() { return partCatFac; }
	public InteractionFactory interaction() { return inteFac; }
	public ProcessFactory process() { return procFac; }
	public ProcessModelFactory processModel() { return procModFac; }
	public SystemModelFactory systemModel() { return systModFac; }
	public USTAssumptionFactory ustAssumption() { return USTSumpFac; }
	
}
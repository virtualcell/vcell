package org.vcell.sybil.models.sbbox;

/*   SBBox  --- by Oliver Ruebenacker, UCHC --- June 2008 to April 2010
 *   Organizes the RDF data and structures to edit it
 */

import java.util.Set;

import org.vcell.sybil.models.sbbox.factories.Factories;
import org.vcell.sybil.util.label.LabelMan;

public interface SBBox extends InferenceBox {
	
	public static interface NamedThing extends RDFThing {
		public String name();
		public String shortName();
		public String label();		
		public SBBox box();
	}
	
	public static interface RDFType extends NamedThing { 
		public Set<RDFType> directSuperTypes();
		public Set<RDFType> directSubTypes();
		public Set<RDFType> allSuperTypes();
		public Set<RDFType> allSubTypes();
		public boolean isSBType();
		public boolean isTypeOf(NamedThing thing);
		public RDFType getSBSubTypeFor(NamedThing thing);
	}
	
	public static interface MutableRDFType extends RDFType {
		public MutableRDFType addDirectSuperType(RDFType superType);
		public MutableRDFType removeDirectSuperType(RDFType superType);
		public MutableRDFType removeAllDirectSuperTypes();
		public MutableRDFType addDirectSubType(RDFType superType);
		public MutableRDFType removeDirectSubType(RDFType superType);
		public MutableRDFType removeAllDirectSubTypes();
	}
	
	public static interface Location extends NamedThing {
		public int dims();
		public Location locationSurrounding();
		public Set<Location> locationsSurrounded();
	}
	
	public static interface MutableLocation extends Location {
		public MutableLocation setDims(int dims);
		public MutableLocation setSurrounding(Location surrounding);
	}
	
	public static interface Substance extends NamedThing { 
		public Set<Substance> subSubstances();
		public boolean hasSubSubstance(Substance substance);
		public Set<Substance> superSubstances();
		public boolean hasSuperSubstance(Substance substance);
		public Set<RDFType> types();
		public boolean hasType(RDFType type);
	}
	
	public static interface MutableSubstance extends Substance {
		public void addSubSubstance(Substance substance);
		public void removeSubSubstance(Substance substance);
		public void removeAllSubSubstances();
		public void addSuperSubstance(Substance substance);
		public void removeSuperSubstance(Substance substance);
		public void removeAllSuperSubstances();
		public void addType(RDFType type);
		public void removeType(RDFType type);
		public void removeAllTypesButSubstance();
	}

	public static interface Species extends NamedThing { 
		public Substance substance();
		public Location location();
	}
	
	public static interface MutableSpecies extends Species {
		public MutableSpecies setSubstance(Substance substance);
		public MutableSpecies setLocation(Location surrounding);
	}
	
	public static interface Unit extends NamedThing { }
	
	public static interface Stoichiometry extends NamedThing {
		public float sc();
		public Unit unit();
	}
	
	public static interface MutableStoichiometry extends Stoichiometry {
		public MutableStoichiometry setSC(float sc);
		public MutableStoichiometry setUnit(Unit unit);
	}
	
	public static interface Participant extends NamedThing { 
		public Species species();
		public Stoichiometry stoichiometry();
	}
	
	public static interface MutableParticipant extends Participant {
		public MutableParticipant setSpecies(Species species);
		public MutableParticipant setStoichiometry(Stoichiometry stoichiometry);
	}
		
	public static interface ParticipantLeft extends Participant { }
	
	public static interface MutableParticipantLeft extends ParticipantLeft, MutableParticipant {
		public MutableParticipantLeft setSpecies(Species species);
		public MutableParticipantLeft setStoichiometry(Stoichiometry stoichiometry);
	}
		
	public static interface ParticipantRight extends Participant { }
	
	public static interface MutableParticipantRight extends ParticipantRight, MutableParticipant {
		public MutableParticipantRight setSpecies(Species species);
		public MutableParticipantRight setStoichiometry(Stoichiometry stoichiometry);
	}
		
	public static interface ParticipantCatalyst extends Participant  { }
	
	public static interface MutableParticipantCatalyst extends ParticipantCatalyst, MutableParticipant  {
		public MutableParticipantCatalyst setSpecies(Species species);
		public MutableParticipantCatalyst setStoichiometry(Stoichiometry stoichiometry);
	}

	public static interface Interaction extends NamedThing { }

	public static interface MutableInteraction extends Interaction { }
	
	public static interface Process extends Interaction { 
		public Set<Participant> participants();
		public Set<ParticipantLeft> participantsLeft();
		public Set<ParticipantRight> participantsRight();
		public Set<ParticipantCatalyst> participantsCat();
	}
	
	public static interface MutableProcess extends Process, MutableInteraction {
		public MutableProcess addParticipantLeft(ParticipantLeft participantLeft);
		public MutableProcess removeParticipantLeft(ParticipantLeft participantLeft);
		public MutableProcess removeAllParticipantsLeft();
		public MutableProcess addParticipantRight(ParticipantRight participantRight);
		public MutableProcess removeParticipantRight(ParticipantRight participantRight);
		public MutableProcess removeAllParticipantsRight();
		public MutableProcess addParticipantCat(ParticipantCatalyst participantCat);
		public MutableProcess removeParticipantCat(ParticipantCatalyst participantCat);
		public MutableProcess addParticipant(Participant participant);
		public MutableProcess removeParticipant(Participant participant);
		public MutableProcess removeAllParticipantsCat();
		public MutableProcess removeAllParticipants();
	}
	
	public static interface SBModel extends NamedThing { }	
	
	public static interface ProcessModel extends SBModel {
		public MutableProcess process();
	}
	
	public static interface MutableProcessModel extends ProcessModel {
		public MutableProcessModel setProcess(Process process);
	}
	
	public static interface SystemModel extends SBModel { 
		public Set<ProcessModel> processModels();
		public Set<Substance> substances();
		public Set<Species> specieses();
	}
	
	public static interface MutableSystemModel extends SystemModel {
		public MutableSystemModel addProcessModel(ProcessModel processModel);
		public MutableSystemModel removeProcessModel(ProcessModel processModel);
		public MutableSystemModel removeAllProcessModels();
		public MutableSystemModel addSubstance(Substance substance);
		public MutableSystemModel removeSubstance(Substance substance);
		public MutableSystemModel removeAllSubstances();
		public MutableSystemModel addSpecies(Species species);
		public MutableSystemModel removeSpecies(Species species);
		public MutableSystemModel removeAllSpecieses();
	}
	
	public static interface USTAssumption extends NamedThing {
		public Set<RDFType> typesItAppliesTo();
		public boolean appliesTo(RDFType type);
	}
	
	public static interface MutableUSTAssumption extends USTAssumption {
		public MutableUSTAssumption addTypeItAppliesTo(RDFType type);
		public MutableUSTAssumption removeTypeItAppliesTo(RDFType type);
		public MutableUSTAssumption removeAllTypesItAppliesTo();
	}
	
	public LabelMan<NamedThing> labelMan();
	public String baseURI();

	public Factories factories();
		
}

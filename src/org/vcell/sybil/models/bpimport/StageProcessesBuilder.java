package org.vcell.sybil.models.bpimport;

/*   StageProcessesBuilder  --- by Oliver Ruebenacker, UCHC --- July 2008 to November 2009
 *   Building a model of SBPAX processes from a set of edges
 */

import java.util.HashMap;
import java.util.Map;

import org.vcell.sybil.models.bpimport.edges.EdgeSBTray;
import org.vcell.sybil.models.bpimport.edges.MutableEdge;
import org.vcell.sybil.models.bpimport.edges.ProcessEdge;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.Location;
import org.vcell.sybil.models.sbbox.SBBox.MutableLocation;
import org.vcell.sybil.models.sbbox.SBBox.MutableParticipant;
import org.vcell.sybil.models.sbbox.SBBox.MutableParticipantCatalyst;
import org.vcell.sybil.models.sbbox.SBBox.MutableParticipantLeft;
import org.vcell.sybil.models.sbbox.SBBox.MutableParticipantRight;
import org.vcell.sybil.models.sbbox.SBBox.MutableProcess;
import org.vcell.sybil.models.sbbox.SBBox.MutableSpecies;
import org.vcell.sybil.models.sbbox.SBBox.MutableStoichiometry;
import org.vcell.sybil.models.sbbox.SBBox.MutableSubstance;
import org.vcell.sybil.models.sbbox.SBBox.MutableRDFType;
import org.vcell.sybil.models.sbbox.SBBox.Substance;
import org.vcell.sybil.models.views.SBWorkView;
import org.vcell.sybil.util.keys.KeyOfTwo;

public class StageProcessesBuilder {

	public static class SpeciesKey extends KeyOfTwo<Substance, Location> {
		public SpeciesKey(Substance substance, Location location) { super(substance, location); }
		public Substance substance() { return a(); }
		public Location location() { return b(); }
	}
	
	public static void build(SBWorkView view) {
		SBBox box = view.box();
		EdgeSBTray edgeBox = new EdgeSBTray(box);
		for(ProcessEdge edgeRaw : view.tableModel().rows()) {
			MutableEdge edge = new MutableEdge(edgeBox, edgeRaw);
			if(edge.process() == null) { continue; }
			if(edge.participant() == null) { continue; }
			if(edge.substance() == null) {
				MutableSubstance entity = edge.entity();
				if(entity == null) { 
					if(edge.species() == null) { 
						continue; 
					}
				}
				edge.setSubstance(entity);
			}
			view.edges().add(edge);
		}
		Map<SpeciesKey, MutableSpecies> speciesMap = new HashMap<SpeciesKey, MutableSpecies>();
		for(MutableEdge edge : view.edges()) {
			MutableSubstance substance = edge.substance();
			MutableSubstance entity = edge.entity();
			if(entity != null && !substance.equals(entity)) { entity.addSubSubstance(substance); }
			MutableRDFType type = edge.entityType();
			if(type != null) {
				substance.addType(type);
				entity.addType(type);
			}
			MutableLocation location = edge.location();
			MutableSpecies species = edge.species();
			MutableSpecies speciesStored = null;
			SpeciesKey key = null; 
			if(substance != null && location != null) {
				key = new SpeciesKey(substance, location); 
				speciesStored = speciesMap.get(key);
			}
			if(species == null && speciesStored != null) { species = speciesStored; }
			if(species == null) { species = box.factories().speciesFactory().createAnonymous(); }
			if(key != null && speciesStored == null) { speciesMap.put(key, species); }
			if(substance != null) { species.setSubstance(substance); }
			if(location != null) { species.setLocation(location); }
			if(edge.species() == null) { edge.setSpecies(species); }
			MutableProcess process = edge.process();
			MutableParticipant participant = edge.participant();
			if(process != null && participant != null && species != null) {
				view.processes().add(process);
				MutableStoichiometry stoichiometry = edge.stoichiometry();
				if(stoichiometry == null) { stoichiometry = box.factories().stoichiometryFactory().createAnonymous(); }
				stoichiometry.setSC(edge.stoichiometricCoeff());
				participant.setStoichiometry(stoichiometry);
				participant.setSpecies(species);
				if(box.factories().participantCatalystFactory().makesTypeOf(participant)) {
					MutableParticipantCatalyst participantCatalyst = 
						box.factories().participantCatalystFactory().create(participant);
					process.addParticipantCat(participantCatalyst);
				} else if(box.factories().participantLeftFactory().makesTypeOf(participant)) {
					MutableParticipantLeft participantLeft = box.factories().participantLeftFactory().create(participant);
					process.addParticipantLeft(participantLeft);
				} else if(box.factories().participantRightFactory().makesTypeOf(participant)) {
					MutableParticipantRight participantRight = 
						box.factories().participantRightFactory().create(participant);
					process.addParticipantRight(participantRight);
				} else {
					process.addParticipant(participant);
				}
			}
		}
	}

}

package org.vcell.sybil.models.sbbox.imp;

/*   ProcessImp  --- by Oliver Ruebenacker, UCHC --- June to November 2009
 *   A view of a resource representing an SBPAX Process
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class ProcessImp extends SBWrapper implements SBBox.MutableProcess {

	public ProcessImp(SBBox box, Resource resource) { super(box, resource); }

	public Set<SBBox.Participant> participants() {
		Set<SBBox.Participant> participants = new HashSet<SBBox.Participant>();
		NodeIterator partIter = box().getRdf().listObjectsOfProperty(resource(), SBPAX.hasParticipant);
		while(partIter.hasNext()) {
			RDFNode partNode = partIter.nextNode();
			if(partNode instanceof Resource) {
				participants.add(box().factories().participantFactory().open((Resource) partNode));
			}
		}
		return participants;
	}

	public Set<SBBox.ParticipantLeft> participantsLeft() {
		Set<SBBox.ParticipantLeft> participantsLeft = new HashSet<SBBox.ParticipantLeft>();
		NodeIterator partIter = box().getRdf().listObjectsOfProperty(resource(), SBPAX.hasParticipantLeft);
		while(partIter.hasNext()) {
			RDFNode partNode = partIter.nextNode();
			if(partNode instanceof Resource) {
				participantsLeft.add(box().factories().participantLeftFactory().open((Resource) partNode));
			}
		}
		return participantsLeft;
	}

	public Set<SBBox.ParticipantRight> participantsRight() {
		Set<SBBox.ParticipantRight> participantsRight = new HashSet<SBBox.ParticipantRight>();
		NodeIterator partIter = box().getRdf().listObjectsOfProperty(resource(), SBPAX.hasParticipantRight);
		while(partIter.hasNext()) {
			RDFNode partNode = partIter.nextNode();
			if(partNode instanceof Resource) {
				participantsRight.add(box().factories().participantRightFactory().open((Resource) partNode));
			}
		}
		return participantsRight;
	}

	public Set<SBBox.ParticipantCatalyst> participantsCat() {
		Set<SBBox.ParticipantCatalyst> participantsCat = new HashSet<SBBox.ParticipantCatalyst>();
		NodeIterator partIter = 
			box().getRdf().listObjectsOfProperty(resource(), SBPAX.hasParticipantCatalyst);
		while(partIter.hasNext()) {
			RDFNode partNode = partIter.nextNode();
			if(partNode instanceof Resource) {
				participantsCat.add(box().factories().participantCatalystFactory().open((Resource) partNode));
			}
		}
		return participantsCat;
	}

	public SBBox.MutableProcess addParticipantLeft(SBBox.ParticipantLeft participantLeft) {
		box().getRdf().add(resource(), SBPAX.hasParticipantLeft, participantLeft.resource());
		return this;
	}

	public SBBox.MutableProcess addParticipantRight(SBBox.ParticipantRight participantRight) {
		box().getRdf().add(resource(), SBPAX.hasParticipantRight, participantRight.resource());
		return this;
	}

	public SBBox.MutableProcess addParticipantCat(SBBox.ParticipantCatalyst participantCat) {
		box().getRdf().add(resource(), SBPAX.hasParticipantCatalyst, participantCat.resource());
		return this;
	}

	public SBBox.MutableProcess addParticipant(SBBox.Participant participant) {
		box().getRdf().add(resource(), SBPAX.hasParticipant, participant.resource());
		return this;
	}

	public SBBox.MutableProcess removeParticipantLeft(SBBox.ParticipantLeft participantLeft) {
		box().getRdf().remove(resource(), SBPAX.hasParticipant, participantLeft.resource());
		box().getRdf().remove(resource(), SBPAX.hasParticipantLeft, participantLeft.resource());
		return this;
	}

	public SBBox.MutableProcess removeParticipantRight(SBBox.ParticipantRight participantRight) {
		box().getRdf().remove(resource(), SBPAX.hasParticipant, participantRight.resource());
		box().getRdf().remove(resource(), SBPAX.hasParticipantRight, participantRight.resource());
		return this;
	}

	public SBBox.MutableProcess removeParticipantCat(SBBox.ParticipantCatalyst participantCat) {
		box().getRdf().remove(resource(), SBPAX.hasParticipant, participantCat.resource());
		box().getRdf().remove(resource(), SBPAX.hasParticipantCatalyst, participantCat.resource());
		return this;
	}

	public SBBox.MutableProcess removeParticipant(SBBox.Participant participant) {
		box().getRdf().remove(resource(), SBPAX.hasParticipant, participant.resource());
		box().getRdf().remove(resource(), SBPAX.hasParticipantCatalyst, participant.resource());
		box().getRdf().remove(resource(), SBPAX.hasParticipantLeft, participant.resource());
		box().getRdf().remove(resource(), SBPAX.hasParticipantRight, participant.resource());
		return this;
	}

	public SBBox.MutableProcess removeAllParticipantsLeft() {
		for(SBBox.ParticipantLeft participantLeft : participantsLeft()) {
			removeParticipantLeft(participantLeft);
		}
		return this;
	}

	public SBBox.MutableProcess removeAllParticipantsRight() {
		for(SBBox.ParticipantRight participantRight : participantsRight()) {
			removeParticipantRight(participantRight);
		}
		return this;
	}

	public SBBox.MutableProcess removeAllParticipantsCat() {
		for(SBBox.ParticipantCatalyst participantCat : participantsCat()) {
			removeParticipantCat(participantCat);
		}
		return this;
	}

	public SBBox.MutableProcess removeAllParticipants() {
		removeAllParticipantsLeft();
		removeAllParticipantsRight();
		removeAllParticipantsCat();
		for(SBBox.Participant participant : participants()) {
			box().getRdf().remove(resource(), SBPAX.hasParticipant, participant.resource());			
		}
		return this;
	}

}
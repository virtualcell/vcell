/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.persistence;

import org.vcell.pathway.*;
import org.vcell.pathway.sbpax.SBEntityImpl;
import org.vcell.pathway.sbpax.SBMeasurable;
import org.vcell.pathway.sbpax.SBState;
import org.vcell.pathway.sbpax.SBVocabulary;

public class BiopaxProxy {

	
	public static interface RdfObjectProxy {
		public void setID(String id);
		public String getID();
	}
	
	public static class InteractionProxy extends InteractionImpl implements RdfObjectProxy { }
	public static class SBEntityProxy extends SBEntityImpl implements RdfObjectProxy { }
	public static class SBMeasurableProxy extends SBMeasurable implements RdfObjectProxy { }
	public static class SBStateProxy extends SBState implements RdfObjectProxy { }
	public static class SBVocabularyProxy extends SBVocabulary implements RdfObjectProxy { }
	public static class InteractionOrPathwayProxy extends InteractionImpl implements RdfObjectProxy { }
	public static class PathwayProxy extends Pathway implements RdfObjectProxy { }
	public static class PhysicalEntityProxy extends PhysicalEntity implements RdfObjectProxy { }
	public static class PhysicalEntityParticipantProxy extends PhysicalEntityParticipant implements RdfObjectProxy { }
	public static class SequenceParticipantProxy extends SequenceParticipant implements RdfObjectProxy { }
	public static class ParticipantDirectionVocabularyProxy extends ParticipantDirectionVocabulary implements RdfObjectProxy { }
	public static class SequenceFeatureProxy extends SequenceFeature implements RdfObjectProxy { }
	public static class SequenceIntervalProxy extends SequenceInterval implements RdfObjectProxy { }
	public static class SequenceLocationProxy extends SequenceLocation implements RdfObjectProxy { }
	public static class SequenceSiteProxy extends SequenceSite implements RdfObjectProxy { }
	public static class SequenceModificationVocabularyProxy extends SequenceModificationVocabulary implements RdfObjectProxy { }
	public static class BioPaxObjectProxy extends EntityImpl implements RdfObjectProxy { }
	public static class PathwayStepProxy extends PathwayStep implements RdfObjectProxy { }
	public static class ProvenanceProxy extends Provenance implements RdfObjectProxy { }
	public static class BioSourceProxy extends BioSource implements RdfObjectProxy { }
	public static class PhysicalEntityOrPathwayProxy extends EntityImpl implements RdfObjectProxy { }
	public static class StoichiometryProxy extends Stoichiometry implements RdfObjectProxy { }
	public static class XrefProxy extends Xref implements RdfObjectProxy { }
	public static class RelationshipTypeVocabularyProxy extends RelationshipTypeVocabulary implements RdfObjectProxy { }
	public static class EntityFeatureProxy extends EntityFeatureImpl implements RdfObjectProxy { }
	public static class CellularLocationVocabularyProxy extends CellularLocationVocabulary implements RdfObjectProxy { }
	public static class EntityReferenceProxy extends EntityReference implements RdfObjectProxy { }
	public static class GeneProxy extends Gene implements RdfObjectProxy { }
	public static class ConversionProxy extends ConversionImpl implements RdfObjectProxy { }
	public static class ChemicalStructureProxy extends ChemicalStructure implements RdfObjectProxy { }
	public static class CellVocabularyProxy extends CellVocabulary implements RdfObjectProxy { }
	public static class EvidenceProxy extends Evidence implements RdfObjectProxy { }
	public static class ExperimentalFormProxy extends ExperimentalForm implements RdfObjectProxy { }
	public static class DnaRegionReferenceProxy extends DnaRegionReference implements RdfObjectProxy { }
	public static class RnaRegionReferenceProxy extends RnaRegionReference implements RdfObjectProxy { }
	public static class EntityReferenceTypeVocabularyProxy extends EntityReferenceTypeVocabulary implements RdfObjectProxy { }
	public static class EvidenceCodeVocabularyProxy extends EvidenceCodeVocabulary implements RdfObjectProxy { }
	public static class ExperimentalFormVocabularyProxy extends ExperimentalFormVocabulary implements RdfObjectProxy { }
	public static class InteractionVocabularyProxy extends InteractionVocabulary implements RdfObjectProxy { }
	public static class PhenotypeVocabularyProxy extends PhenotypeVocabulary implements RdfObjectProxy { }
	public static class SequenceRegionVocabularyProxy extends SequenceRegionVocabulary implements RdfObjectProxy { }
	public static class TissueVocabularyProxy extends TissueVocabulary implements RdfObjectProxy { }
	public static class ScoreProxy extends Score implements RdfObjectProxy { }

}


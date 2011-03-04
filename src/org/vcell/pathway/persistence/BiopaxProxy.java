package org.vcell.pathway.persistence;

import org.vcell.pathway.BioSource;
import org.vcell.pathway.CellVocabulary;
import org.vcell.pathway.CellularLocationVocabulary;
import org.vcell.pathway.ChemicalStructure;
import org.vcell.pathway.ConversionImpl;
import org.vcell.pathway.DnaRegionReference;
import org.vcell.pathway.EntityFeatureImpl;
import org.vcell.pathway.EntityImpl;
import org.vcell.pathway.EntityReference;
import org.vcell.pathway.EntityReferenceTypeVocabulary;
import org.vcell.pathway.Evidence;
import org.vcell.pathway.EvidenceCodeVocabulary;
import org.vcell.pathway.ExperimentalForm;
import org.vcell.pathway.ExperimentalFormVocabulary;
import org.vcell.pathway.Gene;
import org.vcell.pathway.InteractionImpl;
import org.vcell.pathway.InteractionVocabulary;
import org.vcell.pathway.Pathway;
import org.vcell.pathway.PathwayStep;
import org.vcell.pathway.PhenotypeVocabulary;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.pathway.Provenance;
import org.vcell.pathway.RelationshipTypeVocabulary;
import org.vcell.pathway.RnaRegionReference;
import org.vcell.pathway.Score;
import org.vcell.pathway.SequenceLocation;
import org.vcell.pathway.SequenceModificationVocabulary;
import org.vcell.pathway.SequenceRegionVocabulary;
import org.vcell.pathway.SequenceSite;
import org.vcell.pathway.Stoichiometry;
import org.vcell.pathway.TissueVocabulary;
import org.vcell.pathway.Xref;

public class BiopaxProxy {

	
	public static interface RdfObjectProxy {
		public String getResource();
		public void setResource(String resource);
	}
	public static class InteractionProxy extends InteractionImpl implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class InteractionOrPathwayProxy extends InteractionImpl implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class PathwayProxy extends Pathway implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class PhysicalEntityProxy extends PhysicalEntity implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class PathwayStepProxy extends PathwayStep implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class ProvenanceProxy extends Provenance implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class BioSourceProxy extends BioSource implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class PhysicalEntityOrPathwayProxy extends EntityImpl implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class StoichiometryProxy extends Stoichiometry implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class XrefProxy extends Xref implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class SequenceLocationProxy extends SequenceLocation implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class SequenceSiteProxy extends SequenceSite implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class RelationshipTypeVocabularyProxy extends RelationshipTypeVocabulary implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class SequenceModificationVocabularyProxy extends SequenceModificationVocabulary implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class EntityFeatureProxy extends EntityFeatureImpl implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class CellularLocationVocabularyProxy extends CellularLocationVocabulary implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class EntityReferenceProxy extends EntityReference implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class GeneProxy extends Gene implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class ConversionProxy extends ConversionImpl implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class ChemicalStructureProxy extends ChemicalStructure implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class CellVocabularyProxy extends CellVocabulary implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class EvidenceProxy extends Evidence implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class ExperimentalFormProxy extends ExperimentalForm implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class DnaRegionReferenceProxy extends DnaRegionReference implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class RnaRegionReferenceProxy extends RnaRegionReference implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class EntityReferenceTypeVocabularyProxy extends EntityReferenceTypeVocabulary implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class EvidenceCodeVocabularyProxy extends EvidenceCodeVocabulary implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class ExperimentalFormVocabularyProxy extends ExperimentalFormVocabulary implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class InteractionVocabularyProxy extends InteractionVocabulary implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class PhenotypeVocabularyProxy extends PhenotypeVocabulary implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class SequenceRegionVocabularyProxy extends SequenceRegionVocabulary implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class TissueVocabularyProxy extends TissueVocabulary implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	public static class ScoreProxy extends Score implements RdfObjectProxy {
		private String resource;
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
	}

}


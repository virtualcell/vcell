/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.tree;

import java.util.HashMap;
import java.util.Map;

import org.vcell.pathway.BindingFeature;
import org.vcell.pathway.BindingFeatureImpl;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.BioPaxObjectImpl;
import org.vcell.pathway.BioSource;
import org.vcell.pathway.BiochemicalPathwayStep;
import org.vcell.pathway.BiochemicalReaction;
import org.vcell.pathway.BiochemicalReactionImpl;
import org.vcell.pathway.Catalysis;
import org.vcell.pathway.CellVocabulary;
import org.vcell.pathway.CellularLocationVocabulary;
import org.vcell.pathway.ChemicalStructure;
import org.vcell.pathway.Complex;
import org.vcell.pathway.ComplexAssembly;
import org.vcell.pathway.Control;
import org.vcell.pathway.ControlledVocabulary;
import org.vcell.pathway.Conversion;
import org.vcell.pathway.ConversionImpl;
import org.vcell.pathway.CovalentBindingFeature;
import org.vcell.pathway.Degradation;
import org.vcell.pathway.DeltaG;
import org.vcell.pathway.Dna;
import org.vcell.pathway.DnaReference;
import org.vcell.pathway.DnaRegion;
import org.vcell.pathway.DnaRegionReference;
import org.vcell.pathway.Entity;
import org.vcell.pathway.EntityFeature;
import org.vcell.pathway.EntityFeatureImpl;
import org.vcell.pathway.EntityImpl;
import org.vcell.pathway.EntityReference;
import org.vcell.pathway.EntityReferenceTypeVocabulary;
import org.vcell.pathway.Evidence;
import org.vcell.pathway.EvidenceCodeVocabulary;
import org.vcell.pathway.ExperimentalForm;
import org.vcell.pathway.ExperimentalFormVocabulary;
import org.vcell.pathway.FragmentFeature;
import org.vcell.pathway.Gene;
import org.vcell.pathway.GeneticInteraction;
import org.vcell.pathway.GroupObject;
import org.vcell.pathway.Interaction;
import org.vcell.pathway.InteractionImpl;
import org.vcell.pathway.InteractionParticipant;
import org.vcell.pathway.InteractionVocabulary;
import org.vcell.pathway.KPrime;
import org.vcell.pathway.ModificationFeature;
import org.vcell.pathway.ModificationFeatureImpl;
import org.vcell.pathway.Modulation;
import org.vcell.pathway.MolecularInteraction;
import org.vcell.pathway.Pathway;
import org.vcell.pathway.PathwayStep;
import org.vcell.pathway.PhenotypeVocabulary;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.pathway.Protein;
import org.vcell.pathway.ProteinReference;
import org.vcell.pathway.Provenance;
import org.vcell.pathway.PublicationXref;
import org.vcell.pathway.RelationshipTypeVocabulary;
import org.vcell.pathway.RelationshipXref;
import org.vcell.pathway.Rna;
import org.vcell.pathway.RnaReference;
import org.vcell.pathway.RnaRegion;
import org.vcell.pathway.RnaRegionReference;
import org.vcell.pathway.Score;
import org.vcell.pathway.SequenceInterval;
import org.vcell.pathway.SequenceLocation;
import org.vcell.pathway.SequenceModificationVocabulary;
import org.vcell.pathway.SequenceRegionVocabulary;
import org.vcell.pathway.SequenceSite;
import org.vcell.pathway.SmallMolecule;
import org.vcell.pathway.SmallMoleculeReference;
import org.vcell.pathway.Stoichiometry;
import org.vcell.pathway.TemplateReaction;
import org.vcell.pathway.TemplateReactionRegulation;
import org.vcell.pathway.TissueVocabulary;
import org.vcell.pathway.Transport;
import org.vcell.pathway.TransportImpl;
import org.vcell.pathway.TransportWithBiochemicalReaction;
import org.vcell.pathway.UnificationXref;
import org.vcell.pathway.UtilityClass;
import org.vcell.pathway.sbpax.SBEntity;
import org.vcell.pathway.sbpax.SBEntityImpl;
import org.vcell.pathway.sbpax.SBMeasurable;
import org.vcell.pathway.sbpax.SBState;

public class BioPAXClassNameDirectory {

	public static class ClassNameForward implements NameWithPlural {
		
		protected final Class<?> namedClass, namedClassForward;
		
		public ClassNameForward(Class<?> namedClass, Class<?> namedClassForward) {
			this.namedClass = namedClass;
			this.namedClassForward = namedClassForward;
		}
		
		public Class<?> getNamedClass() { return namedClass; }
		public Class<?> getNamedClassForward() { return namedClassForward; }
		public String getSingular() { return BioPAXClassNameDirectory.getNameSingular(getNamedClassForward()); }
		public String getPlural() { return BioPAXClassNameDirectory.getNamePlural(getNamedClassForward()); }
		
	}
	
	protected static Map<Class<?>, NameWithPlural> classNameMap = new HashMap<Class<?>, NameWithPlural>();
	
	public static void setName(Class<?> namedClass, String name) {
		classNameMap.put(namedClass, new NameWithPlural.SimpleNameWithPlural(name));
	}
	
	public static void setName(Class<?> namedClass, String name, String namePlural) {
		classNameMap.put(namedClass, new NameWithPlural.SimpleNameWithPlural(name, namePlural));
	}
	
	public static void setForward(Class<?> namedClass, Class<?> namedClassForward) {
		classNameMap.put(namedClass, new ClassNameForward(namedClass, namedClassForward));
	}
	
	public static NameWithPlural getClassName(Class<?> namedClass) {
		NameWithPlural className = classNameMap.get(namedClass);
		if(className == null) {
			className = new NameWithPlural.SimpleNameWithPlural(namedClass.getSimpleName());
		}
		return className;
	}
	
	public static String getNameSingular(Class<?> namedClass) {
		return getClassName(namedClass).getSingular();
	}
	
	public static String getNamePlural(Class<?> namedClass) {
		return getClassName(namedClass).getPlural();
	}
	
	static {
		setName(BindingFeature.class, "binding feature");
		setForward(BindingFeatureImpl.class, BindingFeature.class);
		setName(BiochemicalPathwayStep.class, "biochemical pathway step");
		setName(BiochemicalReaction.class, "biochemical reaction");
		setForward(BiochemicalReactionImpl.class, BiochemicalReaction.class);
		setName(BioPaxObject.class, "object");
		setForward(BioPaxObjectImpl.class, BioPaxObject.class);
		setName(BioSource.class, "bio-source");
		setName(Catalysis.class, "catalysis");
		setName(CellularLocationVocabulary.class, "cellular location term");
		setName(CellVocabulary.class, "cell-related term");
		setName(ChemicalStructure.class, "chemical structure");
		setName(Complex.class, "complex");
		setName(ComplexAssembly.class, "complex assembly", "complex assemblies");
		setName(Control.class, "control");
		setName(ControlledVocabulary.class, "term");
		setName(Conversion.class, "conversion");
		setForward(ConversionImpl.class, Conversion.class);
		setName(CovalentBindingFeature.class, "covalent binding feature");
		setName(Degradation.class, "degradation");
		setName(DeltaG.class, "delta-G entry", "delta-G entries");
		setName(Dna.class, "DNA");
		setName(DnaReference.class, "DNA reference");
		setName(DnaRegion.class, "DNA region");
		setName(DnaRegionReference.class, "DNA region reference");
		setName(Entity.class, "entity", "entities");
		setName(EntityFeature.class, "entity feature");
		setForward(EntityFeatureImpl.class, EntityFeature.class);
		setForward(EntityImpl.class, Entity.class);
		setName(EntityReference.class, "entity reference");
		setName(EntityReferenceTypeVocabulary.class, "entity reference type term");
		setName(Evidence.class, "evidence");
		setName(EvidenceCodeVocabulary.class, "evidence code terms");
		setName(ExperimentalForm.class, "experimental form");
		setName(ExperimentalFormVocabulary.class, "experimental form term");
		setName(FragmentFeature.class, "fragment feature");
		setName(Gene.class, "gene");
		setName(GeneticInteraction.class, "genetic interaction");
		setName(GroupObject.class, "group");
		setName(Interaction.class, "interaction");
		setForward(InteractionImpl.class, Interaction.class);
		setName(InteractionParticipant.class, "interaction participant");
		setName(InteractionVocabulary.class, "interaction term");
		setName(KPrime.class, "K' entry", "K' entries");
		setName(ModificationFeature.class, "modification feature");
		setForward(ModificationFeatureImpl.class, ModificationFeature.class);
		setName(Modulation.class, "modulation");
		setName(MolecularInteraction.class, "molecular interaction");
		setName(Pathway.class, "pathway");
		setName(PathwayStep.class, "pathway step");
		setName(PhenotypeVocabulary.class, "phenotype term");
		setName(PhysicalEntity.class, "physical entity", "physical entities");
		setName(Protein.class, "protein");
		setName(ProteinReference.class, "protein reference");
		setName(Provenance.class, "provenance");
		setName(PublicationXref.class, "publication cross-reference");
		setName(RelationshipTypeVocabulary.class, "relationship type term");
		setName(RelationshipXref.class, "relationship cross-reference");
		setName(Rna.class, "RNA");
		setName(RnaReference.class, "RNA reference");
		setName(RnaRegion.class, "RNA region");
		setName(RnaRegionReference.class, "RNA region reference");
		setName(SBEntity.class, "entry");
		setForward(SBEntityImpl.class, SBEntity.class);
		setName(SBMeasurable.class, "quantity");
		setName(SBState.class, "data set");
		setName(Score.class, "score");
		setName(SequenceInterval.class, "sequence interval");
		setName(SequenceLocation.class, "sequence location");
		setName(SequenceModificationVocabulary.class, "sequence modification term");
		setName(SequenceRegionVocabulary.class, "sequence region term");
		setName(SequenceSite.class, "sequence site");
		setName(SmallMolecule.class, "small molecule");
		setName(SmallMoleculeReference.class, "small molecule reference");
		setName(Stoichiometry.class, "stoichiometry", "stoichiometries");
		setName(TemplateReaction.class, "template reaction");
		setName(TemplateReactionRegulation.class, "template reaction regulation");
		setName(TissueVocabulary.class, "tissue term");
		setName(Transport.class, "transport");
		setForward(TransportImpl.class, Transport.class);
		setName(TransportWithBiochemicalReaction.class, 
				"transport with biochemical reaction", 
				"transports with biochemical reactions");
		setName(UnificationXref.class, "unification cross-reference");
		setName(UtilityClass.class, "other entry", "other entries");
	}
	
}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.graph;

import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vcell.model.rbm.MolecularType;

import cbit.gui.graph.GraphContainerLayoutReactions;
import cbit.gui.graph.GraphEvent;
import cbit.gui.graph.GraphPane;
import cbit.gui.graph.Shape;
import cbit.vcell.graph.structures.StructureSuite;
import cbit.vcell.model.Catalyst;
import cbit.vcell.model.Diagram;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.GroupingCriteria;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Product;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionRuleParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.RuleParticipantLongSignature;
import cbit.vcell.model.RuleParticipantShortSignature;
import cbit.vcell.model.RuleParticipantSignature;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;

public abstract class ReactionCartoon extends ModelCartoon {

	protected StructureSuite structureSuite = null;
	protected Set<RuleParticipantSignature> ruleParticipantSignatures = new HashSet<>();
	
	public ReactionCartoon() {
		containerLayout = new GraphContainerLayoutReactions();
	}

	public StructureSuite getStructureSuite() {
		return structureSuite;
	}
	
	protected abstract GroupingCriteria getRuleParticipantGroupingCriteria();
	protected abstract void rebindAll(Diagram diagram);
	protected abstract void refreshAll(boolean transitioning);
	protected abstract void applyDefaults(Diagram diagram);
	protected abstract void setPositionsFromReactionCartoon(Diagram diagram);
	
	public void cleanupAll() {
		if (getStructureSuite() != null) {
			for(Structure structure : getStructureSuite().getStructures()) {
				structure.removePropertyChangeListener(this);
				if (structure instanceof Membrane) {
					Membrane membrane = (Membrane) structure;
					if (membrane.getMembraneVoltage() != null) {
						membrane.getMembraneVoltage().removePropertyChangeListener(this);
					}
				}
			}
		}
		if (getModel() != null) {
			getModel().removePropertyChangeListener(this);
			SpeciesContext structSpeciesContext[] = getModel().getSpeciesContexts();
			if (structSpeciesContext != null) {
				for (int i = 0; i < structSpeciesContext.length; i++) {
					structSpeciesContext[i].removePropertyChangeListener(this);
					structSpeciesContext[i].getSpecies().removePropertyChangeListener(this);
				}
			}
			ReactionStep reactionSteps[] = getModel().getReactionSteps();
			if (reactionSteps != null) {
				for (int i = 0; i < reactionSteps.length; i++) {
					reactionSteps[i].removePropertyChangeListener(this);
				}
			}
		}
	}

	@Override
	public void paint(Graphics2D g, GraphPane canvas) {
		super.paint(g, canvas);
	}
	
//	private static Integer getStructureLevel(Structure s) {
//	Structure s0 = s;
//	int level = 0;
//	while (s0 != null) {
//		level += 1;
//		s0 = s0.getParentStructure();
//	}
//	return level;
//}

	private void relistenToMolecule(PropertyChangeEvent event) {
		//
		// TODO: properly listen to the model for molecular type list events (or individual molecular type events?)
		// when a molecule is modified we need to correct the species patterns in the signatures!!!
		//
		if(event.getPropertyName().equals(Model.RbmModelContainer.PROPERTY_NAME_MOLECULAR_TYPE_LIST)) {
			System.out.println("relistenToMolecule, " + event.getSource().getClass() + ": " + event.getPropertyName());
			for(MolecularType mt : getModel().getRbmModelContainer().getMolecularTypeList()) {
				mt.removePropertyChangeListener(this);
				mt.addPropertyChangeListener(this);
			}
		} else if(event.getSource() instanceof MolecularType) {
			MolecularType mt = (MolecularType)event.getSource();
			System.out.println("relistenToMolecule " + mt + ", " + event.getPropertyName() + ": " + event.getOldValue() + " -> " + event.getNewValue());
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		relistenToMolecule(event);
		refreshAll();
	}
	
	@Override
	public void refreshAll() {
		refreshAll(false);
	}

	public void setStructureSuite(StructureSuite structureSuite) {
		if(this.structureSuite != null){
			cleanupAll();
		}
		this.structureSuite = structureSuite;
		refreshAll();
	}

}

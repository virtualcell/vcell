package cbit.vcell.graph;

/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */
import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphEvent;
import cbit.gui.graph.GraphPane;
import cbit.gui.graph.Shape;
import cbit.vcell.graph.structures.StructureSuite;
import cbit.vcell.model.Catalyst;
import cbit.vcell.model.Diagram;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Flux;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.NodeReference;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;

import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

public class ReactionCartoon extends ModelCartoon {

	protected StructureSuite structureSuite = null;

	public ReactionCartoon() {
		setResizable(false);
	}

	public StructureSuite getStructureSuite() {
		return structureSuite;
	}

	public void applyDefaults(Diagram diagram) {
		List<NodeReference> nodeList = diagram.getNodeList();
		for (int i = 0; i < nodeList.size(); i++) {
			NodeReference node = nodeList.get(i);
			Object obj = null;
			switch (node.nodeType) {
			case NodeReference.SIMPLE_REACTION_NODE: {
				obj = getModel().getReactionStep(node.name);
				if (!(obj instanceof SimpleReaction)) {
					System.out
							.println("ReactionCartoon.applyDefaults(), diagram reaction "
									+ node.name
									+ " type mismatch in model, using location anyway");
				}
				break;
			}
			case NodeReference.FLUX_REACTION_NODE: {
				obj = getModel().getReactionStep(node.name);
				if (!(obj instanceof FluxReaction)) {
					System.out
							.println("ReactionCartoon.applyDefaults(), diagram flux "
									+ node.name
									+ " type mismatch in model, using location anyway");
				}
				break;
			}
			case NodeReference.SPECIES_CONTEXT_NODE: {
				obj = getModel().getSpeciesContext(node.name);
				break;
			}
			}
			Shape shape = getShapeFromModelObject(obj);
			if (shape != null) {
				shape.getSpaceManager().setRelPos(node.location);
			}
		}
	}

	public void cleanupAll() {
		if (getStructureSuite() != null) {
			for(Structure structure : getStructureSuite().getStructures()) {
				structure.removePropertyChangeListener(this);
				if (structure instanceof Membrane) {
					Membrane membrane = (Membrane) structure;
					if (membrane.getMembraneVoltage() != null) {
						membrane.getMembraneVoltage().removePropertyChangeListener(
								this);
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
					structSpeciesContext[i].getSpecies()
							.removePropertyChangeListener(this);
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

	public void paint(Graphics2D g, GraphPane canvas) {
		super.paint(g, canvas);
		setRandomLayout(false);
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		refreshAll();
	}

	@Override
	public void refreshAll() {
		try {
			if (getModel() == null || getStructureSuite() == null) {
				return;
			}
			for(Shape shape : getShapes()) {
				shape.setDirty(true);
			}
			ContainerContainerShape containerShape = (ContainerContainerShape) getShapeFromModelObject(getModel());
			List<ReactionContainerShape> reactionContainerShapeList = 
				new ArrayList<ReactionContainerShape>();
			// create all ReactionContainerShapes (one for each Structure)
			for(Structure structure : getStructureSuite().getStructures()) {
				if (structure instanceof Membrane) {
					Membrane membrane = (Membrane) structure;
					ReactionContainerShape membraneShape = 
						(ReactionContainerShape) getShapeFromModelObject(membrane);
					if (membraneShape == null) {
						membraneShape = new ReactionContainerShape(membrane, this);
						addShape(membraneShape);
						membrane.removePropertyChangeListener(this);
						membrane.addPropertyChangeListener(this);
						membrane.getMembraneVoltage().removePropertyChangeListener(this);
						membrane.getMembraneVoltage().addPropertyChangeListener(this);
					}
					membraneShape.refreshLabel();
					membraneShape.setDirty(false);
					reactionContainerShapeList.add(membraneShape);
				}else if (structure instanceof Feature){
					Feature feature = (Feature) structure;
					ReactionContainerShape featureShape = 
						(ReactionContainerShape) getShapeFromModelObject(feature);
					if (featureShape == null) {
						featureShape = new ReactionContainerShape(feature, this);
						addShape(featureShape);
						feature.removePropertyChangeListener(this);
						feature.addPropertyChangeListener(this);
					}
					featureShape.refreshLabel();
					featureShape.setDirty(false);
					reactionContainerShapeList.add(featureShape);
				}
			}
			if (containerShape == null) {
				containerShape = new ContainerContainerShape(this, getModel(), reactionContainerShapeList);
				addShape(containerShape);
			} else {
				containerShape.setReactionContainerShapeList(reactionContainerShapeList);
			}
			containerShape.refreshLabel();
			containerShape.setDirty(false);
			// add all species context shapes within the structures
			for(Structure structure : getStructureSuite().getStructures()) {
				ReactionContainerShape reactionContainerShape = 
					(ReactionContainerShape) getShapeFromModelObject(structure);
				for(SpeciesContext structSpeciesContext : getModel().getSpeciesContexts(structure)) {
					SpeciesContextShape ss = 
						(SpeciesContextShape) getShapeFromModelObject(structSpeciesContext);
					if (ss == null) {
						ss = new SpeciesContextShape(structSpeciesContext, this);
						ss.truncateLabelName(false);
						structSpeciesContext.removePropertyChangeListener(this);
						structSpeciesContext.addPropertyChangeListener(this);
						structSpeciesContext.getSpecies().removePropertyChangeListener(this);
						structSpeciesContext.getSpecies().addPropertyChangeListener(this);
						reactionContainerShape.addChildShape(ss);
						addShape(ss);
						ss.getSpaceManager().setRelPos(reactionContainerShape.getRandomPosition());
					}
					ss.refreshLabel();
					ss.setDirty(false);
				}
			}
			// add all reactionSteps that are in this structure (ReactionContainerShape), and draw the lines
			getModel().removePropertyChangeListener(this);
			getModel().addPropertyChangeListener(this);
			for(ReactionStep reactionStep : getModel().getReactionSteps()) {
				Structure structure = reactionStep.getStructure();
				if(getStructureSuite().areReactionsShownFor(structure)) {
					ReactionContainerShape reactionContainerShape = 
						(ReactionContainerShape) getShapeFromModelObject(structure);
					if(reactionContainerShape == null) {
						System.out.println("Reaction container shape is null for structure " + structure +
								" for reaction step " + reactionStep);
					}
					ReactionStepShape reactionStepShape =
						(ReactionStepShape) getShapeFromModelObject(reactionStep);
					if (reactionStepShape == null) {
						if (reactionStep instanceof SimpleReaction) {
							reactionStepShape = new SimpleReactionShape(
									(SimpleReaction) reactionStep, this);
						} else if (reactionStep instanceof FluxReaction) {
							reactionStepShape = new FluxReactionShape(
									(FluxReaction) reactionStep, this);
						} else {
							throw new RuntimeException(
									"unknown type of ReactionStep '"
									+ reactionStep.getClass()
									.toString());
						}
						addShape(reactionStepShape);
						reactionStep.addPropertyChangeListener(this);
						reactionStepShape.getSpaceManager().setRelPos(
								reactionContainerShape.getRandomPosition());
						reactionContainerShape.addChildShape(reactionStepShape);
					}
					reactionStepShape.refreshLabel();
					reactionStepShape.setDirty(false);
					// add reaction participants as edges
					for(ReactionParticipant participant : reactionStep.getReactionParticipants()) {
						Structure speciesStructure = participant.getStructure();
						Structure reactionStructure = reactionStep.getStructure();
						if(getStructureSuite().getStructures().contains(speciesStructure) &&
								getStructureSuite().areReactionsShownFor(reactionStructure)) {
							SpeciesContext speciesContext = 
								getModel().getSpeciesContext(participant.getSpecies(),
										speciesStructure);
							// add speciesContextShapes that are not in this structure, but are referenced from the reactionParticipants
							// these are only when reactionParticipants are from features that are outside of the membrane being displayed
							SpeciesContextShape speciesContextShape = 
								(SpeciesContextShape) getShapeFromModelObject(speciesContext);
							if (speciesContextShape == null) {
								speciesContextShape = new SpeciesContextShape(
										speciesContext, this);
								speciesContextShape.truncateLabelName(false);
								reactionContainerShape.addChildShape(speciesContextShape);
								addShape(speciesContextShape);
								speciesContextShape.getSpaceManager().setRelPos(
										reactionContainerShape.getRandomPosition());
							}
							speciesContextShape.refreshLabel();
							speciesContextShape.setDirty(false);
							ReactionParticipantShape reactionParticipantShape = 
								(ReactionParticipantShape) getShapeFromModelObject(participant);
							if (reactionParticipantShape == null) {
								if (participant instanceof Reactant) {
									reactionParticipantShape = 
										new ReactantShape((Reactant) participant, reactionStepShape, 
												speciesContextShape, this);
								} else if (participant instanceof Product) {
									reactionParticipantShape = 
										new ProductShape((Product) participant, reactionStepShape, 
												speciesContextShape, this);
								} else if (participant instanceof Catalyst) {
									reactionParticipantShape = 
										new CatalystShape((Catalyst) participant, reactionStepShape, 
												speciesContextShape, this);
								} else if (participant instanceof Flux) {
									reactionParticipantShape = 
										new FluxShape((Flux) participant, reactionStepShape, speciesContextShape, 
												this);
								} else {
									throw new RuntimeException("unsupported ReactionParticipant " + 
											participant.getClass());
								}
								addShape(reactionParticipantShape);
							}
							if(!containerShape.getChildren().contains(reactionParticipantShape)) {
								containerShape.addChildShape(reactionParticipantShape);								
							}
							reactionParticipantShape.setDirty(false);
							reactionParticipantShape.refreshLabel();
						}
					}
				}
			}
			List<Shape> deleteList = new ArrayList<Shape>();
			for(Shape shape : getShapes()) {
				if (shape.isDirty()) {
					deleteList.add(shape);
				}
			}
			for(Shape shape : deleteList) {
				removeShape(shape);
			}
			// update diagrams
			for(Structure structure : structureSuite.getStructures()) {
				Diagram diagram = getModel().getDiagram(structure);
				if (diagram != null) {
					applyDefaults(diagram);
				}				
			}
			fireGraphChanged(new GraphEvent(this));
		} catch (Throwable e) {
			handleException(e);
		}
	}

	public void setPositionsFromReactionCartoon(Diagram diagram) {
		List<NodeReference> nodeList = new ArrayList<NodeReference>();
		for(Shape shape : getShapes()) {
			if (shape instanceof FluxReactionShape) {
				nodeList.add(new NodeReference(
						NodeReference.FLUX_REACTION_NODE, ((FluxReaction) shape.getModelObject()).getName(), 
						shape.getSpaceManager().getRelPos()));
			} else if (shape instanceof SimpleReactionShape) {
				nodeList.add(new NodeReference(
						NodeReference.SIMPLE_REACTION_NODE,
						((ReactionStep) shape.getModelObject()).getName(),
						shape.getSpaceManager().getRelPos()));
			} else if (shape instanceof SpeciesContextShape) {
				nodeList.add(new NodeReference(
						NodeReference.SPECIES_CONTEXT_NODE,
						((SpeciesContext) shape.getModelObject()).getName(),
						shape.getSpaceManager().getRelPos()));
			}
		}
		diagram.setNodeReferences((NodeReference[]) nodeList.toArray(new NodeReference[0]));
	}

	public void setRandomLayout(boolean bRandomize) {
		try {
			// assert random characteristics
			Shape topShape = getTopShape();
			if (topShape instanceof ContainerShape) {
				ContainerShape containerShape = (ContainerShape) topShape;
				containerShape.setRandomLayout(bRandomize);
			}
		} catch (Exception e){
			System.out.println("top shape not found");
			e.printStackTrace(System.out);
		}
	}

	public void setStructureSuite(StructureSuite structureSuite) {
		if(this.structureSuite != null){
			cleanupAll();
		}
		this.structureSuite = structureSuite;
		refreshAll();
	}

}
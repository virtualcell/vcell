package cbit.vcell.graph;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cbit.gui.graph.GraphEvent;
import cbit.gui.graph.Shape;
import cbit.vcell.model.Catalyst;
import cbit.vcell.model.Diagram;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.GroupingCriteria;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.NodeReference;
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

public class ReactionCartoonMolecule extends ReactionCartoon {

	@Override
	protected GroupingCriteria getRuleParticipantGroupingCriteria() {
		return GroupingCriteria.molecule;
	}

	// for the RULE_PARTICIPANT_SIGNATURE_NODE nodes we initialize the node's speciesPattern field 
	// from the matching signature
	public void rebindAll(Diagram diagram) {
		String nodeStructure = diagram.getStructure().getName();
		List<NodeReference> nodeList = diagram.getNodeMoleculeList();
		for (int i = 0; i < nodeList.size(); i++) {
			NodeReference node = nodeList.get(i);
			if(node.nodeType == NodeReference.RULE_PARTICIPANT_SIGNATURE_SHORT_NODE) {

				if(node.speciesPattern != null) {
					continue;
				}
				for(RuleParticipantSignature signature : ruleParticipantSignatures) {
					if(!signature.getStructure().getName().equals(nodeStructure)) {
						continue;
					}
					String speciesPatternName = signature.getFirstSpeciesPatternAsString();
					if(speciesPatternName.equals(node.name)) {
						node.speciesPattern = signature.getSpeciesPattern();
						break;
					}
				}
			} else if(node.nodeType == NodeReference.RULE_PARTICIPANT_SIGNATURE_FULL_NODE) {
				System.out.println("ReactionCartoonMolecule, rebindAll(), wrong NodeReference type RULE_PARTICIPANT_SIGNATURE_FULL_NODE");
			}
		}
	}
	
	public void applyDefaults(Diagram diagram) {
		List<NodeReference> nodeList = diagram.getNodeMoleculeList();
		List<NodeReference> orphansList = new ArrayList<NodeReference> ();

		for (int i = 0; i < nodeList.size(); i++) {
			NodeReference node = nodeList.get(i);
			Object obj = null;
			Structure struct = diagram.getStructure();
			boolean found = false;
			switch (node.nodeType) {
			case NodeReference.SIMPLE_REACTION_NODE:
				obj = getModel().getReactionStep(node.name);
				if (!(obj instanceof SimpleReaction)) {
					System.out
							.println("ReactionCartoon.applyDefaults(), diagram reaction "
									+ node.name
									+ " type mismatch in model, using location anyway");
				}
				break;
			case NodeReference.FLUX_REACTION_NODE:
				obj = getModel().getReactionStep(node.name);
				if (!(obj instanceof FluxReaction)) {
					System.out
							.println("ReactionCartoon.applyDefaults(), diagram flux "
									+ node.name
									+ " type mismatch in model, using location anyway");
				}
				break;
			case NodeReference.SPECIES_CONTEXT_NODE:
				obj = getModel().getSpeciesContext(node.name);
				break;
			case NodeReference.REACTION_RULE_NODE:
				obj = getModel().getRbmModelContainer().getReactionRule(node.name);
				break;
			case NodeReference.RULE_PARTICIPANT_SIGNATURE_FULL_NODE:		// obj is a RuleParticipantSignature
				System.out.println("ReactionCartoonMolecule, RULE_PARTICIPANT_SIGNATURE_FULL_NODE detected");
				for(RuleParticipantSignature signature : ruleParticipantSignatures) {
					if (signature instanceof RuleParticipantLongSignature && signature.getStructure() == struct && signature.compareByCriteria(node.getName(), GroupingCriteria.full)){
						obj = signature;
						found = true;
						break;
					}
				}
				if(!found) {
					orphansList.add(node);
				}
				break;
			case NodeReference.RULE_PARTICIPANT_SIGNATURE_SHORT_NODE:
				for(RuleParticipantSignature signature : ruleParticipantSignatures) {
					if (signature instanceof RuleParticipantShortSignature && signature.getStructure() == struct && signature.compareByCriteria(node.getName(), GroupingCriteria.full)){
						obj = signature;
						found = true;
						break;
					}
				}
				if(!found) {
					orphansList.add(node);
				}
				break;
			}		// -- switch
			Shape shape = getShapeFromModelObject(obj);
			if (shape != null) {
				Point relPosOld = shape.getRelPos();
				Point relPosNew = node.location;
				// In old models, the same node can appear in multiple diagrams.
				// Now, we have only one diagram, so if a node has multiple positions,
				// some would overwrite others.
				// This attempts to prevent overwriting a position with a worse one.
//				if(relPosOld.x + relPosOld.y < relPosNew.x + relPosNew.y) {
					shape.setRelPos(relPosNew);					
//				}
			}
		}
		if(!orphansList.isEmpty()) {
			diagram.removeNodeReferences(NodeReference.Mode.molecule, orphansList);
		}
	}

	public void setPositionsFromReactionCartoon(Diagram diagram) {
		List<NodeReference> nodeList = new ArrayList<NodeReference>();
		NodeReference.Mode mode = NodeReference.Mode.molecule;
		for(Shape shape : getShapes()) {
			if (shape instanceof FluxReactionShape) {
				nodeList.add(new NodeReference(mode,
						NodeReference.FLUX_REACTION_NODE, ((FluxReaction) shape.getModelObject()).getName(), 
						shape.getSpaceManager().getRelPos()));
			} else if (shape instanceof SimpleReactionShape) {
				nodeList.add(new NodeReference(mode,
						NodeReference.SIMPLE_REACTION_NODE,
						((ReactionStep) shape.getModelObject()).getName(),
						shape.getSpaceManager().getRelPos()));
			} else if (shape instanceof ReactionRuleDiagramShape) {
				nodeList.add(new NodeReference(mode,
						NodeReference.REACTION_RULE_NODE,
						((ReactionRule) shape.getModelObject()).getName(),
						shape.getSpaceManager().getRelPos()));
			} else if (shape instanceof SpeciesContextShape) {
				nodeList.add(new NodeReference(mode,
						NodeReference.SPECIES_CONTEXT_NODE,
						((SpeciesContext) shape.getModelObject()).getName(),
						shape.getSpaceManager().getRelPos()));
			} else if (shape instanceof RuleParticipantSignatureFullDiagramShape) {
				System.out.println("ReactionCartoonMolecule, Invalid shape type 'RuleParticipantSignatureFullDiagramShape'");
				RuleParticipantSignature ruleParticipantSignature = (RuleParticipantSignature) shape.getModelObject();
				if (ruleParticipantSignature.getStructure() == diagram.getStructure()){
					String spAsString = ruleParticipantSignature.getFirstSpeciesPatternAsString();
					NodeReference nr = new NodeReference(mode, NodeReference.RULE_PARTICIPANT_SIGNATURE_FULL_NODE, spAsString, shape.getSpaceManager().getRelPos());
					nr.speciesPattern = ruleParticipantSignature.getSpeciesPattern();
					nodeList.add(nr);
				}
			} else if (shape instanceof RuleParticipantSignatureShortDiagramShape) {
				RuleParticipantSignature ruleParticipantSignature = (RuleParticipantSignature) shape.getModelObject();
				if (ruleParticipantSignature.getStructure() == diagram.getStructure()){
					String spAsString = ruleParticipantSignature.getFirstSpeciesPatternAsString();
					NodeReference nr = new NodeReference(mode, NodeReference.RULE_PARTICIPANT_SIGNATURE_SHORT_NODE, spAsString, shape.getSpaceManager().getRelPos());
					nr.speciesPattern = ruleParticipantSignature.getSpeciesPattern();
					nodeList.add(nr);
				}
			}
		}
		diagram.setNodeReferences(mode, nodeList);
	}

	@Override
	protected void refreshAll(boolean reallocateShapes) {
		try {
			if (getModel() == null || getStructureSuite() == null) {
				return;
			}
			System.out.println("ReactionCartoonMolecule, RefreshAll()");
			for(Structure structure : structureSuite.getStructures()) {
				Diagram diagram = getModel().getDiagram(structure);
				if (diagram != null) {
					// Maintain consistency between rule participant nodes, signatures and 
					// species pattern when a molecule is being modified.
					rebindAll(diagram);
				}				
			}
			// calculate species context weight (number of reactions for which it's a participant)
			Map<SpeciesContext, Integer> scWeightMap = new HashMap<>();
			Set<SpeciesContext> scCatalystSet = new HashSet<>();			// all the species contexts that are catalysts
			// calculate species context length (number of species patterns it contains, 1 if has no species patterns)
			for(ReactionStep rs : getModel().getReactionSteps()) {
				ReactionParticipant[] rpList = rs.getReactionParticipants();
				for(int i=0; i<rpList.length; i++) {
					ReactionParticipant rp = rpList[i];
					SpeciesContext sc = rp.getSpeciesContext();
//					int increment = rp.getStoichiometry();
					int increment = 1;
					if(rp instanceof Catalyst) {
						scCatalystSet.add(sc);
					}
					if(scWeightMap.containsKey(sc)) {
						int weight = scWeightMap.get(sc);
						weight += increment;
						scWeightMap.put(sc, weight);
					} else {
						scWeightMap.put(sc, increment);
					}
				}
			}

			Set<Shape> unwantedShapes = new HashSet<Shape>();
			Set<RuleParticipantSignature> unwantedSignatures = new HashSet<RuleParticipantSignature>();
			unwantedShapes.addAll(getShapes());
			unwantedSignatures.addAll(ruleParticipantSignatures);
			ContainerContainerShape containerShape = (ContainerContainerShape) getShapeFromModelObject(getModel());
			List<ReactionContainerShape> reactionContainerShapeList = 
				new ArrayList<ReactionContainerShape>();
			List<Structure> structureList = new ArrayList<Structure>(getStructureSuite().getStructures());

			// create all ReactionContainerShapes (one for each Structure)
			for(Structure structure : structureList) {
				if (structure instanceof Membrane) {
					Membrane membrane = (Membrane) structure;
					ReactionContainerShape membraneShape = 
						(ReactionContainerShape) getShapeFromModelObject(membrane);
					if (membraneShape == null) {
						membraneShape = new ReactionContainerShape(membrane, structureSuite, this);
						addShape(membraneShape);
						membrane.getMembraneVoltage().removePropertyChangeListener(this);
						membrane.getMembraneVoltage().addPropertyChangeListener(this);
					} else {
						membraneShape.setStructureSuite(structureSuite);
					}
					membrane.removePropertyChangeListener(this);
					membrane.addPropertyChangeListener(this);
					membraneShape.refreshLabel();
					unwantedShapes.remove(membraneShape);
					reactionContainerShapeList.add(membraneShape);
				}else if (structure instanceof Feature){
					Feature feature = (Feature) structure;
					ReactionContainerShape featureShape = (ReactionContainerShape) getShapeFromModelObject(feature);
					if (featureShape == null) {
						featureShape = new ReactionContainerShape(feature, structureSuite, this);
						addShape(featureShape);
					} else {
						featureShape.setStructureSuite(structureSuite);
					}
					feature.removePropertyChangeListener(this);
					feature.addPropertyChangeListener(this);
					featureShape.refreshLabel();
					unwantedShapes.remove(featureShape);
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
			unwantedShapes.remove(containerShape);
			// add all species context shapes within the structures
			for(Structure structure : getStructureSuite().getStructures()) {
				ReactionContainerShape reactionContainerShape =	(ReactionContainerShape) getShapeFromModelObject(structure);
				structure.removePropertyChangeListener(this);
				structure.addPropertyChangeListener(this);
				for(SpeciesContext structSpeciesContext : getModel().getSpeciesContexts(structure)) {
					SpeciesContextShape ss = (SpeciesContextShape) getShapeFromModelObject(structSpeciesContext);
					if (ss == null) {
						ss = new SpeciesContextShape(structSpeciesContext, this);
						ss.truncateLabelName(false);
						structSpeciesContext.getSpecies().removePropertyChangeListener(this);
						structSpeciesContext.getSpecies().addPropertyChangeListener(this);
						reactionContainerShape.addChildShape(ss);
						addShape(ss);
						ss.getSpaceManager().setRelPos(reactionContainerShape.getRandomPosition());
					}
					if(speciesSizeOption == SpeciesSizeOptions.weight) {
						Integer weight = scWeightMap.get(structSpeciesContext);		// this number sets the diameter of the shape
						weight = Math.min(weight, 16);		// we cap the diameter of the shape to something reasonable
						ss.setFilters(highlightCatalystOption ? scCatalystSet.contains(structSpeciesContext) : false, weight);
					} else if(speciesSizeOption == SpeciesSizeOptions.length) {
						Integer length = null;
						if(structSpeciesContext.getSpeciesPattern() != null && !structSpeciesContext.getSpeciesPattern().getMolecularTypePatterns().isEmpty()) {
							length = structSpeciesContext.getSpeciesPattern().getMolecularTypePatterns().size() * 2;
							length = Math.min(length, 16);
						}
						ss.setFilters(highlightCatalystOption ? scCatalystSet.contains(structSpeciesContext) : false, length);
					} else {
						ss.setFilters(highlightCatalystOption ? scCatalystSet.contains(structSpeciesContext) : false, null);
					}
					structSpeciesContext.removePropertyChangeListener(this);
					structSpeciesContext.addPropertyChangeListener(this);
					ss.refreshLabel();
					unwantedShapes.remove(ss);
				}
			}
			// add all reactionSteps that are in this structure (ReactionContainerShape), and draw the lines
			getModel().removePropertyChangeListener(this);
			getModel().addPropertyChangeListener(this);
			//
			// =================================== Rules ================================================
			//
			for(ReactionRule rr : getModel().getRbmModelContainer().getReactionRuleList()) {
				rr.removePropertyChangeListener(this);
				rr.addPropertyChangeListener(this);
				Structure structure = rr.getStructure();
				if(getStructureSuite().areReactionsShownFor(structure)) {
					ReactionContainerShape reactionContainerShape =	(ReactionContainerShape) getShapeFromModelObject(structure);
					ReactionRuleFullDiagramShape rrShape = (ReactionRuleFullDiagramShape) getShapeFromModelObject(rr);
					if (rrShape == null) {
						rrShape = new ReactionRuleFullDiagramShape(rr, this);
						addShape(rrShape);
						rrShape.getSpaceManager().setRelPos(reactionContainerShape.getRandomPosition());
						reactionContainerShape.addChildShape(rrShape);
						rrShape.getSpaceManager().setRelPos(reactionContainerShape.getRandomPosition());
					}
					rrShape.refreshLabel();
					unwantedShapes.remove(rrShape);
					
					//
					// add reaction participants as edges and SignatureShapes as needed
					//
					List<ReactionRuleParticipant> participants = rr.getReactionRuleParticipants();
					List<RuleParticipantEdgeDiagramShape> ruleEdges = new ArrayList<> ();
					for(ReactionRuleParticipant participant : participants) {
						participant.getSpeciesPattern().removePropertyChangeListener(this);
						participant.getSpeciesPattern().addPropertyChangeListener(this);
						Structure speciesStructure = participant.getStructure();
						Structure reactionStructure = rr.getStructure();
						
						if(getStructureSuite().getStructures().contains(speciesStructure) && getStructureSuite().areReactionsShownFor(reactionStructure)) {
							//
							// find existing RuleParticipantSignatureShape in cartoon
							//
							RuleParticipantShortSignature ruleParticipantShortSignature = null;
							for (RuleParticipantSignature signature : ruleParticipantSignatures) {
								if (signature instanceof RuleParticipantLongSignature && signature.getStructure() == participant.getStructure()) {
									System.out.println("ReactionCartoonMolecule, refreshAll(), RuleParticipantLongSignature");
									break;
								}
							}
							for (RuleParticipantSignature signature : ruleParticipantSignatures) {
								if (signature instanceof RuleParticipantShortSignature && signature.getStructure() == participant.getStructure() && 
										signature.compareByCriteria(participant.getSpeciesPattern(), GroupingCriteria.molecule)) {
									ruleParticipantShortSignature = (RuleParticipantShortSignature)signature;
									break;
								}
							}
							//
							// if didn't find signature in cartoons list of signatures, then create one (and create a shape for it).
							//
							RuleParticipantSignatureShortDiagramShape signatureShape = null;
							if (ruleParticipantShortSignature == null) {
								ruleParticipantShortSignature = RuleParticipantShortSignature.fromReactionRuleParticipant(participant, this);
								ruleParticipantSignatures.add(ruleParticipantShortSignature);
								signatureShape = new RuleParticipantSignatureShortDiagramShape(ruleParticipantShortSignature, this);
								addShape(signatureShape);
								ReactionContainerShape participantContainerShape =	(ReactionContainerShape) getShapeFromModelObject(participant.getStructure());
								signatureShape.getSpaceManager().setRelPos(participantContainerShape.getRandomPosition());
								participantContainerShape.addChildShape(signatureShape);
								signatureShape.getSpaceManager().setRelPos(participantContainerShape.getRandomPosition());
							} else {
								signatureShape = (RuleParticipantSignatureShortDiagramShape) getShapeFromModelObject(ruleParticipantShortSignature);
							}

							unwantedShapes.remove(signatureShape);
							unwantedSignatures.remove(ruleParticipantShortSignature);
							signatureShape.refreshLabel();
							signatureShape.setVisible(true);

							//
							// add edge for ReactionRuleParticipant if not already present.
							//
							RuleParticipantEdgeDiagramShape ruleParticipantShape = (RuleParticipantEdgeDiagramShape) getShapeFromModelObject(participant);
							if (ruleParticipantShape == null || ruleParticipantShape.getRuleParticipantSignatureShape() != signatureShape) {
								if (participant instanceof ReactantPattern && signatureShape.isVisible()) {
									ruleParticipantShape = new ReactantPatternEdgeDiagramShape((ReactantPattern) participant, rrShape, signatureShape, this);
								} else if (participant instanceof ProductPattern && signatureShape.isVisible()) {
									ruleParticipantShape = new ProductPatternEdgeDiagramShape((ProductPattern) participant, rrShape, signatureShape, this);
								} else {
									throw new RuntimeException("unsupported ReactionRuleParticipant " + participant.getClass());
								}
								addShape(ruleParticipantShape);
							}
							if(!containerShape.getChildren().contains(ruleParticipantShape)) {
								containerShape.addChildShape(ruleParticipantShape);								
							}
							unwantedShapes.remove(ruleParticipantShape);
							ruleParticipantShape.refreshLabel();
							ruleEdges.add(ruleParticipantShape);	// all the edges for this rule
						}
					}
					// now let's see if any reactant and product pair have the same signature - means we need to draw a reactant and 
					// a product edge (a closed loop) between the rule diagram shape and the signature diagram shape
					for(RuleParticipantEdgeDiagramShape ours : ruleEdges) {
						ours.setSibling(false);		// reset them all
					}
					for(RuleParticipantEdgeDiagramShape ours : ruleEdges) {
						for(RuleParticipantEdgeDiagramShape theirs : ruleEdges) {
							if(ours == theirs) {
								continue;			// don't compare with self
							}
							if(ours.getRuleParticipantSignatureShape() == theirs.getRuleParticipantSignatureShape()) {
								ours.setSibling(true);
								theirs.setSibling(true);
							}
						}
					}
				}
			}
			ruleParticipantSignatures.removeAll(unwantedSignatures);
			
			//TODO: uncomment the following block to track the instances of participants and signatures during transitions
//			String msg1 = transitioning ? "transitioning to " : "staying ";
//			msg1 += ruleParticipantGroupingCriteria == RuleParticipantSignature.Criteria.full ? "full" : "short";
//			System.out.println(" --------------------------------- " + msg1 + " ------------------------");
//			for (RuleParticipantSignature signature : ruleParticipantSignatures) {
//				String sSign = RbmUtils.toBnglString(signature.getSpeciesPattern(), null, CompartmentMode.hide, 0);
//				String msg2 = "sign ";
//				msg2 += signature instanceof RuleParticipantLongSignature ? "full:  " : "short: ";
//				String hashSignature = System.identityHashCode(signature) + "";
//				String hashSP = System.identityHashCode(signature.getSpeciesPattern()) + "";
//				System.out.println(msg2 + sSign + ", sp hash: " + hashSP + ", signature hash: " + hashSignature);
//			}
//			for(ReactionRule rr : getModel().getRbmModelContainer().getReactionRuleList()) {
//				for(ReactionRuleParticipant participant : rr.getReactionRuleParticipants()) {
//					String hashParticipant = System.identityHashCode(participant) + "";
//					String hashSP = System.identityHashCode(participant.getSpeciesPattern()) + "";
//					String sPart = RbmUtils.toBnglString(participant.getSpeciesPattern(), null, CompartmentMode.hide, 0);
//					System.out.println("part: " + sPart + ", sp hash: " + hashSP + ", participant hash: " + hashParticipant);
//				}
//			}
			
			for(ReactionStep reactionStep : getModel().getReactionSteps()) {
				reactionStep.removePropertyChangeListener(this);
				reactionStep.addPropertyChangeListener(this);
				Structure structure = reactionStep.getStructure();
				if(getStructureSuite().areReactionsShownFor(structure)) {
					ReactionContainerShape reactionContainerShape =	(ReactionContainerShape) getShapeFromModelObject(structure);
					if(reactionContainerShape == null) {
						System.out.println("Reaction container shape is null for structure " + structure +
								" for reaction step " + reactionStep);
					}
					ReactionStepShape reactionStepShape = (ReactionStepShape) getShapeFromModelObject(reactionStep);
					if (reactionStepShape == null) {
						if (reactionStep instanceof SimpleReaction) {
							reactionStepShape = new SimpleReactionShape((SimpleReaction) reactionStep, this);
						} else if (reactionStep instanceof FluxReaction) {
							reactionStepShape = new FluxReactionShape((FluxReaction) reactionStep, this);
						} else {
							throw new RuntimeException("unknown type of ReactionStep '"	+ reactionStep.getClass().toString());
						}
						addShape(reactionStepShape);
						reactionStepShape.getSpaceManager().setRelPos(reactionContainerShape.getRandomPosition());
						reactionContainerShape.addChildShape(reactionStepShape);
						reactionStepShape.getSpaceManager().setRelPos(reactionContainerShape.getRandomPosition());
					}
					reactionStepShape.refreshLabel();
					unwantedShapes.remove(reactionStepShape);
					// add reaction participants as edges
					for(ReactionParticipant participant : reactionStep.getReactionParticipants()) {
						participant.removePropertyChangeListener(this);
						participant.addPropertyChangeListener(this);
						Structure speciesStructure = participant.getStructure();
						Structure reactionStructure = reactionStep.getStructure();
						if(getStructureSuite().getStructures().contains(speciesStructure) &&
								getStructureSuite().areReactionsShownFor(reactionStructure)) {
							SpeciesContext speciesContext = getModel().getSpeciesContext(participant.getSpecies(), speciesStructure);
							// add speciesContextShapes that are not in this structure, but are referenced from the reactionParticipants
							// these are only when reactionParticipants are from features that are outside of the membrane being displayed
							SpeciesContextShape speciesContextShape = 
								(SpeciesContextShape) getShapeFromModelObject(speciesContext);
							if (speciesContextShape == null) {
								speciesContextShape = new SpeciesContextShape(speciesContext, this);
								speciesContextShape.truncateLabelName(false);
								reactionContainerShape.addChildShape(speciesContextShape);
								addShape(speciesContextShape);
								speciesContextShape.getSpaceManager().setRelPos(reactionContainerShape.getRandomPosition());
							}
							speciesContextShape.refreshLabel();
							unwantedShapes.remove(speciesContextShape);
							ReactionParticipantShape reactionParticipantShape = (ReactionParticipantShape) getShapeFromModelObject(participant);
							if (reactionParticipantShape == null) {
								if (participant instanceof Reactant) {
									reactionParticipantShape = new ReactantShape((Reactant) participant, reactionStepShape, speciesContextShape, this);
								} else if (participant instanceof Product) {
									reactionParticipantShape = new ProductShape((Product) participant, reactionStepShape, speciesContextShape, this);
								} else if (participant instanceof Catalyst) {
									reactionParticipantShape = new CatalystShape((Catalyst) participant, reactionStepShape, speciesContextShape, this);
								} else {
									throw new RuntimeException("unsupported ReactionParticipant " + participant.getClass());
								}
								addShape(reactionParticipantShape);
							}
							if(!containerShape.getChildren().contains(reactionParticipantShape)) {
								containerShape.addChildShape(reactionParticipantShape);								
							}
							unwantedShapes.remove(reactionParticipantShape);
							reactionParticipantShape.refreshLabel();
						}
					}
				}
			}
			for(Shape unwantedShape : unwantedShapes) { removeShape(unwantedShape); }
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
}

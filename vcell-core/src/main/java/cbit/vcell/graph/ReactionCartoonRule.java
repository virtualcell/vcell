package cbit.vcell.graph;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cbit.gui.graph.Shape;
import cbit.vcell.model.Diagram;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.GroupingCriteria;
import cbit.vcell.model.NodeReference;
import cbit.vcell.model.ReactionRuleShortSignature;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.RuleParticipantLongSignature;
import cbit.vcell.model.RuleParticipantShortSignature;
import cbit.vcell.model.RuleParticipantSignature;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;

public class ReactionCartoonRule extends ReactionCartoon {

	private Set<ReactionRuleShortSignature> reactionRuleShortSignatures = new HashSet<>();

	@Override
	protected GroupingCriteria getRuleParticipantGroupingCriteria() {
		return GroupingCriteria.rule;
	}

	// for the RULE_PARTICIPANT_SIGNATURE_NODE nodes we initialize the node's speciesPattern field 
	// from the matching signature
	@Override
	public void rebindAll(Diagram diagram) {
//		String nodeStructure = diagram.getStructure().getName();
//		List<NodeReference> nodeList = diagram.getNodeRuleList();
//		for (int i = 0; i < nodeList.size(); i++) {
//			NodeReference node = nodeList.get(i);
//			// for rule grouping criteria, the participants must be in short format (same as for molecule grouping criteria)
//			if(node.nodeType == NodeReference.RULE_PARTICIPANT_SIGNATURE_SHORT_NODE) {
//
//				if(node.speciesPattern != null) {
//					continue;
//				}
//				for(RuleParticipantSignature signature : ruleParticipantSignatures) {
//					if(!signature.getStructure().getName().equals(nodeStructure)) {
//						continue;
//					}
//					String speciesPatternName = signature.getFirstSpeciesPatternAsString();
//					if(speciesPatternName.equals(node.name)) {
//						node.speciesPattern = signature.getSpeciesPattern();
//						break;
//					}
//				}
//			} else if(node.nodeType == NodeReference.RULE_PARTICIPANT_SIGNATURE_FULL_NODE) {
//				System.out.println("ReactionCartoonRule, rebindAll(), wrong NodeReference type RULE_PARTICIPANT_SIGNATURE_FULL_NODE");
//			}
//		}
	}
	
	
	public void applyDefaults(Diagram diagram) {
		List<NodeReference> nodeList = diagram.getNodeRuleList();
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
			case NodeReference.REACTION_RULE_NODE:		// TODO: aici
				obj = getModel().getRbmModelContainer().getReactionRule(node.name);
				break;
			case NodeReference.RULE_PARTICIPANT_SIGNATURE_FULL_NODE:		// obj is a RuleParticipantSignature
				System.out.println("ReactionCartoonRule, RULE_PARTICIPANT_SIGNATURE_FULL_NODE detected");
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
			diagram.removeNodeReferences(NodeReference.Mode.rule, orphansList);
		}
	}

	public void setPositionsFromReactionCartoon(Diagram diagram) {
		List<NodeReference> nodeList = new ArrayList<NodeReference>();
		NodeReference.Mode mode = NodeReference.Mode.rule;
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
//			} else if (shape instanceof ReactionRuleDiagramShape) {
//				nodeList.add(new NodeReference(mode,
//						NodeReference.REACTION_RULE_NODE,
//						((ReactionRule) shape.getModelObject()).getName(),
//						shape.getSpaceManager().getRelPos()));
			} else if (shape instanceof ReactionRuleShortDiagramShape) {	// can only be short
				
				ReactionRuleShortSignature signature = (ReactionRuleShortSignature)shape.getModelObject();
				String name = signature.getDisplayName();	// the display name is the number of rules in the signature's list of rules
				NodeReference nr = new NodeReference(mode, NodeReference.REACTION_RULE_NODE, name, shape.getSpaceManager().getRelPos());
				nodeList.add(nr);

			} else if (shape instanceof SpeciesContextShape) {
				nodeList.add(new NodeReference(mode,
						NodeReference.SPECIES_CONTEXT_NODE,
						((SpeciesContext) shape.getModelObject()).getName(),
						shape.getSpaceManager().getRelPos()));
			} else if (shape instanceof RuleParticipantSignatureFullDiagramShape) {
				System.out.println("ReactionCartoonRule, Invalid shape type 'RuleParticipantSignatureFullDiagramShape'");
				RuleParticipantSignature ruleParticipantSignature = (RuleParticipantSignature) shape.getModelObject();
				if (ruleParticipantSignature.getStructure() == diagram.getStructure()) {
					String spAsString = ruleParticipantSignature.getFirstSpeciesPatternAsString();
					NodeReference nr = new NodeReference(mode, NodeReference.RULE_PARTICIPANT_SIGNATURE_FULL_NODE, spAsString, shape.getSpaceManager().getRelPos());
					nr.speciesPattern = ruleParticipantSignature.getSpeciesPattern();
					nodeList.add(nr);
				}
			} else if (shape instanceof RuleParticipantSignatureShortDiagramShape) {
				RuleParticipantSignature ruleParticipantSignature = (RuleParticipantSignature) shape.getModelObject();
				if (ruleParticipantSignature.getStructure() == diagram.getStructure()) {
					String spAsString = ruleParticipantSignature.getFirstSpeciesPatternAsString();
					NodeReference nr = new NodeReference(mode, NodeReference.RULE_PARTICIPANT_SIGNATURE_SHORT_NODE, spAsString, shape.getSpaceManager().getRelPos());
					nr.speciesPattern = ruleParticipantSignature.getSpeciesPattern();
					nodeList.add(nr);
				}
			}
		}
		diagram.setNodeReferences(mode, nodeList);	// add all to nodeRuleList
	}
	
	@Override
	protected void refreshAll(boolean reallocateShapes) {
		try {
			if (getModel() == null || getStructureSuite() == null) {
				return;
			}
			System.out.println("ReactionCartoonRule, RefreshAll()");
			for(Structure structure : structureSuite.getStructures()) {
				Diagram diagram = getModel().getDiagram(structure);
				if (diagram != null) {
					// Maintain consistency between rule participant nodes, signatures and 
					// species pattern when a molecule is being modified.
					rebindAll(diagram);
				}				
			}
			Set<Shape> unwantedShapes = new HashSet<Shape>();
			Set<RuleParticipantSignature> unwantedSignatures = new HashSet<RuleParticipantSignature>();
			Set<ReactionRuleShortSignature> unwantedRuleSignatures = new HashSet<ReactionRuleShortSignature>();
			unwantedShapes.addAll(getShapes());
			unwantedSignatures.addAll(ruleParticipantSignatures);
			unwantedRuleSignatures.addAll(reactionRuleShortSignatures);
//			ContainerContainerShape containerShape = (ContainerContainerShape) getShapeFromModelObject(getModel());
//			List<ReactionContainerShape> reactionContainerShapeList = new ArrayList<ReactionContainerShape>();
//			List<Structure> structureList = new ArrayList<Structure>(getStructureSuite().getStructures());
//
//			// create all ReactionContainerShapes (one for each Structure)
//			for(Structure structure : structureList) {
//				if (structure instanceof Membrane) {
//					Membrane membrane = (Membrane) structure;
//					ReactionContainerShape membraneShape = 
//						(ReactionContainerShape) getShapeFromModelObject(membrane);
//					if (membraneShape == null) {
//						membraneShape = new ReactionContainerShape(membrane, structureSuite, this);
//						addShape(membraneShape);
//						membrane.getMembraneVoltage().removePropertyChangeListener(this);
//						membrane.getMembraneVoltage().addPropertyChangeListener(this);
//					} else {
//						membraneShape.setStructureSuite(structureSuite);
//					}
//					membrane.removePropertyChangeListener(this);
//					membrane.addPropertyChangeListener(this);
//					membraneShape.refreshLabel();
//					unwantedShapes.remove(membraneShape);
//					reactionContainerShapeList.add(membraneShape);
//				}else if (structure instanceof Feature){
//					Feature feature = (Feature) structure;
//					ReactionContainerShape featureShape = (ReactionContainerShape) getShapeFromModelObject(feature);
//					if (featureShape == null) {
//						featureShape = new ReactionContainerShape(feature, structureSuite, this);
//						addShape(featureShape);
//					} else {
//						featureShape.setStructureSuite(structureSuite);
//					}
//					feature.removePropertyChangeListener(this);
//					feature.addPropertyChangeListener(this);
//					featureShape.refreshLabel();
//					unwantedShapes.remove(featureShape);
//					reactionContainerShapeList.add(featureShape);
//				}
//			}
//			if (containerShape == null) {
//				containerShape = new ContainerContainerShape(this, getModel(), reactionContainerShapeList);
//				addShape(containerShape);
//			} else {
//				containerShape.setReactionContainerShapeList(reactionContainerShapeList);
//			}
//			containerShape.refreshLabel();
//			unwantedShapes.remove(containerShape);
//			// add all species context shapes within the structures
//			for(Structure structure : getStructureSuite().getStructures()) {
//				ReactionContainerShape reactionContainerShape =	(ReactionContainerShape) getShapeFromModelObject(structure);
//				structure.removePropertyChangeListener(this);
//				structure.addPropertyChangeListener(this);
//				for(SpeciesContext structSpeciesContext : getModel().getSpeciesContexts(structure)) {
//					SpeciesContextShape ss = (SpeciesContextShape) getShapeFromModelObject(structSpeciesContext);
//					if (ss == null) {
//						ss = new SpeciesContextShape(structSpeciesContext, this);
//						ss.truncateLabelName(false);
//						structSpeciesContext.getSpecies().removePropertyChangeListener(this);
//						structSpeciesContext.getSpecies().addPropertyChangeListener(this);
//						reactionContainerShape.addChildShape(ss);
//						addShape(ss);
//						ss.getSpaceManager().setRelPos(reactionContainerShape.getRandomPosition());
//					}
//					structSpeciesContext.removePropertyChangeListener(this);
//					structSpeciesContext.addPropertyChangeListener(this);
//					ss.refreshLabel();
//					unwantedShapes.remove(ss);
//				}
//			}
//			// add all reactionSteps that are in this structure (ReactionContainerShape), and draw the lines
//			getModel().removePropertyChangeListener(this);
//			getModel().addPropertyChangeListener(this);
//			//
//			// =================================== Rules ================================================
//			//
//			for(ReactionRule rr : getModel().getRbmModelContainer().getReactionRuleList()) {
//				rr.removePropertyChangeListener(this);
//				rr.addPropertyChangeListener(this);
//				Structure structure = rr.getStructure();
//				if(getStructureSuite().areReactionsShownFor(structure)) {
//					ReactionContainerShape reactionContainerShape =	(ReactionContainerShape) getShapeFromModelObject(structure);
//					
//					ReactionRuleShortSignature ruleSignature = null;
//					int matches = 0;	// any rule must be part of one and only one ReactionRuleShortSignature object
//					for(ReactionRuleShortSignature rrss : reactionRuleShortSignatures) {
//						if(rrss.compareBySignature(rr)) {
//							ruleSignature = rrss;
//							matches++;
//// 							break;		// once this code gets stable uncomment the break and remove the matches counter
//						} else {
//							// remove the rule if it's in the list even though the signature doesn't match
//							rrss.remove(rr);
//						}
//					}
//					if(matches > 1) {
//						throw new RuntimeException("Rule " + rr.getDisplayName() + " found in multiple ReactionRuleShortSignatures");
//					}
//					
//					ReactionRuleShortDiagramShape rrShape = null;
//					if(ruleSignature == null) {
//						ruleSignature = new ReactionRuleShortSignature(rr);
//						reactionRuleShortSignatures.add(ruleSignature);
//						rrShape = new ReactionRuleShortDiagramShape(ruleSignature, this);
//						addShape(rrShape);
//						rrShape.getSpaceManager().setRelPos(reactionContainerShape.getRandomPosition());
//						reactionContainerShape.addChildShape(rrShape);
//						rrShape.getSpaceManager().setRelPos(reactionContainerShape.getRandomPosition());
//					} else {
//						ruleSignature.add(rr);	// add the rule if it's not there
//						rrShape = (ReactionRuleShortDiagramShape) getShapeFromModelObject(ruleSignature);
//					}
//					
//					rrShape.refreshLabel();
//					unwantedShapes.remove(rrShape);
//					unwantedRuleSignatures.remove(ruleSignature);
//					if(matches != 0) {
//						continue;		// don't make other shapes or edges for this rule, the rule signature already has them from a previous rule
//					}
//					
//					//
//					// add reaction participants as edges and SignatureShapes as needed
//					//
//					List<ReactionRuleParticipant> participants = rr.getReactionRuleParticipants();
//					List<RuleParticipantEdgeDiagramShape> ruleEdges = new ArrayList<> ();
//					for(ReactionRuleParticipant participant : participants) {
//						participant.getSpeciesPattern().removePropertyChangeListener(this);
//						participant.getSpeciesPattern().addPropertyChangeListener(this);
//						Structure speciesStructure = participant.getStructure();
//						Structure reactionStructure = rr.getStructure();
//						
//						if(getStructureSuite().getStructures().contains(speciesStructure) && getStructureSuite().areReactionsShownFor(reactionStructure)) {
//							//
//							// find existing RuleParticipantSignatureShape in cartoon
//							//
//							RuleParticipantShortSignature ruleParticipantShortSignature = null;
//							for (RuleParticipantSignature signature : ruleParticipantSignatures) {
//								if (signature instanceof RuleParticipantLongSignature && signature.getStructure() == participant.getStructure()) {
//									System.out.println("ReactionCartoonMolecule, refreshAll(), RuleParticipantLongSignature");
//									break;
//								}
//							}
//							for (RuleParticipantSignature signature : ruleParticipantSignatures) {
//								if (signature instanceof RuleParticipantShortSignature && signature.getStructure() == participant.getStructure() && 
//										signature.compareByCriteria(participant.getSpeciesPattern(), GroupingCriteria.molecule)) {
//									ruleParticipantShortSignature = (RuleParticipantShortSignature)signature;
//									break;
//								}
//							}
//							//
//							// if didn't find signature in cartoons list of signatures, then create one (and create a shape for it).
//							//
//							RuleParticipantSignatureShortDiagramShape signatureShape = null;
//							if (ruleParticipantShortSignature == null) {
//								ruleParticipantShortSignature = RuleParticipantShortSignature.fromReactionRuleParticipant(participant, this);
//								ruleParticipantSignatures.add(ruleParticipantShortSignature);
//								signatureShape = new RuleParticipantSignatureShortDiagramShape(ruleParticipantShortSignature, this);
//								addShape(signatureShape);
//								ReactionContainerShape participantContainerShape =	(ReactionContainerShape) getShapeFromModelObject(participant.getStructure());
//								signatureShape.getSpaceManager().setRelPos(participantContainerShape.getRandomPosition());
//								participantContainerShape.addChildShape(signatureShape);
//								signatureShape.getSpaceManager().setRelPos(participantContainerShape.getRandomPosition());
//							} else {
//								signatureShape = (RuleParticipantSignatureShortDiagramShape) getShapeFromModelObject(ruleParticipantShortSignature);
//							}
//
//							unwantedShapes.remove(signatureShape);
//							unwantedSignatures.remove(ruleParticipantShortSignature);
//							signatureShape.refreshLabel();
//							signatureShape.setVisible(true);
//
//							//
//							// add edge for ReactionRuleParticipant if not already present.
//							//
//							RuleParticipantEdgeDiagramShape ruleParticipantShape = (RuleParticipantEdgeDiagramShape) getShapeFromModelObject(participant);
//							if (ruleParticipantShape == null || ruleParticipantShape.getRuleParticipantSignatureShape() != signatureShape) {
//								if (participant instanceof ReactantPattern && signatureShape.isVisible()) {
//									ruleParticipantShape = new ReactantPatternEdgeDiagramShape((ReactantPattern) participant, (ElipseShape)rrShape, signatureShape, this);
//								} else if (participant instanceof ProductPattern && signatureShape.isVisible()) {
//									ruleParticipantShape = new ProductPatternEdgeDiagramShape((ProductPattern) participant, (ElipseShape)rrShape, signatureShape, this);
//								} else {
//									throw new RuntimeException("unsupported ReactionRuleParticipant " + participant.getClass());
//								}
//								addShape(ruleParticipantShape);
//							}
//							if(!containerShape.getChildren().contains(ruleParticipantShape)) {
//								containerShape.addChildShape(ruleParticipantShape);								
//							}
//							unwantedShapes.remove(ruleParticipantShape);
//							ruleParticipantShape.refreshLabel();
//							ruleEdges.add(ruleParticipantShape);	// all the edges for this rule
//						}
//					}
//					// now let's see if any reactant and product pair have the same signature - means we need to draw a reactant and 
//					// a product edge (a closed loop) between the rule diagram shape and the signature diagram shape
//					for(RuleParticipantEdgeDiagramShape ours : ruleEdges) {
//						ours.setSibling(false);		// reset them all
//					}
//					for(RuleParticipantEdgeDiagramShape ours : ruleEdges) {
//						for(RuleParticipantEdgeDiagramShape theirs : ruleEdges) {
//							if(ours == theirs) {
//								continue;			// don't compare with self
//							}
//							if(ours.getRuleParticipantSignatureShape() == theirs.getRuleParticipantSignatureShape()) {
//								ours.setSibling(true);
//								theirs.setSibling(true);
//							}
//						}
//					}
//				}
//			}
			ruleParticipantSignatures.removeAll(unwantedSignatures);
			reactionRuleShortSignatures.removeAll(unwantedRuleSignatures);
//			
//			//TODO: uncomment the following block to track the instances of participants and signatures during transitions
////			String msg1 = transitioning ? "transitioning to " : "staying ";
////			msg1 += ruleParticipantGroupingCriteria == RuleParticipantSignature.Criteria.full ? "full" : "short";
////			System.out.println(" --------------------------------- " + msg1 + " ------------------------");
////			for (RuleParticipantSignature signature : ruleParticipantSignatures) {
////				String sSign = RbmUtils.toBnglString(signature.getSpeciesPattern(), null, CompartmentMode.hide, 0);
////				String msg2 = "sign ";
////				msg2 += signature instanceof RuleParticipantLongSignature ? "full:  " : "short: ";
////				String hashSignature = System.identityHashCode(signature) + "";
////				String hashSP = System.identityHashCode(signature.getSpeciesPattern()) + "";
////				System.out.println(msg2 + sSign + ", sp hash: " + hashSP + ", signature hash: " + hashSignature);
////			}
////			for(ReactionRule rr : getModel().getRbmModelContainer().getReactionRuleList()) {
////				for(ReactionRuleParticipant participant : rr.getReactionRuleParticipants()) {
////					String hashParticipant = System.identityHashCode(participant) + "";
////					String hashSP = System.identityHashCode(participant.getSpeciesPattern()) + "";
////					String sPart = RbmUtils.toBnglString(participant.getSpeciesPattern(), null, CompartmentMode.hide, 0);
////					System.out.println("part: " + sPart + ", sp hash: " + hashSP + ", participant hash: " + hashParticipant);
////				}
////			}
//			
//			for(ReactionStep reactionStep : getModel().getReactionSteps()) {
//				reactionStep.removePropertyChangeListener(this);
//				reactionStep.addPropertyChangeListener(this);
//				Structure structure = reactionStep.getStructure();
//				if(getStructureSuite().areReactionsShownFor(structure)) {
//					ReactionContainerShape reactionContainerShape =	(ReactionContainerShape) getShapeFromModelObject(structure);
//					if(reactionContainerShape == null) {
//						System.out.println("Reaction container shape is null for structure " + structure +
//								" for reaction step " + reactionStep);
//					}
//					ReactionStepShape reactionStepShape = (ReactionStepShape) getShapeFromModelObject(reactionStep);
//					if (reactionStepShape == null) {
//						if (reactionStep instanceof SimpleReaction) {
//							reactionStepShape = new SimpleReactionShape((SimpleReaction) reactionStep, this);
//						} else if (reactionStep instanceof FluxReaction) {
//							reactionStepShape = new FluxReactionShape((FluxReaction) reactionStep, this);
//						} else {
//							throw new RuntimeException("unknown type of ReactionStep '"	+ reactionStep.getClass().toString());
//						}
//						addShape(reactionStepShape);
//						reactionStepShape.getSpaceManager().setRelPos(reactionContainerShape.getRandomPosition());
//						reactionContainerShape.addChildShape(reactionStepShape);
//						reactionStepShape.getSpaceManager().setRelPos(reactionContainerShape.getRandomPosition());
//					}
//					reactionStepShape.refreshLabel();
//					unwantedShapes.remove(reactionStepShape);
//					// add reaction participants as edges
//					for(ReactionParticipant participant : reactionStep.getReactionParticipants()) {
//						participant.removePropertyChangeListener(this);
//						participant.addPropertyChangeListener(this);
//						Structure speciesStructure = participant.getStructure();
//						Structure reactionStructure = reactionStep.getStructure();
//						if(getStructureSuite().getStructures().contains(speciesStructure) &&
//								getStructureSuite().areReactionsShownFor(reactionStructure)) {
//							SpeciesContext speciesContext = getModel().getSpeciesContext(participant.getSpecies(), speciesStructure);
//							// add speciesContextShapes that are not in this structure, but are referenced from the reactionParticipants
//							// these are only when reactionParticipants are from features that are outside of the membrane being displayed
//							SpeciesContextShape speciesContextShape = 
//								(SpeciesContextShape) getShapeFromModelObject(speciesContext);
//							if (speciesContextShape == null) {
//								speciesContextShape = new SpeciesContextShape(speciesContext, this);
//								speciesContextShape.truncateLabelName(false);
//								reactionContainerShape.addChildShape(speciesContextShape);
//								addShape(speciesContextShape);
//								speciesContextShape.getSpaceManager().setRelPos(reactionContainerShape.getRandomPosition());
//							}
//							speciesContextShape.refreshLabel();
//							unwantedShapes.remove(speciesContextShape);
//							ReactionParticipantShape reactionParticipantShape = (ReactionParticipantShape) getShapeFromModelObject(participant);
//							if (reactionParticipantShape == null) {
//								if (participant instanceof Reactant) {
//									reactionParticipantShape = new ReactantShape((Reactant) participant, reactionStepShape, speciesContextShape, this);
//								} else if (participant instanceof Product) {
//									reactionParticipantShape = new ProductShape((Product) participant, reactionStepShape, speciesContextShape, this);
//								} else if (participant instanceof Catalyst) {
//									reactionParticipantShape = new CatalystShape((Catalyst) participant, reactionStepShape, speciesContextShape, this);
//								} else {
//									throw new RuntimeException("unsupported ReactionParticipant " + participant.getClass());
//								}
//								addShape(reactionParticipantShape);
//							}
//							if(!containerShape.getChildren().contains(reactionParticipantShape)) {
//								containerShape.addChildShape(reactionParticipantShape);								
//							}
//							unwantedShapes.remove(reactionParticipantShape);
//							reactionParticipantShape.refreshLabel();
//						}
//					}
//				}
//			}
			for(Shape unwantedShape : unwantedShapes) { removeShape(unwantedShape); }
//			// update diagrams
//			for(Structure structure : structureSuite.getStructures()) {
//				Diagram diagram = getModel().getDiagram(structure);
//				if (diagram != null) {
//					applyDefaults(diagram);
//				}				
//			}
//			fireGraphChanged(new GraphEvent(this));
		} catch (Throwable e) {
			handleException(e);
		}
	}
}

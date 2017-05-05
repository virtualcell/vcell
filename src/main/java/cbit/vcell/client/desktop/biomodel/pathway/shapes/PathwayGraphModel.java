/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel.pathway.shapes;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Complex;
import org.vcell.pathway.Control;
import org.vcell.pathway.Conversion;
import org.vcell.pathway.Dna;
import org.vcell.pathway.GroupNeighbor;
import org.vcell.pathway.GroupObject;
import org.vcell.pathway.Interaction;
import org.vcell.pathway.InteractionParticipant;
import org.vcell.pathway.MolecularInteraction;
import org.vcell.pathway.PathwayEvent;
import org.vcell.pathway.PathwayListener;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.pathway.Protein;
import org.vcell.pathway.Rna;
import org.vcell.pathway.SmallMolecule;
import org.vcell.relationship.RelationshipEvent;
import org.vcell.relationship.RelationshipListener;
import org.vcell.relationship.RelationshipObject;

import cbit.gui.graph.EdgeShape;
import cbit.gui.graph.GraphContainerLayoutPathways;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
import cbit.vcell.biomodel.BioModel;

public class PathwayGraphModel extends GraphModel implements PathwayListener, RelationshipListener {
	
	private PathwayModel pathwayModel;
	protected BioModel bioModel;
	protected Random random = new Random();
	
	private PathwayContainerShape pathwayContainerShape = null;
	private Set<Shape> unwantedShapes = null;

	public PathwayGraphModel() {
		this.containerLayout = new GraphContainerLayoutPathways();
	}
	
	public PathwayModel getPathwayModel() {
		return pathwayModel;
	}

	public void setPathwayModel(PathwayModel pathwayModel) {
		if (this.pathwayModel!=null){
			this.pathwayModel.removePathwayListener(this);
		}
		this.pathwayModel = pathwayModel;
		if (this.pathwayModel!=null){
			this.pathwayModel.addPathwayListener(this);
		}
		refreshAll();
	}
	
	public void setBioModel(BioModel bioModel) { 
		if(this.bioModel != null) {
			this.bioModel.getRelationshipModel().removeRelationShipListener(this);
		}
		this.bioModel = bioModel; 
		this.bioModel.getRelationshipModel().addRelationShipListener(this);
	}
	
	public BioModel getBioModel() { return bioModel; }

	@Override
	public void refreshAll() {
		if (pathwayModel == null){
			clearAllShapes();
			fireGraphChanged();
			return;
		}
		unwantedShapes = new HashSet<Shape>();
		unwantedShapes.addAll(getShapes());
		pathwayContainerShape = (PathwayContainerShape) getShapeFromModelObject(pathwayModel);
		if(pathwayContainerShape == null) {
			pathwayContainerShape = new PathwayContainerShape(this,pathwayModel);
			pathwayContainerShape.getSpaceManager().setSize(400, 300);
			addShape(pathwayContainerShape);			
		}
		unwantedShapes.remove(pathwayContainerShape);
		Set<BioPaxObject> bioPaxObjects = new HashSet<BioPaxObject>(pathwayModel.getDisplayableBioPaxObjectList());
		for (BioPaxObject bpObject : bioPaxObjects){
			BioPaxShape bpObjectShape = (BioPaxShape) getShapeFromModelObject(bpObject);
			if(bpObjectShape == null) {
				if(bpObject instanceof Conversion) {
					bpObjectShape = new BioPaxConversionShape((Conversion) bpObject, this);	
				} else if(bpObject instanceof MolecularInteraction) {
					bpObjectShape = new BioPaxMolecularInteractionShape((MolecularInteraction) bpObject, this);
				} else if(bpObject instanceof Protein) {
					bpObjectShape = new BioPaxProteinShape((Protein) bpObject, this);
				} else if(bpObject instanceof Complex) {
					bpObjectShape = new BioPaxComplexShape((Complex) bpObject, this);
				} else if(bpObject instanceof SmallMolecule) {
					bpObjectShape = new BioPaxSmallMoleculeShape((SmallMolecule) bpObject, this);
				} else if(bpObject instanceof Dna) {
					bpObjectShape = new BioPaxDnaShape((Dna) bpObject, this);
				} else if(bpObject instanceof Rna) {
					bpObjectShape = new BioPaxRnaShape((Rna) bpObject, this);
				} else if(bpObject instanceof PhysicalEntity) {
					bpObjectShape = new BioPaxPhysicalEntityShape((PhysicalEntity) bpObject, this);
				} else if(bpObject instanceof GroupObject) {
					bpObjectShape = new BioPaxGroupShape((GroupObject) bpObject, this);
				} else {
					bpObjectShape = new BioPaxObjectShape(bpObject, this);				
				}
				if(!(bpObject instanceof Control)){ // the Control objects will not be displayed on the diagram
					pathwayContainerShape.addChildShape(bpObjectShape);
					addShape(bpObjectShape);
				}
				Dimension shapeSize = bpObjectShape.getSpaceManager().getSize();
				Rectangle boundary = getContainerLayout().getBoundaryForAutomaticLayout(pathwayContainerShape);
				int xPos = boundary.x + random.nextInt(boundary.width - shapeSize.width);
				int yPos = boundary.y + random.nextInt(boundary.height - shapeSize.height);
				bpObjectShape.setAbsPos(xPos, yPos);
			}
			unwantedShapes.remove(bpObjectShape);
		}
		for (BioPaxObject bpObject : bioPaxObjects) {
			if (bpObject instanceof Conversion) {
				refreshInteraction((Conversion) bpObject);
			} else if (bpObject instanceof MolecularInteraction) {
				refreshInteraction((MolecularInteraction) bpObject);
			} else if (bpObject instanceof Control) {
				refreshControl((Control) bpObject);
			} else if (bpObject instanceof GroupObject){
				refreshGroupObject((GroupObject) bpObject);
			}
		}
		for(Shape unwantedShape : unwantedShapes) { 
			removeShape(unwantedShape); 
		}
		refreshRelationshipInfo();
		fireGraphChanged();
	}
	
	public void refreshRelationshipInfo() {
		for(Shape shape : getShapes()) {
			refreshRelationshipInfo(shape);
		}
	}
	
	private BioPaxGroupNeighborShape refreshGroup(PathwayContainerShape pathwayContainerShape, 
					BioPaxObject ancObject, BioPaxShape bioPaxShape, InteractionParticipant participant){
		if(ancObject == null) return null;
		if(ancObject instanceof GroupObject){
			GroupObject groupObject = (GroupObject)ancObject;
			Shape shapeG = getShapeFromModelObject(groupObject);
			if(shapeG instanceof BioPaxGroupShape) {
				BioPaxGroupShape groupShape = (BioPaxGroupShape) shapeG;
				if(bioPaxShape instanceof BioPaxInteractionShape){
					BioPaxInteractionShape interactionShape = (BioPaxInteractionShape)bioPaxShape;
					GroupNeighbor groupNeighbor = new GroupNeighbor(groupObject, interactionShape.getInteraction(), participant.getType());
					BioPaxGroupNeighborShape neighborShape = 
						(BioPaxGroupNeighborShape) getShapeFromModelObject(groupNeighbor);
					if(neighborShape == null) {
						neighborShape = new BioPaxGroupNeighborShape(groupNeighbor,interactionShape, groupShape, this);
						pathwayContainerShape.addChildShape(neighborShape);
						addShape(neighborShape);
					}
					return neighborShape;
				}else if(bioPaxShape instanceof BioPaxGroupShape){
					BioPaxGroupShape groupConversionShape = (BioPaxGroupShape) bioPaxShape;
					GroupNeighbor groupNeighbor = new GroupNeighbor(groupConversionShape.getGroupObject(), groupObject, participant.getType());
					BioPaxGroupNeighborShape neighborShape = 
						(BioPaxGroupNeighborShape) getShapeFromModelObject(groupNeighbor);
					if(neighborShape == null) {
						neighborShape = new BioPaxGroupNeighborShape(groupNeighbor,groupConversionShape, groupShape, this);
						pathwayContainerShape.addChildShape(neighborShape);
						addShape(neighborShape);
					}
					return neighborShape;
				}
				
			}
		}
		return null;
	}
	
	private void removeEdgeShape(EdgeShape shape){
		PhysicalEntity physicalEntity = null;
		if(shape instanceof BioPaxInteractionParticipantShape){
			BioPaxInteractionParticipantShape edgeShape = (BioPaxInteractionParticipantShape)shape;
			physicalEntity = ((InteractionParticipant)edgeShape.getModelObject()).getPhysicalEntity();
		}else if(shape instanceof BioPaxGroupNeighborShape){
			BioPaxGroupNeighborShape edgeShape = (BioPaxGroupNeighborShape) shape;
			BioPaxObject bpObject = ((GroupNeighbor) edgeShape.getModelObject()).getNeighbor();
			if(bpObject instanceof PhysicalEntity){
				physicalEntity = (PhysicalEntity) bpObject;
			}
		}
		if(physicalEntity == null) return;
		if(	! pathwayModel.getDisplayableBioPaxObjectList().contains(physicalEntity)){
			removeShape(shape);
		}
	}

	protected void refreshRelationshipInfo(Shape shape) {
		if(shape instanceof BioPaxShape) {
			BioPaxShape bpShape = (BioPaxShape) shape;
			BioPaxObject bpObject = bpShape.getBioPaxObject();
			boolean hasRelationships = false;
			if(bioModel != null) {
				HashSet<RelationshipObject> relationships = 
					bioModel.getRelationshipModel().getRelationshipObjects(bpObject);
				if(relationships.size() > 0) {
					hasRelationships = true;
				}
			}
			bpShape.setHasRelationships(hasRelationships);
		}
	}
	
	public void pathwayChanged(PathwayEvent event) {
		refreshAll();
	}

	public void relationshipChanged(RelationshipEvent event) {
		Shape shape = getShapeFromModelObject(event.getRelationshipObject());
		refreshRelationshipInfo(shape);
		refreshAll();
	}
	
	private void refreshInteraction(Interaction interaction){
		BioPaxInteractionShape interactionShape = 
			(BioPaxInteractionShape) getShapeFromModelObject(interaction);
		BioPaxObject ancestorObject = pathwayModel.findTopLevelGroupAncestor(interaction);
		if(ancestorObject == interaction){ // conversion was not grouped
			for(InteractionParticipant participant : interaction.getParticipants()) {
				refreshParticipant(interactionShape, participant);
			}
		}else{
			if(ancestorObject instanceof GroupObject){// conversion has been grouped
				GroupObject groupObject = (GroupObject) ancestorObject;
				for(InteractionParticipant participant : interaction.getParticipants()) {
					refreshGroupInteraction(groupObject, participant);
				}
			}
		}
	}
	
	private void refreshParticipant(BioPaxInteractionShape interactionShape, InteractionParticipant participant){
		BioPaxInteractionParticipantShape edgeShape = (BioPaxInteractionParticipantShape) getShapeFromModelObject(participant);
		PhysicalEntity physicalEntity = participant.getPhysicalEntity();
		BioPaxObject ancestorObject = pathwayModel.findTopLevelGroupAncestor(physicalEntity);
		if(edgeShape == null) {
			Shape shape = getShapeFromModelObject(physicalEntity);
			if(shape instanceof BioPaxPhysicalEntityShape) {
				BioPaxPhysicalEntityShape physicalEntityShape = 
					(BioPaxPhysicalEntityShape) shape;
				edgeShape = 
					new BioPaxInteractionParticipantShape(participant, 
							interactionShape, physicalEntityShape, this);
				pathwayContainerShape.addChildShape(edgeShape);
				addShape(edgeShape);
			}	
		}else{
			// edges without end objects will be removed
			if(ancestorObject != physicalEntity){
				removeEdgeShape(edgeShape);
			}
		}
		unwantedShapes.remove(refreshGroup(pathwayContainerShape, ancestorObject, interactionShape, participant));
		unwantedShapes.remove(edgeShape);
	}
	
	private void refreshGroupInteraction(GroupObject groupObject, InteractionParticipant participant){
		BioPaxGroupShape groupShape = (BioPaxGroupShape)getShapeFromModelObject(groupObject);
		PhysicalEntity physicalEntity = participant.getPhysicalEntity();
		GroupNeighbor groupNeighbor = new GroupNeighbor(groupObject, physicalEntity, participant.getType());
		BioPaxGroupNeighborShape edgeShape = (BioPaxGroupNeighborShape) getShapeFromModelObject(groupNeighbor);
		BioPaxObject ancestorObject = pathwayModel.findTopLevelGroupAncestor(physicalEntity);					
		if(edgeShape == null) {
			Shape shape = getShapeFromModelObject(physicalEntity);
			if(shape instanceof BioPaxPhysicalEntityShape) {
				BioPaxPhysicalEntityShape physicalEntityShape = (BioPaxPhysicalEntityShape) shape;
				edgeShape = new BioPaxGroupNeighborShape(groupNeighbor, groupShape, physicalEntityShape, this);
				pathwayContainerShape.addChildShape(edgeShape);
				addShape(edgeShape);
			}						
		}else{
			// edges without end objects will be removed
			if(ancestorObject != physicalEntity){// for grouped objects
				removeEdgeShape(edgeShape);
			}
		}
		unwantedShapes.remove(refreshGroup(pathwayContainerShape, ancestorObject, groupShape, participant));
		unwantedShapes.remove(edgeShape);
	}

	private void refreshControl(Control control){
		Interaction controlledInteraction = control.getControlledInteraction();
		if(controlledInteraction instanceof Conversion) {
			List<InteractionParticipant> physicalControllers = control.getParticipants();
			if(physicalControllers != null) {
				Conversion conversion = (Conversion) controlledInteraction;
				BioPaxObject ancestorObject = pathwayModel.findTopLevelGroupAncestor(conversion);
				if(ancestorObject == conversion){ // conversion was not grouped
					BioPaxConversionShape conversionShape = (BioPaxConversionShape) getShapeFromModelObject(conversion);
					if(conversionShape != null){
						for(InteractionParticipant participant : physicalControllers) {
							refreshParticipant(conversionShape, participant);
						}
					}
				}else{
					if(ancestorObject instanceof GroupObject){// conversion has been grouped
						GroupObject groupObject = (GroupObject) ancestorObject;
						for(InteractionParticipant participant : physicalControllers) {
							refreshGroupInteraction(groupObject, participant);
						}
					}
				}
					
			}
		}
	}
	
	private void refreshGroupObject(GroupObject groupObject){
		for(BioPaxObject bpObject : groupObject.getGroupedObjects()){
			if(bpObject instanceof Conversion){ //  Conversions inside groupObject
				Conversion conversion = (Conversion) bpObject;
				refreshInteraction(conversion);
			}else if(bpObject instanceof MolecularInteraction){ // molecularInteraction inside  groupObject
				MolecularInteraction molecularInteraction = (MolecularInteraction) bpObject;
				refreshInteraction(molecularInteraction);
			}else if(bpObject instanceof GroupObject){ // groupObject inside another groupObject
				refreshGroupObject((GroupObject) bpObject);
			}
		}
	}
}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel.pathway;

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
import org.vcell.pathway.Interaction;
import org.vcell.pathway.InteractionParticipant;
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

import cbit.gui.graph.GraphContainerLayoutPathways;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.BioPaxComplexShape;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.BioPaxConversionShape;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.BioPaxDnaShape;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.BioPaxInteractionParticipantShape;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.BioPaxObjectShape;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.BioPaxPhysicalEntityShape;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.BioPaxProteinShape;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.BioPaxRnaShape;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.BioPaxShape;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.BioPaxSmallMoleculeShape;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.PathwayContainerShape;

public class PathwayGraphModel extends GraphModel implements PathwayListener, RelationshipListener {
	
	private PathwayModel pathwayModel;
	protected BioModel bioModel;
	protected Random random = new Random();

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
		Set<Shape> unwantedShapes = new HashSet<Shape>();
		unwantedShapes.addAll(getShapes());
		PathwayContainerShape pathwayContainerShape = 
			(PathwayContainerShape) getShapeFromModelObject(pathwayModel);
		if(pathwayContainerShape == null) {
			pathwayContainerShape = new PathwayContainerShape(this,pathwayModel);
			pathwayContainerShape.getSpaceManager().setSize(400, 300);
			addShape(pathwayContainerShape);			
		}
		unwantedShapes.remove(pathwayContainerShape);
		for (BioPaxObject bpObject : pathwayModel.getBiopaxObjects()){
			BioPaxShape bpObjectShape = (BioPaxShape) getShapeFromModelObject(bpObject);
			if(bpObjectShape == null) {
				if(bpObject instanceof Conversion) {
					bpObjectShape = new BioPaxConversionShape((Conversion) bpObject, this);				
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
		for (BioPaxObject bpObject : pathwayModel.getBiopaxObjects()) {
			if (bpObject instanceof Conversion) {
				Conversion conversion = (Conversion) bpObject;
				BioPaxConversionShape conversionShape = 
					(BioPaxConversionShape) getShapeFromModelObject(conversion);
				for(InteractionParticipant participant : conversion.getParticipants()) {
					BioPaxInteractionParticipantShape edgeShape = 
						(BioPaxInteractionParticipantShape) getShapeFromModelObject(participant);
					if(edgeShape == null) {
						PhysicalEntity physicalEntity = participant.getPhysicalEntity();
						Shape shape = getShapeFromModelObject(physicalEntity);
						if(shape instanceof BioPaxPhysicalEntityShape) {
							BioPaxPhysicalEntityShape physicalEntityShape = (BioPaxPhysicalEntityShape) shape;
							edgeShape = 
								new BioPaxInteractionParticipantShape(participant,
										conversionShape, physicalEntityShape, 
										this);
							pathwayContainerShape.addChildShape(edgeShape);
							addShape(edgeShape);
						}
					}
					unwantedShapes.remove(edgeShape);
				}
			} else if (bpObject instanceof Control) {			
				Control control = (Control) bpObject;
				Interaction controlledInteraction = control.getControlledInteraction();
				if(controlledInteraction instanceof Conversion) {
					List<InteractionParticipant> physicalControllers = control.getParticipants();
					if(physicalControllers != null) {
						Conversion conversion = (Conversion) controlledInteraction;
						BioPaxConversionShape conversionShape = 
							(BioPaxConversionShape) getShapeFromModelObject(conversion);
						if(conversionShape instanceof BioPaxConversionShape) {
							for(InteractionParticipant participant : physicalControllers) {
								BioPaxInteractionParticipantShape edgeShape = 
									(BioPaxInteractionParticipantShape) 
									getShapeFromModelObject(participant);
								if(edgeShape == null) {
									PhysicalEntity physicalEntity = participant.getPhysicalEntity();
									Shape shape = getShapeFromModelObject(physicalEntity);
									if(shape instanceof BioPaxPhysicalEntityShape) {
										BioPaxPhysicalEntityShape physicalEntityShape = 
											(BioPaxPhysicalEntityShape) shape;
										edgeShape = 
											new BioPaxInteractionParticipantShape(participant, 
													conversionShape, physicalEntityShape, this);
										pathwayContainerShape.addChildShape(edgeShape);
										addShape(edgeShape);
									}
								}
								unwantedShapes.remove(edgeShape);
							}							
						}
					}
				}
			}
		}
		for(Shape unwantedShape : unwantedShapes) { removeShape(unwantedShape); }
		refreshRelationshipInfo();
		fireGraphChanged();
	}
	
	public void refreshRelationshipInfo() {
		for(Shape shape : getShapes()) {
			refreshRelationshipInfo(shape);
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

}

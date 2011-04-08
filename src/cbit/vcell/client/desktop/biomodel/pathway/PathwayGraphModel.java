package cbit.vcell.client.desktop.biomodel.pathway;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import java.util.Random;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Catalysis;
import org.vcell.pathway.Control;
import org.vcell.pathway.Conversion;
import org.vcell.pathway.Interaction;
import org.vcell.pathway.InteractionParticipant;
import org.vcell.pathway.PathwayEvent;
import org.vcell.pathway.PathwayListener;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.PhysicalEntity;

import cbit.gui.graph.GraphContainerLayoutPathways;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.BioPaxConversionShape;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.BioPaxInteractionParticipantShape;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.BioPaxObjectShape;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.BioPaxPhysicalEntityShape;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.BioPaxShape;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.PathwayContainerShape;

public class PathwayGraphModel extends GraphModel implements PathwayListener {
	
	private PathwayModel pathwayModel;
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

	@Override
	public void refreshAll() {
		if (pathwayModel == null){
			clearAllShapes();
			fireGraphChanged();
			return;
		}
		objectShapeMap.clear();
		PathwayContainerShape pathwayContainerShape = new PathwayContainerShape(this,pathwayModel);
		// TODO What size?
		pathwayContainerShape.getSpaceManager().setSize(400, 300);
		for (BioPaxObject bpObject : pathwayModel.getBiopaxObjects()){
			BioPaxShape bpObjectShape;
			if(bpObject instanceof Conversion) {
				bpObjectShape = new BioPaxConversionShape((Conversion) bpObject, this);				
			} else if(bpObject instanceof PhysicalEntity) {
				bpObjectShape = new BioPaxPhysicalEntityShape((PhysicalEntity) bpObject, this);
			} else {
				bpObjectShape = new BioPaxObjectShape(bpObject, this);				
			}
			if(!(bpObject instanceof Catalysis)){ // the Catalysis objects will not be displayed on the diagram
				pathwayContainerShape.addChildShape(bpObjectShape);
				addShape(bpObjectShape);
			}
			Dimension shapeSize = bpObjectShape.getSpaceManager().getSize();
			Rectangle boundary = getContainerLayout().getBoundaryForAutomaticLayout(pathwayContainerShape);
			int xPos = boundary.x + random.nextInt(boundary.width - shapeSize.width);
			int yPos = boundary.y + random.nextInt(boundary.height - shapeSize.height);
			bpObjectShape.setAbsPos(xPos, yPos);
		}
		for (BioPaxObject bpObject : pathwayModel.getBiopaxObjects()) {
			if (bpObject instanceof Conversion) {
				Conversion conversion = (Conversion) bpObject;
				BioPaxConversionShape conversionShape = 
					(BioPaxConversionShape) getShapeFromModelObject(conversion);
				for(InteractionParticipant participant : conversion.getParticipants()) {
					PhysicalEntity physicalEntity = participant.getPhysicalEntity();
					Shape shape = getShapeFromModelObject(physicalEntity);
					if(shape instanceof BioPaxPhysicalEntityShape) {
						BioPaxPhysicalEntityShape physicalEntityShape = (BioPaxPhysicalEntityShape) shape;
						BioPaxInteractionParticipantShape edgeShape = 
							new BioPaxInteractionParticipantShape(participant,
									conversionShape, physicalEntityShape, 
									this);
						pathwayContainerShape.addChildShape(edgeShape);
						addShape(edgeShape);
					}
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
								PhysicalEntity physicalEntity = participant.getPhysicalEntity();
								Shape shape = getShapeFromModelObject(physicalEntity);
								if(shape instanceof BioPaxPhysicalEntityShape) {
									BioPaxPhysicalEntityShape physicalEntityShape = 
										(BioPaxPhysicalEntityShape) shape;
									BioPaxInteractionParticipantShape edgeShape = 
										new BioPaxInteractionParticipantShape(participant, conversionShape, 
												physicalEntityShape, this);
									pathwayContainerShape.addChildShape(edgeShape);
									addShape(edgeShape);
								}
							}
							
						}
					}
				}
				// TODO
			}
		}
		addShape(pathwayContainerShape);
		fireGraphChanged();
	}
	
	public void pathwayChanged(PathwayEvent event) {
		refreshAll();
	}

}

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

/*  A container shape for a reaction network, typically for one structure
 *  October 2010
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.EdgeShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
import cbit.vcell.graph.structures.StructureSuite;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Structure;

public class ReactionContainerShape extends ContainerShape {

	protected Structure structure = null;
	protected StructureSuite structureSuite;

	public ReactionContainerShape(Structure structure, StructureSuite structureSuite, 
			GraphModel graphModel) {
		super(graphModel);
		this.structure = structure;
		this.structureSuite = structureSuite;
		bNoFill = (structure instanceof Membrane?false:true);
		defaultFGselect = Color.red;
		defaultBG = Color.lightGray;
		defaultBGselect = Color.lightGray;
		backgroundColor = defaultBG;
	}

	public Structure getStructure() { return structure; }
	public void setStructureSuite(StructureSuite structureSuite) { this.structureSuite = structureSuite; }
	public StructureSuite getStructureSuite() { return structureSuite; }
	@Override public Object getModelObject() { return structure; }

	public void randomize() {
		// randomize the locations of speciesContexts and of reactionSteps,
		// then draw in the reactionParticipant edges
		for(Shape child : childShapeList) {
			if (child instanceof SpeciesContextShape || child instanceof ReactionStepShape
			 || child instanceof ReactionRuleDiagramShape || child instanceof RuleParticipantSignatureDiagramShape){
				// position normally about the center
				child.getSpaceManager().setRelPos(getRandomPosition());
			}	
		}
		// calculate locations and sizes of reactionParticipant edges
		for(Shape child : childShapeList) {
			if (child instanceof ReactionParticipantShape || child instanceof RuleParticipantEdgeDiagramShape){
				EdgeShape reactionParticipantShape = (EdgeShape)child;
				reactionParticipantShape.refreshLayoutSelf();
			}
		}
		// position label
		int centerX = getSpaceManager().getSize().width/2;
		int currentY = labelSize.height;
		labelPos.x = centerX - labelSize.width/2;
		labelPos.y = currentY;
		currentY += labelSize.height;	
	}

	@Override
	public void refreshLabel() {
		setLabel(getStructure().getName());
	}
	
	private static final BasicStroke dropTargetRectangleStroke = new BasicStroke(2, BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL, 1, new float[] {10f}, 0);
	private Boolean[] dropTargetEnable = new Boolean[2];
	private Boolean bAddStructureMode;
	public void setDropTargetEnableLow(Boolean bDropTargetEnableLow,Boolean bAddStructureMode){
		dropTargetEnable[0] = bDropTargetEnableLow;
		this.bAddStructureMode = bAddStructureMode;
	}
	public void setDropTargetEnableHigh(Boolean bDropTargetEnableHigh,Boolean bAddStructureMode){
		dropTargetEnable[1] = bDropTargetEnableHigh;
		this.bAddStructureMode = bAddStructureMode;
	}
	private boolean isOverDropTarget(int mouseX,int mouseY){
		for (int i = 0; i < dropTargetEnable.length; i++) {
			if(dropTargetEnable[i] != null){
				if(i==0){
					if(new Rectangle(getAbsX(), getAbsY(), 10, getSpaceManager().getSize().height).contains(mouseX, mouseY)){
						return true;
					}
				}else{
					if(new Rectangle(getAbsX()+getWidth()-10, getAbsY(), 10, getSpaceManager().getSize().height).contains(mouseX, mouseY)){
						return true;
					}
				}
			}
		}
		return false;
	}
	public String getSpecialToolTipText(Point unzoomedMousePos){
		Rectangle labelOutlineRectangle = getLabelOutline(getAbsX(),getAbsY());
		boolean bLabel = labelOutlineRectangle.contains(unzoomedMousePos.x, unzoomedMousePos.y);
		if(bLabel && !(bAddStructureMode != null && bAddStructureMode)){
			return "drag to MOVE '"+getLabel()+"'";
		}else if((bAddStructureMode != null && bAddStructureMode) && isOverDropTarget(unzoomedMousePos.x, unzoomedMousePos.y)){
			return "Click to insert structure...";
		}
		return getLabel();
	}
	@Override
	public void paintSelf(Graphics2D g, int absPosX, int absPosY) {
		super.paintSelf(g, absPosX, absPosY);
		for (int i = 0; i < dropTargetEnable.length; i++) {
			if(dropTargetEnable[i] != null){
				Stroke origStroke = g.getStroke();
				Color origColor = g.getColor();
				g.setStroke(dropTargetRectangleStroke);
				if(dropTargetEnable[i]){
					g.setColor(Color.GREEN);
				}
				if(i==0){
					g.drawRect(absPosX, absPosY, 10, getSpaceManager().getSize().height);
				}else{
					g.drawRect(absPosX+getWidth()-10, absPosY, 10, getSpaceManager().getSize().height);
				}
				g.setColor(origColor);
				g.setStroke(origStroke);			
			}
		}

	}
}

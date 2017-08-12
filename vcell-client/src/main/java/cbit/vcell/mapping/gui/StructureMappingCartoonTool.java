/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

import cbit.gui.graph.GraphModel;
import cbit.gui.graph.RubberBandEdgeShape;
import cbit.gui.graph.Shape;
import cbit.gui.graph.actions.CartoonToolEditActions;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.graph.GeometryClassLegendShape;
import cbit.vcell.graph.StructureMappingCartoon;
import cbit.vcell.graph.StructureMappingShape;
import cbit.vcell.graph.StructureShape;
import cbit.vcell.graph.gui.BioCartoonTool;
import cbit.vcell.mapping.IllegalMappingException;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.model.Structure;

public class StructureMappingCartoonTool extends BioCartoonTool {
	//
	private StructureMappingCartoon structureMappingCartoon = null;
	//
	private boolean bLineStretch = false;
	private Point endPoint = null;
	private RubberBandEdgeShape edgeShape = null;


	private Mode mode = null;

	private static final int LINE_TYPE_NULL = 0;
	private static final int LINE_TYPE_RESOLVED = 1;
	//	private static final int LINE_TYPE_DISTRIBUTED = 2;
	private static final String lineLabels[]  = {"<<?>>","<<R E S O L V E D>>","<<D I S T R I B U T E D>>"};
	private static final Color lineColors[]  = {Color.red, Color.black, Color.black};	
	private static final Cursor lineCursors[]  = {	Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR),
		Cursor.getPredefinedCursor(Cursor.HAND_CURSOR),
		Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)};

	public StructureMappingCartoonTool () {
		super();
	}

	public GraphModel getGraphModel() {
		return getStructureMappingCartoon();
	}

	private int getLineTypeFromWorld(Structure structure, Point worldPoint) {
		try {
			Shape mouseOverShape = getStructureMappingCartoon().pickWorld(worldPoint);
			if (mouseOverShape instanceof GeometryClassLegendShape){
				//			GeometryClass geometryClass = (GeometryClass)mouseOverShape.getModelObject();
				return LINE_TYPE_RESOLVED;
				//			if (getStructureMappingCartoon().getGeometryContext().isResolvedAllowed(feature,geometryClass)){
				//				return LINE_TYPE_RESOLVED;
				//			}else if (getStructureMappingCartoon().getGeometryContext().isDistributedAllowed(feature,geometryClass)){
				//				return LINE_TYPE_DISTRIBUTED;
				//			}else{
				//				return LINE_TYPE_NULL;
				//			}
			}else{
				return LINE_TYPE_NULL;
			}
		}catch (Exception e){
			return LINE_TYPE_NULL;
		}
	}

	public StructureMappingCartoon getStructureMappingCartoon() {
		return structureMappingCartoon;
	}

	protected void menuAction(Shape shape, String menuAction) {
		//
		if(shape == null){return;}
		//	
		if (menuAction.equals(CartoonToolEditActions.Delete.MENU_ACTION)){
			if (shape instanceof StructureMappingShape){
				try {
					StructureMapping sm = (StructureMapping)((StructureMappingShape)shape).getModelObject();
					getStructureMappingCartoon().getGeometryContext().assignStructure(sm.getStructure(),null);
					getStructureMappingCartoon().refreshAll();
				}catch (IllegalMappingException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(getGraphPane(), e.getMessage());
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(getGraphPane(), e.getMessage());
				} catch (MappingException e) {
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(getGraphPane(), e.getMessage());
				}
			}

		}else{
			// default action is to ignore
			System.out.println("unsupported menu action '"+menuAction+"' on shape '"+shape+"'");
		}

	}

	public void mouseClicked(java.awt.event.MouseEvent event) {
		// do nothing on compartmental
		if (getStructureMappingCartoon() == null ||
				getStructureMappingCartoon().getGeometryContext() == null ||
				getStructureMappingCartoon().getGeometryContext().getGeometry() == null ||
				getStructureMappingCartoon().getGeometryContext().getGeometry().getDimension() == 0)
		{
			return;
		}
		// process clicks only in select mode
		switch (mode) {
		case SELECT: {
			selectEventFromWorld(screenToWorld(event.getPoint()));
			break;		
		}	
		default:
			break;
		}	
	}

	public void mouseDragged(MouseEvent event) {
		if(getStructureMappingCartoon() == null){
			return;
		}
		try {
			switch (mode){
			case LINE: {
				if (getStructureMappingCartoon().getGeometryContext().getGeometry().getDimension()==0) return;
				Point worldPoint = screenToWorld(event.getX(),event.getY());
				if (bLineStretch){
					// repaint last location with XOR
					Graphics2D g = (Graphics2D)getGraphPane().getGraphics();
					g.setXORMode(Color.white);
					edgeShape.setEnd(endPoint);
					edgeShape.paint_NoAntiAlias(g,0,0);
					// set label and color for line depending on attachment area on ReactionStepShape
					Structure structure = (Structure)edgeShape.getStartShape().getModelObject();
					int lineType = getLineTypeFromWorld(structure,worldPoint);
					edgeShape.setLabel(lineLabels[lineType]);
					edgeShape.setForgroundColor(lineColors[lineType]);
					getGraphPane().setCursor(lineCursors[lineType]);
					// move line and paint with XOR
					endPoint = worldPoint;
					edgeShape.setEnd(endPoint);
					edgeShape.paint_NoAntiAlias(g,0,0);
				}else{
					if (edgeShape != null){
						return;
					}	
					Shape startShape = getStructureMappingCartoon().pickWorld(worldPoint);
					if (startShape instanceof StructureShape){
						StructureShape structureShape = (StructureShape)startShape;
						bLineStretch = true;
						endPoint = worldPoint;
						edgeShape = new RubberBandEdgeShape(structureShape,null,getStructureMappingCartoon());
						edgeShape.setEnd(endPoint);
						Graphics2D g = (Graphics2D)getGraphPane().getGraphics();
						g.setXORMode(Color.white);
						edgeShape.paint(g,0,0);
					}	
				}		
				break;
			}
			default: {
				break;
			}
			}		
		}catch (Exception e){
			System.out.println("CartoonCanvasTool.mouseDragged: uncaught exception");
			e.printStackTrace(System.out);
		}			
	}

	public void mousePressed(MouseEvent event) {
	}

	public void mouseReleased(MouseEvent event) {
		if(getStructureMappingCartoon() == null){
			return;
		}
		try {
			//Picked shape
			//		Shape pickedShape =  getStructureMappingCartoon().pickEdgeWorld(screenToWorld(event.getPoint()));
			//
			// if right mouse button, then do nothing
			//
			if (event.isPopupTrigger() && mode == Mode.SELECT){
				Shape selectedShape = getStructureMappingCartoon().getSelectedShape();
				if (selectedShape != null) {
					popupMenu(selectedShape,event.getX(),event.getY());
				}
			}
			// else, line etc...
			getGraphPane().setCursor(Cursor.getDefaultCursor());
			switch (mode){
			case LINE: {
				if (getStructureMappingCartoon() == null ||
						getStructureMappingCartoon().getGeometryContext() == null ||
						getStructureMappingCartoon().getGeometryContext().getGeometry() == null ||
						getStructureMappingCartoon().getGeometryContext().getGeometry().getDimension() == 0)
				{
					return;
				}

				getGraphPane().setCursor(Cursor.getDefaultCursor());
				Point worldPoint = screenToWorld(event.getPoint());
				Shape shape = getStructureMappingCartoon().pickWorld(worldPoint);
				if (bLineStretch){
					bLineStretch = false;
					// set label and color for line depending on attachment area on ReactionStepShape
					Structure structure = (Structure)edgeShape.getStartShape().getModelObject();
					// remove temporary edge
					getStructureMappingCartoon().removeShape(edgeShape);
					edgeShape = null;
					if (shape instanceof GeometryClassLegendShape){
						GeometryClass geometryClass = (GeometryClass)shape.getModelObject();
						getStructureMappingCartoon().getGeometryContext().assignStructure(structure, geometryClass);
						getStructureMappingCartoon().refreshAll();
						setMode(Mode.SELECT);
					}else{
						getGraphPane().repaint();
					}
				}
				break;
			}

			}
		}catch (IllegalMappingException e){
			PopupGenerator.showErrorDialog(this.getDialogOwner(getGraphPane()), "mapping error\n"+e.getMessage());
			setMode(Mode.SELECT);
		}catch (Exception e){
			System.out.println("CartoonTool.mouseReleased: uncaught exception");
			e.printStackTrace(System.out);
		}			

	}

	private void selectEventFromWorld(Point worldPoint) {
		//	Shape selectedShape = cartoon.getSelectedShape();
		getStructureMappingCartoon().clearSelection();
		Shape pickedShape = getStructureMappingCartoon().pickEdgeWorld(worldPoint);

		if (pickedShape==null) return;
		//	if (pickedShape!=selectedShape){
		getStructureMappingCartoon().selectShape(pickedShape);
		//	}	
	}

	public void setStructureMappingCartoon(StructureMappingCartoon newStructureMappingCartoon) {
		structureMappingCartoon = newStructureMappingCartoon;
	}

	public boolean shapeHasMenuAction(cbit.gui.graph.Shape shape, java.lang.String menuAction) {
		if (shape instanceof StructureMappingShape){
			if (menuAction.equals(CartoonToolEditActions.Delete.MENU_ACTION)){
				return true;
			}
		}
		return false;
	}

	public boolean shapeHasMenuActionEnabled(Shape shape, String menuAction) {
		return true;
	}

	public void updateMode(Mode newMode) {
		if (newMode==mode){
			return;
		}		
		bLineStretch = false;
		edgeShape = null;
		endPoint = null;
		if (getStructureMappingCartoon() != null){
			getStructureMappingCartoon().clearSelection();
		}
		this.mode = newMode;
		if(getGraphPane() != null){
			switch (mode){
			case LINE:{
				getGraphPane().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				break;
			}
			case SELECT:{
				getGraphPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				break;
			}
			default:{
				System.out.println("ERROR: mode " + newMode + "not defined");
				break;
			}
			}
		}	
		return;
	}

	public void saveNodePositions() { }
}

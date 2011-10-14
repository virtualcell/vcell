/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;

import java.awt.event.*;
import org.vcell.util.Coordinate;

import cbit.vcell.geometry.*;
/**
 * This type was created in VisualAge.
 */
public class CurveEditorTool implements KeyListener, MouseListener, MouseMotionListener {
	//
	//java.awt.Point lastPoint = null;
	Coordinate lastSnappedWorldCoord = null;
	private int toolBeforePanZoom = -1;
	//
	public static final int TOOL_SELECT = 0;
	public static final int TOOL_POINT = 1;
	public static final int TOOL_LINE = 2;
	public static final int TOOL_SPLINE = 3;
	public static final int TOOL_ADDCP = 4;
	public static final int TOOL_ZOOM = 5;
	public static final int TOOL_PAN = 6;
	//
	private String[] toolDescriptions =
		new String[] {
			"Select","Point","Line","Spline","Add CP","Zoom","Pan"
		};
	
	//
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private CurveRenderer fieldCurveRenderer = null;
	private cbit.image.WorldCoordinateCalculator fieldWorldCoordinateCalculator = null;
	private VCellDrawable fieldVcellDrawable = null;
	private boolean fieldProperlyConfigured = false;
	private java.awt.Component fieldKeyboardAndMouseEvents = null;
	private int fieldTool = TOOL_SELECT;
	private boolean fieldSelectionOnly = false;
	private cbit.vcell.simdata.gui.CurveValueProvider fieldCurveValueProvider = null;
	private boolean fieldEnableDrawingTools = true;

/**
 * CurveEditorTool constructor comment.
 */
public CurveEditorTool() {
	super();
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}
/**
 * Insert the method's description here.
 * Creation date: (10/14/00 3:46:59 PM)
 */
private void checkConfiguration() {
	if (getCurveRenderer() == null || 
		getWorldCoordinateCalculator() == null || 
		getVcellDrawable() == null || 
		getKeyboardAndMouseEvents() == null) {
		setProperlyConfigured(false);
	} else {
		setProperlyConfigured(true);
	}
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Gets the curveRenderer property (cbit.vcell.geometry.gui.CurveRenderer) value.
 * @return The curveRenderer property value.
 * @see #setCurveRenderer
 */
public CurveRenderer getCurveRenderer() {
	return fieldCurveRenderer;
}
/**
 * Gets the curveValueProvider property (cbit.vcell.simdata.gui.CurveValueProvider) value.
 * @return The curveValueProvider property value.
 * @see #setCurveValueProvider
 */
public cbit.vcell.simdata.gui.CurveValueProvider getCurveValueProvider() {
	return fieldCurveValueProvider;
}
/**
 * Gets the enableDrawingTools property (boolean) value.
 * @return The enableDrawingTools property value.
 * @see #setEnableDrawingTools
 */
public boolean getEnableDrawingTools() {
	return fieldEnableDrawingTools;
}
/**
 * Gets the keyboardAndMouseEvents property (java.awt.Component) value.
 * @return The keyboardAndMouseEvents property value.
 * @see #setKeyboardAndMouseEvents
 */
public java.awt.Component getKeyboardAndMouseEvents() {
	return fieldKeyboardAndMouseEvents;
}
/**
 * Gets the properlyConfigured property (boolean) value.
 * @return The properlyConfigured property value.
 * @see #setProperlyConfigured
 */
public boolean getProperlyConfigured() {
	return fieldProperlyConfigured;
}
/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}
/**
 * Gets the selectionOnly property (boolean) value.
 * @return The selectionOnly property value.
 * @see #setSelectionOnly
 */
public boolean getSelectionOnly() {
	return fieldSelectionOnly;
}
/**
 * Gets the tool property (int) value.
 * @return The tool property value.
 * @see #setTool
 */
public int getTool() {
	return fieldTool;
}
/**
 * Insert the method's description here.
 * Creation date: (7/9/2003 5:38:34 PM)
 * @return java.lang.String
 * @param tool int
 */
public String getToolDescription(int tool) {
	
	if(tool >= 0 && tool <= toolDescriptions.length){
		return toolDescriptions[tool];
	}
	return null;
}
/**
 * Gets the vcellDrawable property (cbit.vcell.geometry.gui.VCellDrawable) value.
 * @return The vcellDrawable property value.
 * @see #setVcellDrawable
 */
public VCellDrawable getVcellDrawable() {
	return fieldVcellDrawable;
}
/**
 * Gets the worldCoordinate property (cbit.image.WorldCoordinate) value.
 * @return The worldCoordinate property value.
 * @see #setWorldCoordinate
 */
public cbit.image.WorldCoordinateCalculator getWorldCoordinateCalculator() {
	return fieldWorldCoordinateCalculator;
}
/**
 * Insert the method's description here.
 * Creation date: (10/15/00 1:56:24 PM)
 * @param mousePoint java.awt.Point
 */
private Coordinate getWorldCoordinateValue(java.awt.Point mousePoint) {
	java.awt.geom.Point2D.Double c2dUnitized = getVcellDrawable().getImagePointUnitized(mousePoint);
	Coordinate worldCoordinate = getWorldCoordinateCalculator().getWorldCoordinateFromUnitized2D(c2dUnitized.getX(),c2dUnitized.getY());
	return worldCoordinate;
}
/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.KeyEvent
 */
public void keyPressed(KeyEvent event) {
    if (!getProperlyConfigured()) {
        return;
    }
    if (event.getKeyCode() == KeyEvent.VK_DELETE || event.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
        getCurveRenderer().removeSelected(event.getKeyCode());
        if(getCurveValueProvider() != null){
	        getCurveValueProvider().curveRemoved(null);
        }
        if(getTool() == TOOL_SELECT){
	        if(getCurveRenderer().getSelection() != null && !getCurveRenderer().getSelection().getCurve().isValid()){
		        getCurveRenderer().removeCurve(getCurveRenderer().getSelection().getCurve());
	        }
        }
        getVcellDrawable().repaint();
    }
    //else if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
        //getCurveRenderer().removeSelected(KeyEvent.VK_BACK_SPACE);
        //getVcellDrawable().repaint();
    //}
    else if (event.getKeyCode() == event.VK_N && getTool() == TOOL_SELECT) {
        getCurveRenderer().selectNext();
        if(getCurveValueProvider() != null){
	        getCurveValueProvider().curveRemoved(null);
        }
        getVcellDrawable().repaint();
    } else if (
        (event.getKeyCode() == event.VK_UP || event.getKeyCode() == event.VK_DOWN || event.getKeyCode() == event.VK_LEFT || event.getKeyCode() == event.VK_RIGHT)
            && getTool() == TOOL_SELECT) {
        step(event);
        getVcellDrawable().repaint();
    } else if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
        setTool(TOOL_SELECT);
        getVcellDrawable().repaint();
    }
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.KeyEvent
 */
public void keyReleased(KeyEvent event) {
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.KeyEvent
 */
public void keyTyped(KeyEvent event) {
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.MouseEvent
 */
public void mouseClicked(MouseEvent event) {
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.MouseEvent
 */
public void mouseDragged(MouseEvent event) {
	if (!getProperlyConfigured() || !getCurveRenderer().getSelectionValid()) {
		return;
	}
	if (getTool() != TOOL_SELECT) {
		//Prevents spurious coordinate changes with other tools
		return;
	}
	CurveSelectionInfo csi = getCurveRenderer().getSelection();
	if (csi != null) {
		if (csi.getType() == CurveSelectionInfo.TYPE_SEGMENT || csi.getType() == CurveSelectionInfo.TYPE_U) {
			//extend the selection
			CurveSelectionInfo csiExtended = getCurveRenderer().extend(getWorldCoordinateValue(event.getPoint()));
			if (csiExtended != null) {
				getCurveRenderer().setSelection(csiExtended);
			}
		} else {
			//move the selection
			if (lastSnappedWorldCoord != null) {
				//getCurveRenderer().move(event.getPoint().x - lastPoint.x, event.getPoint().y - lastPoint.y);
				if(csi.getCurve() instanceof ControlPointCurve && getCurveRenderer().getRenderPropertyEditable(csi.getCurve())){
					Coordinate currentSnappedWorldCoord = getWorldCoordinateCalculator().snapWorldCoordinate(getWorldCoordinateValue(event.getPoint()));
					if(!lastSnappedWorldCoord.compareEqual(currentSnappedWorldCoord)){
						double xDelta = 
							Coordinate.convertAxisFromStandardXYZToNormal(currentSnappedWorldCoord,Coordinate.X_AXIS,getCurveRenderer().getNormalAxis())-
							Coordinate.convertAxisFromStandardXYZToNormal(lastSnappedWorldCoord,Coordinate.X_AXIS,getCurveRenderer().getNormalAxis());
						double yDelta = 
							Coordinate.convertAxisFromStandardXYZToNormal(currentSnappedWorldCoord,Coordinate.Y_AXIS,getCurveRenderer().getNormalAxis())-
							Coordinate.convertAxisFromStandardXYZToNormal(lastSnappedWorldCoord,Coordinate.Y_AXIS,getCurveRenderer().getNormalAxis());
						if(move(xDelta,yDelta,csi,getCurveRenderer().getNormalAxis(),event.isControlDown())){
							lastSnappedWorldCoord = currentSnappedWorldCoord;
						}
					}
				}else{
					lastSnappedWorldCoord = getWorldCoordinateCalculator().snapWorldCoordinate(getWorldCoordinateValue(event.getPoint()));
				}
			}
		}
		getVcellDrawable().repaint();
	}
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.MouseEvent
 */
public void mouseEntered(MouseEvent event) {
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.MouseEvent
 */
public void mouseExited(MouseEvent event) {
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.MouseEvent
 */
public void mouseMoved(MouseEvent event) {
	/*
	if(!(fieldVcellDrawable.getImagePoint(event.getPoint()) != null)){
		return;
	}
	try {
		if (mode == SELECT_MODE){
		}else if (mode == LINE_MODE){
			if (newCurve != null){
				java.awt.Graphics g = fieldVcellDrawable.getGraphics();
				g.setXORMode(java.awt.Color.red);
				if (tmpPoint!=null){
					g.drawLine(lastPoint.x,lastPoint.y,tmpPoint.x,tmpPoint.y);
				}
				g.drawLine(lastPoint.x,lastPoint.y,event.getPoint().x,event.getPoint().y);
				tmpPoint = event.getPoint();
			}
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
	*/
}
/**
 * This method was created in VisualAge.
 * @param event MouseEvent
 */
public void mousePressed(MouseEvent event) {
	if (!getProperlyConfigured()) {
		return;
	}
	if((event.getModifiers() & event.BUTTON3_MASK) != 0){
		setTool(TOOL_SELECT);
		return;
	}
	if (getTool() == TOOL_ADDCP) {
		if(	getCurveRenderer().getSelection() != null && 
			getCurveRenderer().getSelection().getCurve() instanceof ControlPointCurve &&
			((ControlPointCurve)getCurveRenderer().getSelection().getCurve()).isControlPointAddable()){
				((ControlPointCurve)(getCurveRenderer().getSelection().getCurve())).insertControlPoint(
					getWorldCoordinateCalculator().snapWorldCoordinate(getWorldCoordinateValue(event.getPoint())),getCurveRenderer().getSelection().getControlPoint());
		}
		getVcellDrawable().repaint();
	} else if (getTool() == TOOL_LINE || getTool() == TOOL_SPLINE || getTool() == TOOL_POINT) {
		CurveSelectionInfo selectedCSI = getCurveRenderer().getSelection();
		if (selectedCSI == null || getTool() == TOOL_POINT) {
			boolean wasFromCVP = false;
			if(getCurveValueProvider() != null && getCurveValueProvider().providesInitalCurve(getTool(),getWorldCoordinateValue(event.getPoint()))){
				//Returns null if curve shouldn't be started at this point
				selectedCSI = getCurveValueProvider().getInitalCurveSelection(getTool(),getWorldCoordinateValue(event.getPoint()));
				wasFromCVP = true;
			}else if (getTool() == TOOL_LINE) {
				selectedCSI = new CurveSelectionInfo(new PolyLine());
			} else if (getTool() == TOOL_SPLINE) {
				selectedCSI = new CurveSelectionInfo(new Spline());
			} else if (getTool() == TOOL_POINT) {
				selectedCSI = new CurveSelectionInfo(new SinglePoint());
			}
			if(selectedCSI != null){
				if(getCurveValueProvider() != null){
					getCurveValueProvider().setDescription(selectedCSI.getCurve());
				}
				getCurveRenderer().addCurve(selectedCSI.getCurve());
				getCurveRenderer().renderPropertySubSelectionType(selectedCSI.getCurve(), CurveRenderer.SUBSELECTION_CONTROL_POINT);
				getCurveRenderer().renderPropertyEditable(selectedCSI.getCurve(),(!wasFromCVP?true:false));
			}
		}
		//
		if(	selectedCSI != null && selectedCSI.getCurve() instanceof ControlPointCurve &&
				(getCurveValueProvider() == null ||
				!getCurveValueProvider().providesInitalCurve(getTool(),getWorldCoordinateValue(event.getPoint())) ||
				getCurveValueProvider().isAddControlPointOK(getTool(),getWorldCoordinateValue(event.getPoint()),selectedCSI.getCurve()))){
					if((!(selectedCSI.getCurve() instanceof CurveSelectionCurve)) && getCurveValueProvider().isAddControlPointOK(getTool(),getWorldCoordinateCalculator().snapWorldCoordinate(getWorldCoordinateValue(event.getPoint())),selectedCSI.getCurve())){
						((ControlPointCurve)(selectedCSI.getCurve())).appendControlPoint(getWorldCoordinateCalculator().snapWorldCoordinate(getWorldCoordinateValue(event.getPoint())));
					}else if(getCurveValueProvider().isAddControlPointOK(getTool(),getWorldCoordinateValue(event.getPoint()),selectedCSI.getCurve())){
						try{
							if(selectedCSI.getCurve() instanceof CurveSelectionCurve){
								ControlPointCurve targetCurve =
									(ControlPointCurve)(((CurveSelectionCurve)selectedCSI.getCurve()).
											getSourceCurveSelectionInfo().getCurve());
								double dist = targetCurve.getDistanceTo(getWorldCoordinateValue(event.getPoint()));
								int segmentIndex = targetCurve.pickSegment(getWorldCoordinateValue(event.getPoint()), dist*1.1);
								Coordinate[] coordArr = targetCurve.getSampledCurve().getControlPointsForSegment(segmentIndex);
								Coordinate middleCoord = new Coordinate((coordArr[0].getX()+coordArr[1].getX())/2,(coordArr[0].getY()+coordArr[1].getY())/2,(coordArr[0].getZ()+coordArr[1].getZ())/2);
								((ControlPointCurve)(selectedCSI.getCurve())).appendControlPoint(getWorldCoordinateCalculator().snapWorldCoordinateFace(middleCoord/*getWorldCoordinateValue(event.getPoint())*/));
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
			}

		if(selectedCSI != null && !selectedCSI.getCurve().isValid()){
			int subSel =getCurveRenderer().getRenderPropertySubSelectionType(selectedCSI.getCurve());
			boolean bEdit = getCurveRenderer().getRenderPropertyEditable(selectedCSI.getCurve());
			getCurveRenderer().setSelection(null);
			getCurveRenderer().addCurve(selectedCSI.getCurve());
			getCurveRenderer().renderPropertyEditable(selectedCSI.getCurve(),bEdit);
			getCurveRenderer().renderPropertySubSelectionType(selectedCSI.getCurve(),subSel);
		}
		getCurveRenderer().setSelection(selectedCSI);
		if(getCurveValueProvider() != null && selectedCSI != null && selectedCSI.getCurve().isValid()){
			getCurveValueProvider().curveAdded(selectedCSI.getCurve());
		}
		getVcellDrawable().repaint();
	} else if (getTool() == TOOL_SELECT) {
		CurveSelectionInfo invalidCSI = null;
		if(getCurveRenderer().getSelection() != null && 
			!getCurveRenderer().getSelection().getCurve().isValid()){
				invalidCSI = getCurveRenderer().getSelection();
		}
			//
		lastSnappedWorldCoord = getWorldCoordinateCalculator().snapWorldCoordinate(getWorldCoordinateValue(event.getPoint()));
		CurveSelectionInfo csi = getCurveRenderer().pick(getWorldCoordinateValue(event.getPoint()));
		if(csi != null && getCurveRenderer().getRenderPropertySelectable(csi.getCurve())){
			getCurveRenderer().setSelection(csi);
		}else{
			getCurveRenderer().selectNothing();
		}
		//
		if(getCurveValueProvider() != null){
			if(invalidCSI != null){
				getCurveValueProvider().curveRemoved(invalidCSI.getCurve());
			}else{
				getCurveValueProvider().curveAdded(null);
			}
		}
		//
		getVcellDrawable().repaint();
	}
	//
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.MouseEvent
 */
public void mouseReleased(MouseEvent event) {
}
/**
 * Insert the method's description here.
 * Creation date: (7/17/2003 5:59:43 PM)
 * @param dx double
 * @param dy double
 */
private boolean move(double xDelta, double yDelta,CurveSelectionInfo csi,int normalAxis,boolean bAdjustToSlice) {

	//
	double maxDistance = 0;
	ControlPointCurve cpc = (ControlPointCurve)csi.getCurve();
	Coordinate[] newCPArr = new Coordinate[cpc.getControlPointCount()];
	Coordinate[] oldCPArr = new Coordinate[cpc.getControlPointCount()];
	//
	//Move all the controlpoints
	//
	for(int i=0;i < cpc.getControlPointCount();i+= 1){
		//
		double xCoord = Coordinate.convertAxisFromStandardXYZToNormal(cpc.getControlPoint(i),Coordinate.X_AXIS,normalAxis);
		double yCoord = Coordinate.convertAxisFromStandardXYZToNormal(cpc.getControlPoint(i),Coordinate.Y_AXIS,normalAxis);
		double zCoord;
		if(bAdjustToSlice){
			zCoord = Coordinate.convertAxisFromStandardXYZToNormal(getWorldCoordinateCalculator().getWorldCoordinateFromUnitized2D(0,0),Coordinate.Z_AXIS,normalAxis);
		}else{
			zCoord = Coordinate.convertAxisFromStandardXYZToNormal(cpc.getControlPoint(i),Coordinate.Z_AXIS,normalAxis);
		}
		//
		Coordinate oldCoord =
			Coordinate.convertCoordinateFromNormalToStandardXYZ(xCoord,yCoord,zCoord,normalAxis);
		Coordinate newCoord =
			Coordinate.convertCoordinateFromNormalToStandardXYZ(xCoord+xDelta,yCoord+yDelta,zCoord,normalAxis);
		//
		Coordinate snapOldCoord = getWorldCoordinateCalculator().snapWorldCoordinate(oldCoord);
		Coordinate snapNewCoord = getWorldCoordinateCalculator().snapWorldCoordinate(newCoord);
		
		double nextDistance = Math.abs(snapOldCoord.distanceTo(snapNewCoord));
		if(nextDistance > maxDistance){maxDistance = nextDistance;}
		
		oldCPArr[i] = snapOldCoord;
		newCPArr[i] = snapNewCoord;
	}
	//
	//Check if all controlpoints moved the same (some may be at edge)
	//
	boolean bAllSame = true;
	for(int i=0;i < newCPArr.length;i+= 1){
		double nextDistance = Math.abs(oldCPArr[i].distanceTo(newCPArr[i]));
		double dDelta = (maxDistance-nextDistance)/maxDistance;
		if(dDelta > 1e-8){
			bAllSame = false;
			break;
		}
	}
	if(!bAllSame && (csi.getType()== CurveSelectionInfo.TYPE_CURVE)){
		return false;
	}
	//
	//Set new Controlpoints
	//
	for(int i=0;i < cpc.getControlPointCount();i+= 1){
		if (csi.getType() == CurveSelectionInfo.TYPE_CONTROL_POINT ) {
			if(csi.getControlPoint() == i){
				cpc.setControlPoint(i,newCPArr[i]);
			}
		} else if(csi.getType()== CurveSelectionInfo.TYPE_CURVE){
			cpc.setControlPoint(i,newCPArr[i]);
		}
	}
	return true;
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}
/**
 * Sets the curveRenderer property (cbit.vcell.geometry.gui.CurveRenderer) value.
 * @param curveRenderer The new value for the property.
 * @see #getCurveRenderer
 */
public void setCurveRenderer(CurveRenderer curveRenderer) {
	CurveRenderer oldValue = fieldCurveRenderer;
	fieldCurveRenderer = curveRenderer;
	firePropertyChange("curveRenderer", oldValue, curveRenderer);
	checkConfiguration();
}
/**
 * Sets the curveValueProvider property (cbit.vcell.simdata.gui.CurveValueProvider) value.
 * @param curveValueProvider The new value for the property.
 * @see #getCurveValueProvider
 */
public void setCurveValueProvider(cbit.vcell.simdata.gui.CurveValueProvider curveValueProvider) {
	cbit.vcell.simdata.gui.CurveValueProvider oldValue = fieldCurveValueProvider;
	fieldCurveValueProvider = curveValueProvider;
	firePropertyChange("curveValueProvider", oldValue, curveValueProvider);
}
/**
 * Sets the enableDrawingTools property (boolean) value.
 * @param enableDrawingTools The new value for the property.
 * @see #getEnableDrawingTools
 */
public void setEnableDrawingTools(boolean enableDrawingTools) {
	boolean oldValue = fieldEnableDrawingTools;
	fieldEnableDrawingTools = enableDrawingTools;
	firePropertyChange("enableDrawingTools", new Boolean(oldValue), new Boolean(enableDrawingTools));
}
/**
 * Sets the keyboardAndMouseEvents property (java.awt.Component) value.
 * @param keyboardAndMouseEvents The new value for the property.
 * @see #getKeyboardAndMouseEvents
 */
public void setKeyboardAndMouseEvents(java.awt.Component keyboardAndMouseEvents) {
	java.awt.Component oldValue = fieldKeyboardAndMouseEvents;
	if (oldValue != null) {
		oldValue.removeKeyListener(this);
		oldValue.removeMouseListener(this);
		oldValue.removeMouseMotionListener(this);
	}
	fieldKeyboardAndMouseEvents = keyboardAndMouseEvents;
	if (keyboardAndMouseEvents != null) {
		fieldKeyboardAndMouseEvents.addKeyListener(this);
		fieldKeyboardAndMouseEvents.addMouseListener(this);
		fieldKeyboardAndMouseEvents.addMouseMotionListener(this);
	}
	firePropertyChange("keyboardAndMouseEvents", oldValue, keyboardAndMouseEvents);
	checkConfiguration();
}
/**
 * Sets the properlyConfigured property (boolean) value.
 * @param properlyConfigured The new value for the property.
 * @see #getProperlyConfigured
 */
private void setProperlyConfigured(boolean properlyConfigured) {
	boolean oldValue = fieldProperlyConfigured;
	fieldProperlyConfigured = properlyConfigured;
	firePropertyChange("properlyConfigured", new Boolean(oldValue), new Boolean(properlyConfigured));
}
/**
 * Sets the selectionOnly property (boolean) value.
 * @param selectionOnly The new value for the property.
 * @see #getSelectionOnly
 */
public void setSelectionOnly(boolean selectionOnly) {
	boolean oldValue = fieldSelectionOnly;
	fieldSelectionOnly = selectionOnly;
	firePropertyChange("selectionOnly", new Boolean(oldValue), new Boolean(selectionOnly));
}
/**
 * Sets the tool property (int) value.
 * @param tool The new value for the property.
 * @see #getTool
 */
public void setTool(int tool) {
	if (tool == fieldTool) {
		return;
	}
	int oldValue = fieldTool;
	fieldTool = tool;
	if(fieldTool == TOOL_SELECT){
		toolBeforePanZoom = -1;
	}
	if (fieldTool != TOOL_ADDCP && fieldTool != TOOL_ZOOM && fieldTool != TOOL_PAN && fieldTool != toolBeforePanZoom && getCurveRenderer() != null) {
		toolBeforePanZoom = -1;
		boolean bRepaint = getCurveRenderer().getSelection() != null;
		getCurveRenderer().selectNothing();
		if (bRepaint && getVcellDrawable() != null) {
			getVcellDrawable().repaint();
		}
	}else if((fieldTool == TOOL_ZOOM || fieldTool == TOOL_PAN) && toolBeforePanZoom == -1){
		toolBeforePanZoom = oldValue;
	}
	if(getCurveValueProvider() != null){
		getCurveValueProvider().curveAdded(null);
	}
	firePropertyChange("tool", new Integer(oldValue), new Integer(tool));
}
/**
 * Insert the method's description here.
 * Creation date: (7/9/2003 5:38:34 PM)
 * @return java.lang.String
 * @param tool int
 */
public void setToolDescription(int tool,String description) {
	
	if(tool >= 0 && tool <= toolDescriptions.length){
		toolDescriptions[tool] = description;
	}
	
}
/**
 * Sets the vcellDrawable property (cbit.vcell.geometry.gui.VCellDrawable) value.
 * @param vcellDrawable The new value for the property.
 * @see #getVcellDrawable
 */
public void setVcellDrawable(VCellDrawable vcellDrawable) {
	VCellDrawable oldValue = fieldVcellDrawable;
	fieldVcellDrawable = vcellDrawable;
	firePropertyChange("vcellDrawable", oldValue, vcellDrawable);
	checkConfiguration();
}
/**
 * Sets the worldCoordinate property (cbit.image.WorldCoordinate) value.
 * @param worldCoordinate The new value for the property.
 * @see #getWorldCoordinate
 */
public void setWorldCoordinateCalculator(cbit.image.WorldCoordinateCalculator worldCoordinateCalculator) {
	cbit.image.WorldCoordinateCalculator oldValue = fieldWorldCoordinateCalculator;
	fieldWorldCoordinateCalculator = worldCoordinateCalculator;
	firePropertyChange("worldCoordinateCalculator", oldValue, worldCoordinateCalculator);
	checkConfiguration();
}
/**
 * Insert the method's description here.
 * Creation date: (10/14/00 3:53:50 PM)
 */
private void step(KeyEvent event) {
	if(getCurveRenderer() == null ||
		getCurveRenderer().getSelection() == null ||
		!(getCurveRenderer().getSelection().getCurve() instanceof ControlPointCurve)){
			return;
		}
	Coordinate wd = getCurveRenderer().getWorldDelta();
	double xDelta = Coordinate.convertAxisFromStandardXYZToNormal(wd,Coordinate.X_AXIS,getCurveRenderer().getNormalAxis());
	double yDelta = Coordinate.convertAxisFromStandardXYZToNormal(wd,Coordinate.Y_AXIS,getCurveRenderer().getNormalAxis());
	
	int multiplier = event.isShiftDown() ? 10 : 1;
	double dx = 0;
	double dy = 0;
	switch (event.getKeyCode()) {
		case KeyEvent.VK_UP :
			dy = -multiplier*yDelta;
			break;
		case KeyEvent.VK_DOWN :
			dy = multiplier*yDelta;
			break;
		case KeyEvent.VK_LEFT :
			dx = -multiplier*xDelta;
			break;
		case KeyEvent.VK_RIGHT :
			dx = multiplier*xDelta;
			break;
	}
	ControlPointCurve cpc = (ControlPointCurve)getCurveRenderer().getSelection().getCurve();
	move(dx, dy,getCurveRenderer().getSelection(),getCurveRenderer().getNormalAxis(),event.isControlDown());
}
}

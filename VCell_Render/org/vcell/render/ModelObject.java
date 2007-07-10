package org.vcell.render;

import java.util.Iterator;
import java.util.Vector;

import javax.media.opengl.GL;

import cbit.render.objects.BoundingBox;

public abstract class ModelObject {
	private Vector<ModelObject> modelObjectList = new Vector<ModelObject>();
	private Object sourceObject = null;
	private int callListID = -1;
	private boolean bDirty = true;
	private boolean showSurfacesWireframe = false;
	private boolean hidden = false;
	protected BoundingBox boundingBox = null;
	
	public ModelObject(){
		
	}
	public final ModelObject findModelObjectBySourceObject(Object object){
		if (object == null){
			return null;
		}
		if (object.equals(getSourceObject())){
			return this;
		}
		for (Iterator iter = modelObjectList.iterator(); iter.hasNext();) {
			ModelObject element = (ModelObject) iter.next();
			ModelObject foundModelObject = element.findModelObjectBySourceObject(object);
			if (foundModelObject!=null){
				return foundModelObject;
			}
		}
		return null;
	}
	public final int getNumChildren(){
		return modelObjectList.size();
	}
	public final ModelObject getChild(int index){
		return modelObjectList.elementAt(index);
	}
	public final void addChild(ModelObject modelObject){
		modelObjectList.add(modelObject);
	}
	public BoundingBox getBoundingBox(){
		return boundingBox;
	}
	public final BoundingBox getSubTreeBoundingBox(){
		BoundingBox boundingBox = getBoundingBox();
		for (Iterator iter = modelObjectList.iterator(); iter.hasNext();) {
			ModelObject element = (ModelObject) iter.next();
			boundingBox = BoundingBox.union(boundingBox, element.getSubTreeBoundingBox());
		}
		return boundingBox;
	}
	
	public void draw(GL gl){
    	if (getCallListID()<0 || isDirty()){
		    /* make the gears */
		    int callListID = getCallListID();
		    if (callListID == -1){
		    	callListID = gl.glGenLists(1);
	    	    setCallListID(callListID);
		    }
		    gl.glNewList(callListID, GL.GL_COMPILE);
		    draw0(gl);
		    setDirty(false);
		    gl.glEndList();
		}
		gl.glCallList(getCallListID());
    	drawChildren(gl);
	}
	
	public void drawChildren(GL gl){
	    for (int i=0; i<getNumChildren();i++){
	    	getChild(i).draw(gl);
	    }
	}
	
	public void draw0(GL gl){
	}

	public int getCallListID() {
		return callListID;
	}
	
	public final void clearCallLists(){
		callListID = -1;
		for (Iterator iter = modelObjectList.iterator(); iter.hasNext();) {
			ModelObject element = (ModelObject) iter.next();
			element.clearCallLists();
		}
	}

	public void setCallListID(int callListID) {
		this.callListID = callListID;
	}

	public boolean isDirty() {
		return bDirty;
	}

	public void setDirty(boolean dirty) {
		bDirty = dirty;
	}

	public Object getSourceObject() {
		return sourceObject;
	}

	public void setSourceObject(Object sourceObject) {
		this.sourceObject = sourceObject;
	}
	public boolean isShowSurfacesWireframe() {
		return showSurfacesWireframe;
	}
	public void setShowSurfacesWireframe(boolean showSurfacesWireframe) {
		this.showSurfacesWireframe = showSurfacesWireframe;
	}
	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
}

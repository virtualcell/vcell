package org.vcell.render;

import java.awt.Color;

import javax.media.opengl.GL;

import org.vcell.spatial.BoundingBox;
import org.vcell.spatial.SurfaceCollection;


public class SurfaceCollectionModelObject extends ModelObject {
	private SurfaceModelObject[] childSurfaceModelObjects = null;
	private SurfaceCollection surfaceCollection = null;
	public SurfaceCollectionModelObject(SurfaceCollection argSurfaceCollection){
		if (argSurfaceCollection==null){
			throw new IllegalArgumentException("surface was null");
		}
		this.surfaceCollection = argSurfaceCollection;
		setSourceObject(surfaceCollection);
		childSurfaceModelObjects = new SurfaceModelObject[argSurfaceCollection.getSurfaceCount()];
		for (int i=0;i<argSurfaceCollection.getSurfaceCount();i++){
			childSurfaceModelObjects[i] = new SurfaceModelObject(argSurfaceCollection.getSurfaces(i));
			addChild(childSurfaceModelObjects[i]);
		}
		boundingBox = BoundingBox.fromNodes(surfaceCollection.getNodes());		
	}

	public void setSurfaceColors(int[][] surfaceColors) {
		for (int i=0;i<childSurfaceModelObjects.length;i++){
			childSurfaceModelObjects[i].setSurfaceColors(surfaceColors[i]);
		}
	}
	
	public void showSurfacesWireframe(boolean[] surfacesWireframe){
		for (int i=0;i<childSurfaceModelObjects.length;i++){
			childSurfaceModelObjects[i].setShowSurfacesWireframe(surfacesWireframe[i]);
		}
	}
	
	public void setSurfacesShowing(boolean[] surfacesShowing){
		for (int i=0;i<childSurfaceModelObjects.length;i++){
			childSurfaceModelObjects[i].setHidden(!surfacesShowing[i]);
		}
	}
	
}

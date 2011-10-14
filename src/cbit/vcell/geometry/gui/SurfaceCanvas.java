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
import cbit.vcell.render.Vect3d;
/**
 * Insert the type's description here.
 * Creation date: (11/26/2003 12:40:01 PM)
 * @author: Jim Schaff
 */
public class SurfaceCanvas extends javax.swing.JPanel implements java.beans.PropertyChangeListener {


	//
	//
	private java.awt.Color aoiHighlightColor = java.awt.Color.white;
	//
	//
	public static class SurfaceCollectionPick {
		public int surfaceIndex = -1;
		public int polygonIndex = -1;

		public SurfaceCollectionPick(int argSI,int argPI){
			surfaceIndex = argSI;
			polygonIndex = argPI;
		}
	}
	//
	private static final int POLYGON_SCREEN_SIZE = 5000;
	private static final int MAX_FAST_RENDER = 500;
	private int[] quickRenderSkip = null;
	private boolean worldPolygonQuickRender = false;
	//

	private java.awt.Color[][] surfaceColorsColorArr = null;
	private PolygonInfo[] sortedScreenPolygons = null;
	private cbit.vcell.render.Vect3d displacement = new cbit.vcell.render.Vect3d(0,0,0);
	private double scale = 1.0;
	private java.awt.image.BufferedImage cachedProjectedImage = null;
	private cbit.vcell.geometry.surface.SurfaceCollection fieldSurfaceCollection = null;
	private cbit.vcell.render.Trackball fieldTrackball = new cbit.vcell.render.Trackball(new cbit.vcell.render.Camera());
	private org.vcell.util.Origin fieldOrigin = new org.vcell.util.Origin(0, 0, 0);
	private org.vcell.util.Extent fieldExtent = new org.vcell.util.Extent(1, 1, 1);
	private boolean fieldEnableDepthCueing = false;
	private boolean fieldBQuickRender = false;
	private boolean[] fieldSurfacesShowing = null;
	private int[][] fieldSurfacesColors = null;
	private boolean[] fieldSurfacesWireframe = null;
	private java.awt.AlphaComposite[] fieldSurfaceTransparency = null;
	private boolean fieldShowBoundingBox = true;
	private double fieldWireFrameThickness = 2;
	private boolean fieldDisableRepaint = false;
	private boolean[][] fieldAreaOfInterest = null;

/**
 * SurfaceCanvas constructor comment.
 */
public SurfaceCanvas() {
	super();
	initialize();
	addPropertyChangeListener(this);
}

/**
 * Insert the method's description here.
 * Creation date: (10/19/2005 12:58:11 PM)
 */
public void clearCachedImage() {

	cachedProjectedImage = null;	
}


/**
 * Insert the method's description here.
 * Creation date: (10/12/2005 7:05:43 AM)
 */
private void createSurfaceColorsColorArr(boolean bFlatShade) {
	
	SurfaceRenderer surfaceRenderer = new SurfaceRenderer(getTrackball(),getExtent(),getOrigin());
	SurfaceRenderer.BoundingBoxInfo bbi = surfaceRenderer.createBoundingBoxInfo(getWidth(),getHeight(),getEnableDepthCueing());
	
	if(getSurfacesColors() == null){
		surfaceColorsColorArr = null;
	}else{
		surfaceColorsColorArr = new java.awt.Color[getSurfaceCollection().getSurfaceCount()][];
		for(int i=0;i<surfaceColorsColorArr.length;i+= 1){
			surfaceColorsColorArr[i] = new java.awt.Color[getSurfaceCollection().getSurfaces(i).getPolygonCount()];
		}

		float baseColor_Red = 1.0f;
		float baseColor_Green = 1.0f;
		float baseColor_Blue = 1.0f;
		for(int i=0;i<sortedScreenPolygons.length;i+= 1){
			int surfIndex = sortedScreenPolygons[i].surfaceIndex;
			int polyIndex = sortedScreenPolygons[i].polygonIndex;
			if(getSurfacesColors()[surfIndex].length == 1){//apply 1 color to all polygons
				baseColor_Red = ((float)((getSurfacesColors()[surfIndex][0] &  0x00ff0000)>>16))/(float)255;
				baseColor_Green = ((float)((getSurfacesColors()[surfIndex][0] & 0x0000ff00)>>8))/(float)255;
				baseColor_Blue = ((float)(getSurfacesColors()[surfIndex][0] &    0x000000ff))/(float)255;
			}else{
				baseColor_Red = ((float)((getSurfacesColors()[surfIndex][polyIndex] &  0x00ff0000)>>16))/(float)255;
				baseColor_Green = ((float)((getSurfacesColors()[surfIndex][polyIndex] & 0x0000ff00)>>8))/(float)255;
				baseColor_Blue = ((float)(getSurfacesColors()[surfIndex][polyIndex] &    0x000000ff))/(float)255;
			}
			surfaceColorsColorArr[surfIndex][polyIndex] = 
				new java.awt.Color(
					baseColor_Red*(float)(bFlatShade?sortedScreenPolygons[i].flatShadeColorScale:sortedScreenPolygons[i].notFlatShadeColorScale),
					baseColor_Green*(float)(bFlatShade?sortedScreenPolygons[i].flatShadeColorScale:sortedScreenPolygons[i].notFlatShadeColorScale),
					baseColor_Blue*(float)(bFlatShade?sortedScreenPolygons[i].flatShadeColorScale:sortedScreenPolygons[i].notFlatShadeColorScale));
			
		}
	}
}


/**
 * Gets the areaOfInterest property (boolean[][]) value.
 * @return The areaOfInterest property value.
 * @see #setAreaOfInterest
 */
public boolean[][] getAreaOfInterest() {
	return fieldAreaOfInterest;
}


/**
 * Gets the bQuickRender property (boolean) value.
 * @return The bQuickRender property value.
 * @see #setBQuickRender
 */
public boolean getBQuickRender() {
	return fieldBQuickRender;
}


/**
 * Gets the disableRepaint property (boolean) value.
 * @return The disableRepaint property value.
 * @see #setDisableRepaint
 */
public boolean getDisableRepaint() {
	return fieldDisableRepaint;
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/2005 12:15:25 PM)
 * @return cbit.vcell.render.Vect3d
 */
public cbit.vcell.render.Vect3d getDisplacement() {
	return displacement;
}


/**
 * Gets the enableDepthCueing property (boolean) value.
 * @return The enableDepthCueing property value.
 * @see #setEnableDepthCueing
 */
public boolean getEnableDepthCueing() {
	return fieldEnableDepthCueing;
}


/**
 * Gets the extent property (cbit.util.Extent) value.
 * @return The extent property value.
 * @see #setExtent
 */
public org.vcell.util.Extent getExtent() {
	return fieldExtent;
}


/**
 * Gets the origin property (cbit.util.Origin) value.
 * @return The origin property value.
 * @see #setOrigin
 */
public org.vcell.util.Origin getOrigin() {
	return fieldOrigin;
}


/**
 * Insert the method's description here.
 * Creation date: (10/15/2005 9:41:52 AM)
 * @return float
 */
private float getPolygonScale() {
	
	return POLYGON_SCREEN_SIZE/getPreferredScreenSize();
}


/**
 * Insert the method's description here.
 * Creation date: (10/15/2005 9:40:28 AM)
 * @return float
 */
private float getPreferredScreenSize() {
	
	return 0.9f*Math.min(getWidth(),getHeight());
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/2005 12:15:25 PM)
 * @return double
 */
public double getScale() {
	return scale;
}


/**
 * Gets the showBoundingBox property (boolean) value.
 * @return The showBoundingBox property value.
 * @see #setShowBoundingBox
 */
public boolean getShowBoundingBox() {
	return fieldShowBoundingBox;
}


/**
 * Gets the surfaceCollection property (cbit.vcell.geometry.surface.SurfaceCollection) value.
 * @return The surfaceCollection property value.
 * @see #setSurfaceCollection
 */
public cbit.vcell.geometry.surface.SurfaceCollection getSurfaceCollection() {
	return fieldSurfaceCollection;
}


/**
 * Gets the surfacesColors property (int[][]) value.
 * @return The surfacesColors property value.
 * @see #setSurfacesColors
 */
public int[][] getSurfacesColors() {
	return fieldSurfacesColors;
}


/**
 * Gets the surfacesShowing property (boolean[]) value.
 * @return The surfacesShowing property value.
 * @see #setSurfacesShowing
 */
public boolean[] getSurfacesShowing() {
	return fieldSurfacesShowing;
}


/**
 * Gets the surfacesWireframe property (boolean[]) value.
 * @return The surfacesWireframe property value.
 * @see #setSurfacesWireframe
 */
public boolean[] getSurfacesWireframe() {
	return fieldSurfacesWireframe;
}


/**
 * Gets the surfaceTransparency property (java.awt.AlphaComposite[]) value.
 * @return The surfaceTransparency property value.
 * @see #setSurfaceTransparency
 */
public java.awt.AlphaComposite[] getSurfaceTransparency() {
	return fieldSurfaceTransparency;
}


/**
 * Gets the trackball property (cbit.vcell.render.Trackball) value.
 * @return The trackball property value.
 * @see #setTrackball
 */
public cbit.vcell.render.Trackball getTrackball() {
	return fieldTrackball;
}


/**
 * Gets the wireFrameThickness property (double) value.
 * @return The wireFrameThickness property value.
 * @see #setWireFrameThickness
 */
public double getWireFrameThickness() {
	return fieldWireFrameThickness;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SurfaceCanvas");
		setLayout(null);
		setSize(160, 120);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SurfaceCanvas aSurfaceCanvas;
		aSurfaceCanvas = new SurfaceCanvas();
		frame.setContentPane(aSurfaceCanvas);
		frame.setSize(aSurfaceCanvas.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/26/2003 12:40:37 PM)
 * @param g2d java.awt.Graphics
 */
protected void paintComponent(java.awt.Graphics g) {
	setBackground(java.awt.Color.black);
	super.paintComponent(g);
	if(getDisableRepaint()){
		return;
	}
	try {
		if (getSurfaceCollection()==null){
			return;
		}
		java.awt.Graphics2D g2d = (java.awt.Graphics2D)g;

		boolean bAlreadyQuickRendered = false;
		if(sortedScreenPolygons == null){
//System.out.println("recalculating screen polygons");
			bAlreadyQuickRendered = true;
			SurfaceRenderer surfaceRenderer = new SurfaceRenderer(getTrackball(),getExtent(),getOrigin());
			SurfaceRenderer.BoundingBoxInfo bbi0 = surfaceRenderer.createBoundingBoxInfo(POLYGON_SCREEN_SIZE,POLYGON_SCREEN_SIZE,getEnableDepthCueing());
			sortedScreenPolygons = surfaceRenderer.createProjectedScreenPolygons(getSurfaceCollection(),(getBQuickRender()?quickRenderSkip:null),bbi0);
			cachedProjectedImage = null;
			surfaceColorsColorArr = null;
		}
		if(surfaceColorsColorArr == null){
//System.out.println("recalculating colors");
			createSurfaceColorsColorArr(true);
			cachedProjectedImage = null;
		}
		if(cachedProjectedImage == null /*|| cachedProjectedImage.getWidth() != getWidth() || cachedProjectedImage.getHeight() != getHeight()*/){
//System.out.println("recalculating image");
			SurfaceRenderer surfaceRenderer = new SurfaceRenderer(getTrackball(),getExtent(),getOrigin());
			cachedProjectedImage = new java.awt.image.BufferedImage(getWidth(),getHeight(),java.awt.image.BufferedImage.TYPE_INT_ARGB);
			java.awt.Graphics2D cpig2d = (java.awt.Graphics2D)cachedProjectedImage.getGraphics();

			cpig2d.translate(displacement.getX(),displacement.getY());
			cpig2d.translate((getWidth()/2.0)*(1-scale),(getHeight()/2.0)*(1-scale));
			cpig2d.scale(scale,scale);
			float preferedScreenSize = getPreferredScreenSize();
			float polygonScale = getPolygonScale();
			//
			// center polygons
			//
			cpig2d.translate(((getWidth()-preferedScreenSize)/2.0),((getHeight()-preferedScreenSize)/2.0));
			//
			// scale to fit
			//
			cpig2d.scale(1.0f/polygonScale,1.0f/polygonScale);
			
			SurfaceRenderer.BoundingBoxInfo bbi1 = surfaceRenderer.createBoundingBoxInfo(POLYGON_SCREEN_SIZE,POLYGON_SCREEN_SIZE,getEnableDepthCueing());
			java.awt.BasicStroke wireFrameThickness = new java.awt.BasicStroke((float)(getPolygonScale()*fieldWireFrameThickness),java.awt.BasicStroke.CAP_ROUND,java.awt.BasicStroke.JOIN_ROUND);			
			surfaceRenderer.paintSurface(
				sortedScreenPolygons,bbi1,cpig2d,(bAlreadyQuickRendered || !getBQuickRender()?null:quickRenderSkip),
				surfaceColorsColorArr,getSurfacesShowing(),getSurfacesWireframe(),
				getSurfaceTransparency(),getShowBoundingBox(),getAreaOfInterest(),aoiHighlightColor,wireFrameThickness);
		}
		g2d.drawImage(cachedProjectedImage,0,0,java.awt.Color.black,null);
//System.out.println("repainting\n\n");
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (9/21/2005 2:38:27 PM)
 */
public SurfaceCollectionPick pickPolygon(int scrnX,int scrnY) {
	//
	// must scale and zoom scrnX,scrnY
	//
	int newScrnX =
		(int)	(
					(((scrnX
					- displacement.getX()
					- ((getWidth()/2.0)*(1-scale)))
					/scale)
					-((getWidth()-getPreferredScreenSize())/2.0))
					/(1.0f/getPolygonScale())
				);

	int newScrnY =
		(int)	(
					(((scrnY
					- displacement.getY()
					- ((getHeight()/2.0)*(1-scale)))
					/scale)
					-((getHeight()-getPreferredScreenSize())/2.0))
					/(1.0f/getPolygonScale())
				);




	
	if(sortedScreenPolygons != null){
		for (int i = sortedScreenPolygons.length-1; i >= 0 ; i--){
			PolygonInfo mapping = sortedScreenPolygons[i];
			if(	(getSurfacesShowing() == null || getSurfacesShowing()[mapping.surfaceIndex]) &&
				mapping.polygon.contains(newScrnX,newScrnY)){
				return new SurfaceCollectionPick(mapping.surfaceIndex,mapping.polygonIndex);
			}
		}
	}

	return null;
}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {

	//
	if(evt.getSource() == this && evt.getPropertyName().equals("surfaceCollection")
			){
			
				sortedScreenPolygons = null;
				if(getSurfaceCollection() == null){
					fieldAreaOfInterest = null;
				}else{
					fieldAreaOfInterest = new boolean[getSurfaceCollection().getSurfaceCount()][];
					for(int i=0;i<fieldAreaOfInterest.length;i+= 1){
						fieldAreaOfInterest[i] = new boolean[getSurfaceCollection().getSurfaces(i).getPolygonCount()];
						java.util.Arrays.fill(fieldAreaOfInterest[i],false);
					}
				}

				quickRenderSkip = null;
				if(getSurfaceCollection() != null){
					quickRenderSkip = new int[getSurfaceCollection().getSurfaceCount()];
					int totalPolygons = 0;
					for(int i=0;i<quickRenderSkip.length;i+= 1){
						quickRenderSkip[i] = getSurfaceCollection().getSurfaces(i).getPolygonCount();
						totalPolygons+= quickRenderSkip[i];
					}
					for(int i=0;i<quickRenderSkip.length;i+= 1){
						int fracTotal = (MAX_FAST_RENDER*quickRenderSkip[i])/totalPolygons;
						if(fracTotal != 0){
							quickRenderSkip[i] = quickRenderSkip[i]/fracTotal;
						}else{
							quickRenderSkip[i] = 1;
						}
					}
				}

				invalidate();
				repaint();
		
	}

	if((evt.getSource() instanceof SurfaceViewerTool && evt.getPropertyName().equals("viewAngleRadians"))){
		
				sortedScreenPolygons = null;
				worldPolygonQuickRender = getBQuickRender();
				
	}else if(	(evt.getSource() == this && evt.getPropertyName().equals("bQuickRender"))
			){
			
				cachedProjectedImage = null;
				if(getBQuickRender() == false && worldPolygonQuickRender){
					sortedScreenPolygons = null;
				}
				worldPolygonQuickRender = false;
		
	}else if(	(evt.getSource() == this && evt.getPropertyName().equals("displacement")) ||
				(evt.getSource() == this && evt.getPropertyName().equals("scale"))
				){
			
				cachedProjectedImage = null;
		
	}else if(	(evt.getSource() == this && evt.getPropertyName().equals("enableDepthCueing"))	
			){
			
				sortedScreenPolygons = null;
		
	}else if(//Projected polygons still valid but cached image needs recalculation
				(evt.getSource() == this && evt.getPropertyName().equals("surfaceTransparency")) ||
				(evt.getSource() == this && evt.getPropertyName().equals("surfacesWireframe")) ||
				(evt.getSource() == this && evt.getPropertyName().equals("surfacesShowing")) ||
				(evt.getSource() == this && evt.getPropertyName().equals("wireFrameThickness")) ||
				(evt.getSource() == this && evt.getPropertyName().equals("showBoundingBox"))
			){
				
				cachedProjectedImage = null;
		
	}else if(//Colors need recalulation
				evt.getSource() == this && evt.getPropertyName().equals("surfacesColors")
			){
				
				surfaceColorsColorArr = null;
		
	}

	//set highlited surface polygons array
	if	(
				evt.getSource() == this &&
				(evt.getPropertyName().equals("areaOfInterest") || evt.getPropertyName().equals("lineOfInterest"))
			){
				cachedProjectedImage = null;
			}
}


/**
 * Insert the method's description here.
 * Creation date: (2/26/2006 2:58:04 PM)
 */
public void setAOIHighlightColor(java.awt.Color argAOIHighlightColor) {
	
	aoiHighlightColor = argAOIHighlightColor;
}


/**
 * Sets the areaOfInterest property (boolean[][]) value.
 * @param areaOfInterest The new value for the property.
 * @see #getAreaOfInterest
 */
public void setAreaOfInterest(boolean[][] areaOfInterest) {
	boolean[][] oldValue = fieldAreaOfInterest;
	fieldAreaOfInterest = areaOfInterest;
	firePropertyChange("areaOfInterest",oldValue, areaOfInterest);
}


/**
 * Sets the bQuickRender property (boolean) value.
 * @param bQuickRender The new value for the property.
 * @see #getBQuickRender
 */
public void setBQuickRender(boolean bQuickRender) {
	boolean oldValue = fieldBQuickRender;
	fieldBQuickRender = bQuickRender;
	firePropertyChange("bQuickRender", new Boolean(oldValue), new Boolean(bQuickRender));
}


/**
 * Sets the disableRepaint property (boolean) value.
 * @param disableRepaint The new value for the property.
 * @see #getDisableRepaint
 */
public void setDisableRepaint(boolean disableRepaint) {
	boolean oldValue = fieldDisableRepaint;
	fieldDisableRepaint = disableRepaint;
	firePropertyChange("disableRepaint", new Boolean(oldValue), new Boolean(disableRepaint));
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/2005 12:15:25 PM)
 * @param newDisplacement cbit.vcell.render.Vect3d
 */
public void setDisplacement(cbit.vcell.render.Vect3d newDisplacement) {
	Vect3d oldDisplacement = displacement;
	displacement = newDisplacement;
	firePropertyChange("displacement",oldDisplacement,displacement);
	invalidate();
	//repaint();
}


/**
 * Sets the enableDepthCueing property (boolean) value.
 * @param enableDepthCueing The new value for the property.
 * @see #getEnableDepthCueing
 */
public void setEnableDepthCueing(boolean enableDepthCueing) {
	boolean oldValue = fieldEnableDepthCueing;
	fieldEnableDepthCueing = enableDepthCueing;
	firePropertyChange("enableDepthCueing", new Boolean(oldValue), new Boolean(enableDepthCueing));
}


/**
 * Sets the extent property (cbit.util.Extent) value.
 * @param extent The new value for the property.
 * @see #getExtent
 */
public void setExtent(org.vcell.util.Extent extent) {
	fieldExtent = extent;
}


/**
 * Sets the origin property (cbit.util.Origin) value.
 * @param origin The new value for the property.
 * @see #getOrigin
 */
public void setOrigin(org.vcell.util.Origin origin) {
	fieldOrigin = origin;
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/2005 12:15:25 PM)
 * @param newScale double
 */
public void setScale(double newScale) {
	double oldScale = scale;
	scale = newScale;
	firePropertyChange("scale",oldScale,scale);
	invalidate();
	//repaint();
}


/**
 * Sets the showBoundingBox property (boolean) value.
 * @param showBoundingBox The new value for the property.
 * @see #getShowBoundingBox
 */
public void setShowBoundingBox(boolean showBoundingBox) {
	boolean oldValue = fieldShowBoundingBox;
	fieldShowBoundingBox = showBoundingBox;
	firePropertyChange("showBoundingBox", new Boolean(oldValue), new Boolean(showBoundingBox));
}


/**
 * Sets the surfaceCollection property (cbit.vcell.geometry.surface.SurfaceCollection) value.
 * @param surfaceCollection The new value for the property.
 * @see #getSurfaceCollection
 */
public void setSurfaceCollection(cbit.vcell.geometry.surface.SurfaceCollection surfaceCollection) {
	cbit.vcell.geometry.surface.SurfaceCollection oldValue = fieldSurfaceCollection;
	fieldSurfaceCollection = surfaceCollection;
	firePropertyChange("surfaceCollection", oldValue, surfaceCollection);
}


/**
 * Sets the surfacesColors property (int[][]) value.
 * @param surfacesColors The new value for the property.
 * @see #getSurfacesColors
 */
public void setSurfacesColors(int[][] surfacesColors) {
	int[][] oldValue = fieldSurfacesColors;
	fieldSurfacesColors = surfacesColors;
	firePropertyChange("surfacesColors", oldValue, surfacesColors);
}


/**
 * Sets the surfacesShowing property (boolean[]) value.
 * @param surfacesShowing The new value for the property.
 * @see #getSurfacesShowing
 */
public void setSurfacesShowing(boolean[] surfacesShowing) {
	boolean[] oldValue = fieldSurfacesShowing;
	fieldSurfacesShowing = surfacesShowing;
	firePropertyChange("surfacesShowing", oldValue, surfacesShowing);
}


/**
 * Sets the surfacesWireframe property (boolean[]) value.
 * @param surfacesWireframe The new value for the property.
 * @see #getSurfacesWireframe
 */
public void setSurfacesWireframe(boolean[] surfacesWireframe) {
	boolean[] oldValue = fieldSurfacesWireframe;
	fieldSurfacesWireframe = surfacesWireframe;
	firePropertyChange("surfacesWireframe", oldValue, surfacesWireframe);
}


/**
 * Sets the surfaceTransparency property (java.awt.AlphaComposite[]) value.
 * @param surfaceTransparency The new value for the property.
 * @see #getSurfaceTransparency
 */
public void setSurfaceTransparency(java.awt.AlphaComposite[] surfaceTransparency) {
	java.awt.AlphaComposite[] oldValue = fieldSurfaceTransparency;
	fieldSurfaceTransparency = surfaceTransparency;
	firePropertyChange("surfaceTransparency", oldValue, surfaceTransparency);
}


/**
 * Sets the trackball property (cbit.vcell.render.Trackball) value.
 * @param trackball The new value for the property.
 * @see #getTrackball
 */
public void setTrackball(cbit.vcell.render.Trackball trackball) {
	cbit.vcell.render.Trackball oldValue = fieldTrackball;
	fieldTrackball = trackball;
	firePropertyChange("trackball", oldValue, trackball);
}


/**
 * Sets the wireFrameThickness property (double) value.
 * @param wireFrameThickness The new value for the property.
 * @see #getWireFrameThickness
 */
public void setWireFrameThickness(double wireFrameThickness) {
	double oldValue = fieldWireFrameThickness;
	fieldWireFrameThickness = wireFrameThickness;
	firePropertyChange("wireFrameThickness", new Double(oldValue), new Double(wireFrameThickness));
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC7FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D7FDEC94C594A7319A7FA091132A45C4C57C2A26A8C60C44BF8C9A7EC04198C9D1932B41D8B5D1E22AB6FEA06A5CC721D50B859B2311C4C454D25A6B754396AE20ADF7C7DB2D5AC0454634143D5B69F52FFB374B6EDEBF845477E6F66E36758E8A89173C4C4EFBB3EF5E67EF66C86DD70BAADC6EDAC2DC95447EF53888D974A9A10D117CFA415B777D5AAA6278FD8D340D648DF9E16C815996B6BF987C
	98462DC0A70007E3910CB5640F6067B6BF4F668BF23989D16BF00D9E5CE1734AE0CDA570BA01866AB26B3981178676597321AB8BC979A3063B9A75B8F48ADAE66FFD4A9EF7120D2EF9FB702C054CCE5CD383739EA0F45EC33C7F6E0571C07FF86C47E8C431FC93D12A2A3E98559254B226FD311492889CCD9923D21486256404E412CDCC5FF341CA361F1015B03F3C9EF93BFB700CCE789E845201387D873FBFE05BB7F86ABED25C1270FE12437EB5755C6E72FA077D09E75CDF30D82F9C46187A77C20C00460066
	8395812D817A92E41F1255C318402E0895549289AD59256B07D45978D513298A5F01GC8EC5E919A558C19901E479B028DD96CB99B71FDCB4EFB9F0FC9E12B48E95111BBC949BDD73B37E409C9C961A9F769DE9E0BCDFBB3B161E73F18736C9D8DB3EBEE6659FF9267595E6AA7BC95D94E5E39F11D47EF6B89B5E44E369AB05785AC57B2FC3B6CBA607C450C3F5C416775D74F78B8DF853206C6D19B03DB05ADFD5E452426791A2CF1A8F8E83EFBC1A3372130B1E38B2F0BC7C7300F0A01DF86D469DBDB35932893
	E888C877095A289DEE1AE3ED74CB06F5A82528B2B5885F7B6870E7B052A94B10FA1326A5A5237418E4A8D2C425A63157BCB10C36C7E66CFB1A55EC944E6826C9EAC81622A5C13B846DC58D132FA99D5E06EB8C4A2483CA024281891D883D9B06811A08AA19569F3AACD9B433024B4B07B1169352B150888A601B47E563890C7982703F90E8F9BA8E5AF154778BB512D48D847AB4EDBC258F20A7243289F3759E6B6DB5303E94086BFAD3C2DE9F4865A6216756635B9DFAC2CA944D170CE9519B5777A0D6CEB509DC
	3FBAA8F29D2ADEC29A56DD15B55779D0F779CDB616B64D2E79C226936D62F3FF4F60F9765FD65B263977F86F851D611963BA9EDFB90A39AA863B4AB087FE9163670E6F64B5FBC8B22928525F8DA8B0AAE7A24E57BEB90238CAA7E8528AD11DA6E578B06D9AF9F624AAD33F23DA3A36CA99DF07FA8FC20119E97E4BA3D81312E599CAA4E55123543214E4ACA3FF1D1DE526A2FA26D544D96F0CA01EE813D06F69756F0FECE7FD10CCD2E6F4FA0BADA7927ACFD51AG2303DAAAE9F17EE68973BDCE276DDE79D05A4C
	EADDCDE15F71BBEC2974917AE0A889888536AE1972E39D5706449D0CD487BC3C0B7BE0BC6197B5B9790D28495DFF5FCED60C686EECD8D8F04B956E95FEDEB3457ECCCDF21B5E4DE13BABD219611B5C563FFA716E1EF9230A9A38CEC6BD65203F3299B02CD954401948970EBE33139F54D41507B29D63F83486F3C2CF26A84093B674AF69381D0CEE1FA5BBE647104B272398C35501855C5EB776E08C597BA0AECDC8D3BEF3926AA0907839DF82EB78DB601F907A096B77B50B374049EE74C547B95022E6916F472B
	45DBA0FF3C102C2FDE67497516D95E4C63DC521C09F749DD139E9E2B17A81E3923856E12969127722893573ACCD30BAA8CD2ED9FEF121D79603CC279C398C7954334FA21E126C4BC6E1451DE44DC1BEDE341A52159F7EEE7CB86CB86DA7E0FAB7CDEFC0145E32C055F0B2EB47FD546379CFCFEDFCEF60B775ABC101536889C5E9D43FC7434828E831D864A87DA86D45CAA6250CEDB49B96E395EA4DCBFC9C9BDBA230145FDC3BD3C676CF9992D90BD87A33FA32EE5B9AF03B33DEDD02F0C37BE0C3CBA18F73689FC
	FE251B24E3D76E78BE53FDA118BAD762E7DCFDD14F6031E90C65465C36D8F64C8D4432E3EEE7EC9B67038E678BC448FBE2E747647ED8B6CC76B0595D214D2C1E27351485EF30B64C572AB4BE4AB0BF5D46EBF7E99B3F77F97F5FBD0938B08A8F96B86A28CE2364669FF06FDBE1313786663B01C2C0B2D09E902F61321028618D3826DD680C6D7A16087F9CDA4227C4923E54C442A7C5AC9F1595AB9078AD252AFD36290463C9E398F58D018E8BE8FE3B4013296F841E945D3A1894D7E547137583E75CB737F37FD6
	35E770A49F7EDBF08CBBF538525E676DD8DBF52083F9379F2EF56840B83E8664C35B197C6999F24C4D1E99726AEE279C6D6DF2B965BF4D10D7B876F35FD6FFABFC5B34FAA9D16F4D4B793FAF2F43FE9B5B630252052E028F7E7670BEF873FF18B5377E71064FF79F47349BD92E56F74C463ABB98FFC347EC2CEB8D0BBAF99BE4C37B8456EDD73046D6403C84289C68637D0077C0BD7B85563D907313F3E05D002677C1032A0E16627E4DBBFCE17F5D9E5CA272326C7EBB48724BC26E0FF264E5E8BF4F47E9FB2CFB
	FE218BC76E5FADF1742F88E42B00B6GF983EB2BBE87728704FF0DE38D5C3FB0F4C617E5C951B1F6FB89670EA9F4B2434686653E5D96A669BCAEF5FC6F8FDFFC5CBDF2D14F60F179D2490D3B3B146C38DB27E447DD1FF2F6DCEDD7F23FF58FAA4E37AE4F6BAB635857A148618920BC20822022F4CED7AB3BF8CE3364B3AC47E8D0E37FB02D74DBA41E5B57FB63677EDF7AC09C5FB8914D3234846A977C15F14C1725690C49F98F47B1C606929B3378D21B7FD89C71D125230C4DF9EF0C6736EB6378396D72286CBD
	867A8626F5DBE7250A7714B6BAEA52347F55EACD1A57EB76EB26020F3A7FGD0CB8788942EC1A14587GG4893GGD0CB818294G94G88G88GC7FBB0B6942EC1A14587GG4893GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG7F87GGGG
**end of data**/
}
}

package org.vcell.render;

import javax.media.opengl.GLJPanel;

import org.vcell.util.Extent;

import cbit.render.objects.SurfaceCollection;
import cbit.render.objects.Vect3d;

/**
 * Insert the type's description here.
 * Creation date: (11/26/2003 12:40:01 PM)
 * @author: Jim Schaff
 */
public class SurfaceCanvas extends GLJPanel implements java.beans.PropertyChangeListener {

	private JOGLRenderer joglRenderer = new JOGLRenderer();
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
	private Vect3d displacement = new Vect3d(0,0,0);
	private double scale = 1.0;
	private java.awt.image.BufferedImage cachedProjectedImage = null;
	private SurfaceCollection fieldSurfaceCollection = null;
	private Trackball fieldTrackball = new Trackball(new Camera());
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
	addGLEventListener(joglRenderer);
	addMouseListener(joglRenderer);
	addMouseMotionListener(joglRenderer);
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
public Vect3d getDisplacement() {
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
public SurfaceCollection getSurfaceCollection() {
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
public Trackball getTrackball() {
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

//protected void paintComponent(java.awt.Graphics g) {
//	setBackground(java.awt.Color.black);
//	super.paintComponent(g);
//	if(getDisableRepaint()){
//		return;
//	}
//	try {
//		if (getSurfaceCollection()==null){
//			return;
//		}
//		java.awt.Graphics2D g2d = (java.awt.Graphics2D)g;
//
//		boolean bAlreadyQuickRendered = false;
//		if(sortedScreenPolygons == null){
////System.out.println("recalculating screen polygons");
//			bAlreadyQuickRendered = true;
//			SurfaceRenderer surfaceRenderer = new SurfaceRenderer(getTrackball(),getExtent(),getOrigin());
//			SurfaceRenderer.BoundingBoxInfo bbi0 = surfaceRenderer.createBoundingBoxInfo(POLYGON_SCREEN_SIZE,POLYGON_SCREEN_SIZE,getEnableDepthCueing());
//			sortedScreenPolygons = surfaceRenderer.createProjectedScreenPolygons(getSurfaceCollection(),(getBQuickRender()?quickRenderSkip:null),bbi0);
//			cachedProjectedImage = null;
//			surfaceColorsColorArr = null;
//		}
//		if(surfaceColorsColorArr == null){
////System.out.println("recalculating colors");
//			createSurfaceColorsColorArr(true);
//			cachedProjectedImage = null;
//		}
//		if(cachedProjectedImage == null /*|| cachedProjectedImage.getWidth() != getWidth() || cachedProjectedImage.getHeight() != getHeight()*/){
////System.out.println("recalculating image");
//			SurfaceRenderer surfaceRenderer = new SurfaceRenderer(getTrackball(),getExtent(),getOrigin());
//			cachedProjectedImage = new java.awt.image.BufferedImage(getWidth(),getHeight(),java.awt.image.BufferedImage.TYPE_INT_ARGB);
//			java.awt.Graphics2D cpig2d = (java.awt.Graphics2D)cachedProjectedImage.getGraphics();
//
//			cpig2d.translate(displacement.getX(),displacement.getY());
//			cpig2d.translate((getWidth()/2.0)*(1-scale),(getHeight()/2.0)*(1-scale));
//			cpig2d.scale(scale,scale);
//			float preferedScreenSize = getPreferredScreenSize();
//			float polygonScale = getPolygonScale();
//			//
//			// center polygons
//			//
//			cpig2d.translate(((getWidth()-preferedScreenSize)/2.0),((getHeight()-preferedScreenSize)/2.0));
//			//
//			// scale to fit
//			//
//			cpig2d.scale(1.0f/polygonScale,1.0f/polygonScale);
//			
//			SurfaceRenderer.BoundingBoxInfo bbi1 = surfaceRenderer.createBoundingBoxInfo(POLYGON_SCREEN_SIZE,POLYGON_SCREEN_SIZE,getEnableDepthCueing());
//			java.awt.BasicStroke wireFrameThickness = new java.awt.BasicStroke((float)(getPolygonScale()*fieldWireFrameThickness),java.awt.BasicStroke.CAP_ROUND,java.awt.BasicStroke.JOIN_ROUND);			
//			surfaceRenderer.paintSurface(
//				sortedScreenPolygons,bbi1,cpig2d,(bAlreadyQuickRendered || !getBQuickRender()?null:quickRenderSkip),
//				surfaceColorsColorArr,getSurfacesShowing(),getSurfacesWireframe(),
//				getSurfaceTransparency(),getShowBoundingBox(),getAreaOfInterest(),aoiHighlightColor,wireFrameThickness);
//		}
//		g2d.drawImage(cachedProjectedImage,0,0,java.awt.Color.black,null);
////System.out.println("repainting\n\n");
//	}catch (Throwable e){
//		e.printStackTrace(System.out);
//	}
//}

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
public void setDisplacement(Vect3d newDisplacement) {
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
public void setSurfaceCollection(SurfaceCollection surfaceCollection) {
	SurfaceCollection oldValue = fieldSurfaceCollection;
	fieldSurfaceCollection = surfaceCollection;
	if (fieldSurfaceCollection==null){
		joglRenderer.setModelObject(null);
	}else{
		SurfaceCollectionModelObject surfaceModelObject = new SurfaceCollectionModelObject(fieldSurfaceCollection);
		AxisModelObject axisModelObject = new AxisModelObject(surfaceModelObject.getBoundingBox().getSize());
		surfaceModelObject.addChild(axisModelObject);
		joglRenderer.setModelObject(surfaceModelObject);
	}
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
	if (joglRenderer.getModelObject() instanceof SurfaceCollectionModelObject){
		SurfaceCollectionModelObject surfaceCollectionModelObject = (SurfaceCollectionModelObject)joglRenderer.getModelObject();
		surfaceCollectionModelObject.setSurfaceColors(surfacesColors);
		surfaceCollectionModelObject.setDirty(true);
	}
	display();
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
	if (joglRenderer.getModelObject() instanceof SurfaceCollectionModelObject){
		SurfaceCollectionModelObject surfaceCollectionModelObject = (SurfaceCollectionModelObject)joglRenderer.getModelObject();
		surfaceCollectionModelObject.setSurfacesShowing(surfacesShowing);
		surfaceCollectionModelObject.setDirty(true);
	}
	display();
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
	if (joglRenderer.getModelObject() instanceof SurfaceCollectionModelObject){
		SurfaceCollectionModelObject surfaceCollectionModelObject = (SurfaceCollectionModelObject)joglRenderer.getModelObject();
		surfaceCollectionModelObject.showSurfacesWireframe(surfacesWireframe);
		surfaceCollectionModelObject.setDirty(true);
	}
	display();
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
public void setTrackball(Trackball trackball) {
	Trackball oldValue = fieldTrackball;
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
	D0CB838494G88G88G4F0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D7FDEC9C4595F7E8C0D1D515A0E86B28C1F223008218128A3735107F8888D0A09604AAD0243AC0D527CA54A8A2CA2BD214CAF3E77B6E6CCB0273A1F0A09289A42500398602A83A3AD59D9BB5AEEE31380F580E0DEF6FEEECAF593BDDF6773E62B86EFB73F137B6F70993A9A77DB4BBEFE65E3CF99F3F19A387DF2FE9F3398F9262EAA36277B797A1B58788997AC8EB11326569281FB8FEC7817B48B75D
	2D5056401817757F65598D6DDEC0BD4830ED55237ECEB26C699272A6A117F03989F9104D31BC07056CB474E300EAC02DDE1A5F4EF63EBE7831680F5D34067C6ED1533D8775B8F4CA6CB6785A9322BDC2DEF4BDED60DEAB191D38E69A7AB5GBCFC8BE91DFB975A1E21F3D37FCC44D5BB10CDD0CD8BCCD1BDC5ED3390184A2841E0A8E3CEAA895A2724330AC542DF213EE60F0F2DA764B0741FE332377C38C7B5FC2F852C87F07B7F71D60F30EFF8662724F96A217617AA58FF4C64F68F1AA57B075E09379CE23E3E
	A704BEDD86E33500F500E6C02B95759F833C86E38748936EF7206D0D5BFDFAAA2527FB8D631416B47F2BA729865F41A00C885967B4211BC9C2F89CD7F93A4B58F3A970F5EB2FF89D77491A13B226351F7F10B45D7BDD37370CCF1A564C38FBAC6E0B30D572895FFBEB453DEDEBFE4E4D5F7B8CF16ECD36FE60EDAB33776127BE70AE93FAD6BA76EE34B0565FE731EE00EF174883AE3F0D49BB6D121C675FFBCC0E7DA3B036541639B13CD75AB2583A0AF43E7F3D32FE28FE60DB6EADB63701E4CA36703C280FE09D
	F5037CB4A08648832A81F5007599199B2F367CF51139B1241876290C2AA529C9785A7A504B50523CEDAA83E94BD652899AD5CCD509EB54A2E24E7DA134BDBEEF5D43ACE79330473FE91A1A0A2D6AE9502EC0F9D15362F39E8F6D43B9A6E523E1B5C5E10314C1245E1F07001A0826D8F644C8AAB6AD4D60634F065017D9A58A9AC181FCF33FEC0E224F374079C996E21015FE988D233ECF291926DAB0B8286B67B2C698CFC2E2B88FD29BEB7B98FC77G382EDF1DC159EA68B794755C945E6F50532FA650FC45AC48
	5A78019F39F2E3D646FA7B300CF57F1E3BC977135FA99B6B6510F75B3202CBF38BF37EAE26936D627D137E61AB2C3F3D422645577860D56D513248F95C3F1F0DE02C3A613C278153B969634670911E3327940B4AAC7D4C048423491247795C87A2482BB4CB53F6BFB5E8BA899F164811C7A26D257C1D540B3975B8139B106FFD10E0D6D17EE484F3D231EDD30DE7EC9A22362D2627CA633FE4FBD91938D1AA9539D7F38479C44FC13E9767EF0D6CE7F510CED3E6F4F109987FAD4BD52A5194985D27E752B6176F08
	E23C4F510228155FC7BDAC57358C569D3F4356AAF8D19B261A82D7E069120DB94C63D73C728EC6D4018C6F6255505667E4CE665E14B9F96C7C9DE45D0461AE470555375D686E4A731C694E17F2125B741BB365ED5F46EAC648ADEE6B68G5E5D73EFD419834BC6D14FA0680F8172F919835343079CF5A6025F27EB9AF7E551471111CE0C897DAAC3011E3020BFAD7AEDECE47F02312870A19FCF0E208FB58797F0FBF73E0DBEE46F03AF152C128FD8B94803E070BFC38AD8435F82CB98DF607C65F9799630BB70AC
	6B318FF8A1AF7D7D449E799638655C9A523475FDEF2537CCE7017B7943C2495F534F77FA382FFEF6967734E1ECE9C17A695991A72F75DA161ED099250AB3FEFB54998FAE3BF934835AC955346C81A8183C74470AD13497B9D70885975C60DDF867D697CADCD2DB78BA2F70FBF18B73473A823F97DDC5798EA6EFF44879FDF93EC33E571E06311E731207DF9A47F854C03F813089308D30FBA66A6F1E11FEB8FA76A4394CBDB710066BA72DE821F985AC6F1B33AD3C66C47F97E75BE44DC14B6F08DBD94CCF431EE3
	GAE7B358FE5D5502F1E117C3C9D5DABFC778747F7D54755F06AE24177D8F1CD77603E699A2F4C392F0F17675C636365B9775D71FDDC8EBA1CAF90B9FEF27C521C7C7771F21C5C42466A3C9E164F85BDE3C3ADDD40F8EDA872E38344EB538C4FDD5F85FE6F737A7FD18EF9E1929EAC30D5482089320B2DFD51A757CEC37F82E0A52081508460B93C53ABF338FF96664CCA1DD3EF9CA772BF071E8A2871D4A013CD8514389D20C9558E867F175134C1E1AA61FCF242073A6AC0C7A320F9D672C97E4F12CFEAEFDFC5
	B6EEAE4FA74F05E75D3BE6850FDCAC71C9927EDBF08E3B586E936B46E07C160BA82BEDBF685001FE5C8188G7878B37346B1B6EF4F9B6F6CF00E233D3DAE677829F963ED0E757CEC9B0E4B336D5A3016E8BFDEDA71FFDF13B8931993E7FBEC056B416AA18F2F03BFFE0D3396D7BFDE5F152E631C361A456A633905DCF7B713774FAD643A0FFCB2CF06E02CEECEF25D1F3E409C6B2A0A7ABF848C82120025001AAA49F59B47CF104BF0DDD8B78621C0B5C7C97173FD237D6A7E3B5D6F15F1293B6FCED2FFC33F5BDF
	A1AEF5D5BC9E1BC4DB5F384205ADBF5F72981E2FF5C954FF84508F98839800E5CB64793A3E6866677B84AA2357361544B43B3D646122AA4D1544D8207CECF7E2BA0BB85E6B786E71DDFB5E0DDC53BD38DFBC9315F9F74FC4F95EBDB0D11EF78FCDDC1AD70FCED4FE6B7EE542795665F1FDCA413ADE89B12C87B4811E8734DEA7E37A1309B7F8CC4B44731364946D5359BFCC3B789609D5BEEBCD6C727FCB6F0861459B57EDDBCF21FEA93FA7067132F50389392CAE06BEB25529E91BCF95727BE218C49A1DE4E2AE
	FBC629EC57E66572F6EDD758FB8C74058B065039D341FBCA1F1C34E8717CAFA81DC55900B524DBAABE6A7E8FD0CB87881C09AB3C5287GG4893GGD0CB818294G94G88G88G4F0171B41C09AB3C5287GG4893GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8C88GGGG
**end of data**/
}
}
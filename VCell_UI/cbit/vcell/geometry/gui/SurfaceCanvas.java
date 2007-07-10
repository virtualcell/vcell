package cbit.vcell.geometry.gui;
import org.vcell.render.*;

import cbit.render.objects.SurfaceCollection;
import cbit.render.objects.Vect3d;

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
}
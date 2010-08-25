package cbit.gui.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import java.util.Vector;

import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.VisualState.PaintLayer;

public abstract class Shape implements VisualState.Owner {
	public Point      relativePos     = new Point(0,0);       // screen coordinate relative to parent
	public Dimension  shapeSize    = new Dimension();      // screen size
	protected Dimension preferredSize = new Dimension();
	protected Dimension minimumSize = new Dimension();

	protected Dimension labelSize = new Dimension();
	protected Point     labelPos = new Point();
	private   String label = null;

	protected Vector<Shape> childShapeList = new Vector<Shape>();
	private Shape parent = null;

	private boolean bDirty = false;

	protected boolean bNoFill = false;

	public static final int ATTACH_CENTER = 0;
	public static final int ATTACH_LEFT   = 1;
	public static final int ATTACH_RIGHT  = 2;

	protected Color defaultBG = Color.white;
	protected Color defaultFG = Color.black;
	protected Color defaultBGselect = Color.red;
	protected Color defaultFGselect = Color.white;

	protected Color backgroundColor = defaultBG;
	public Color forgroundColor = defaultFG;

	private boolean bSelected = false;
	protected GraphModel graphModel = null;

	private Font boldFont = null;

	protected final VisualState visualState = createVisualState();

	/**
	 * This method was created by a SmartGuide.
	 * @param feature cbit.vcell.model.Feature
	 */
	public Shape (GraphModel graphModel) {
		this.graphModel = graphModel;
	}

	public abstract VisualState createVisualState();
	public VisualState getVisualState() { return visualState; }

	public void addChildShape(Shape shape) {
		if(shape.parent != null) { shape.parent.removeChild(shape); }
		childShapeList.addElement(shape);
		shape.parent = this;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @param g java.awt.Graphics
	 */
	public void change_managed(Graphics2D g) throws Exception {
		for (int i=0;i<countChildren();i++){
			Shape child = childShapeList.elementAt(i);
			child.change_managed(g);
		}
		shapeSize = getPreferedSize(g);
		layout();
	}


	/**
	 * This method was created by a SmartGuide.
	 * @param g java.awt.Graphics
	 */
	public void change_managed2(Graphics2D g, Dimension forceSize) throws Exception {
		for (int i=0;i<countChildren();i++){
			Shape child = childShapeList.elementAt(i);
			child.change_managed2(g,null);
		}
		if (forceSize != null){
			shapeSize = forceSize;
		}else{		
			shapeSize = getPreferedSize(g);
		}	
		layout();
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (4/17/2001 12:54:21 AM)
	 * @return boolean
	 * @param shape cbit.vcell.graph.Shape
	 */
	public boolean contains(Shape shape) {
		for (int i=0;i<childShapeList.size();i++){
			if ((childShapeList.elementAt(i)).equals(shape)){
				return true;
			}
		}
		return false;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return int
	 */
	public int countChildren() {
		return childShapeList.size();
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (6/27/2005 1:53:00 PM)
	 */
	public final static void drawRaisedOutline(int x,int y,int width,int height,Graphics g,Color back,Color fore,Color shadow) {

		Color origColor = g.getColor();
		g.setColor(shadow);
		g.drawRect(x+1,y+1,width,height);
		g.drawRect(x+2,y+2,width,height);
		g.setColor(back);
		g.fillRect(x,y,width,height);
		g.setColor(fore);
		g.drawRect(x,y,width,height);
		g.setColor(origColor);

	}


	/**
	 * This method was created by a SmartGuide.
	 * @return java.awt.Point
	 */
	public final Point getAbsLocation() {
		Shape parent = this;
		Point pos = new Point(relativePos);
		while ((parent = parent.getParent())!=null){
			pos.x += parent.relativePos.x;
			pos.y += parent.relativePos.y;
		}	
		return pos;
	}


	/**
	 * This method was created in VisualAge.
	 * @return int
	 * @param absolutePosition java.awt.Point
	 */
	public int getAttachmentFromAbs(Point screenPoint) throws Exception {

		Point offset = getAbsLocation();
		Point localPos = new Point(screenPoint.x-offset.x,screenPoint.y-offset.y);

		//
		// get distance from Center
		//
		Point attach = getAttachmentLocation(ATTACH_CENTER);
		double d2Center = (localPos.x-attach.x)*(localPos.x-attach.x) + (localPos.y-attach.y)*(localPos.y-attach.y);
		int attachType = ATTACH_CENTER;
		double attachDistance = d2Center;

		//
		// get distance from Left
		//
		attach = getAttachmentLocation(ATTACH_LEFT);
		double d2Left = (localPos.x-attach.x)*(localPos.x-attach.x) + (localPos.y-attach.y)*(localPos.y-attach.y);
		if (d2Left<attachDistance){
			attachType = ATTACH_LEFT;
			attachDistance = d2Left;
		}
		//
		// get distance from Right
		//
		attach = getAttachmentLocation(ATTACH_RIGHT);
		double d2Right = (localPos.x-attach.x)*(localPos.x-attach.x) + (localPos.y-attach.y)*(localPos.y-attach.y);
		if (d2Right<attachDistance){
			attachType = ATTACH_RIGHT;
			attachDistance = d2Right;
		}

		return attachType;
	}


	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Point
	 */
	public Point getAttachmentLocation(int attachmentType) {
		return new Point(shapeSize.width/2, shapeSize.height/2);
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (6/28/2005 9:13:17 AM)
	 * @param g java.awt.Graphics
	 */
	public Font getBoldFont(Graphics g) {

		if(boldFont == null){
			boldFont = g.getFont().deriveFont(Font.BOLD);
		}
		return boldFont;
	}


	public List<Shape> getChildren() { return childShapeList;}


	/**
	 * This method was created by a SmartGuide.
	 * @return java.lang.String
	 */
	public final String getLabel() {
		if (label==null){
			refreshLabel();
		}
		return label;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return java.awt.Point
	 */
	public Point getLocation() {
		return relativePos;
	}


	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Point
	 */
	public java.awt.Point getLocationOnScreen(java.awt.Point canvasLoc) {
		//java.awt.Point canvasLoc = graphModel.graphPane.getLocationOnScreen();
		java.awt.Point myLoc = getAbsLocation();
		myLoc.x += canvasLoc.x;
		myLoc.y += canvasLoc.y;
		return myLoc;
	}


	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Object
	 */
	public abstract Object getModelObject();


	/**
	 * This method was created by a SmartGuide.
	 * @return cbit.vcell.graph.Shape
	 */
	public Shape getParent() {
		return parent;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return int
	 * @param g java.awt.Graphics
	 */
	abstract public Dimension getPreferedSize(java.awt.Graphics2D g);


	/**
	 * This method was created by a SmartGuide.
	 * @return int
	 */
	public Point getSeparatorDeepCount() {
		int selfCountX = 1;
		if (countChildren()>0){
			selfCountX++;
		}		
		int selfCountY = countChildren() + 1;

		Point totalDeepCount = new Point(selfCountX,selfCountY);

		for (int i=0;i<countChildren();i++){
			Shape child = childShapeList.elementAt(i);
			Point childCount = child.getSeparatorDeepCount();
			totalDeepCount.x += childCount.x;
			totalDeepCount.y += childCount.y;
		}

		return totalDeepCount;
	}


	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Dimension
	 */
	public Dimension getSize() {
		return shapeSize;
	}

	/**
	 * This method was created by a SmartGuide.
	 * @return boolean
	 * @param shape cbit.vcell.graph.Shape
	 */
	boolean isDescendant(Shape shape) {
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.elementAt(i);
			//
			// check if child is the shape
			//
			if (child==shape){
				return true;
			}
			//
			// check if child encapsulates shape
			//
			boolean childHasShape = child.isDescendant(shape);	
			if (childHasShape) return true;
		}
		return false;
	}


	/**
	 * This method was created in VisualAge.
	 * @return boolean
	 */
	public boolean isDirty() {
		return bDirty;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return boolean
	 * @param p java.awt.Point
	 */
	protected abstract boolean isInside (Point p );


	/**
	 * This method was created by a SmartGuide.
	 * @return boolean
	 * @param p java.awt.Point
	 */
	public abstract boolean isOnBorder(Point p);


	/**
	 * This method was created by a SmartGuide.
	 * @return boolean
	 */
	public boolean isSelected() {
		return bSelected;
	}

	public abstract void layout() throws LayoutException;

	// TODO Layering needs some review - but not now
	// TODO Need to move selected shape on top
	public void paint(Graphics2D graphics, int xParent, int yParent) {
		int xThis = relativePos.x + xParent;
		int yThis = relativePos.y + yParent;
		if(visualState.isAllowedToShowByAllAncestors()) {
			if(visualState.isShowingItself()) {
				paintSelf(graphics, xThis, yThis);
			}
			if(visualState.isShowingDescendents()) {
				for(PaintLayer paintLayer : PaintLayer.values()) {
					for(Shape child : getChildren()) {
						if(paintLayer.equals(child.getVisualState().getPaintLayer())) {
							child.paint(graphics, xThis, yThis);							
						}
					}
				}				
			}
		}
	}

	public abstract void paintSelf(Graphics2D graphics, int xParent, int yParent);

	/**
	 * This method was created by a SmartGuide.
	 * @return cbit.vcell.graph.Shape
	 * @param x int
	 * @param y int
	 */
	public Shape pick(Point point) {
		if (isInside(point)==false) return null;
		Point childPoint = new Point(point.x-relativePos.x,point.y-relativePos.y);
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.elementAt(i);
			Shape childPick = child.pick(childPoint);
			if (childPick!=null) return childPick;
		}
		return this;	
	}


	/**
	 * This method was created in VisualAge.
	 */
	public abstract void refreshLabel();


	/**
	 * This method was created by a SmartGuide.
	 */
	public void removeAllChildren() {
		childShapeList.removeAllElements();
	}


	/**
	 * This method was created in VisualAge.
	 * @param shape cbit.vcell.graph.Shape
	 */
	public void removeChild(Shape shape) {
		if (childShapeList.contains(shape)){
			childShapeList.removeElement(shape);
		}
	}


	/**
	 * This method was created by a SmartGuide.
	 * @param g java.awt.Graphics
	 */
	public void resize(java.awt.Graphics2D g, Dimension newSize) throws Exception {

		int deltaX = newSize.width - shapeSize.width;
		int deltaY = newSize.height - shapeSize.height;

		shapeSize = newSize;
		//
		// allocate extra new space according to deep child count of children
		//
		Point totalDeepCount = getSeparatorDeepCount();
		for (int i=0;i<countChildren();i++){
			Shape child = childShapeList.elementAt(i);
			Point childDeepCount = child.getSeparatorDeepCount();
			Dimension childSize = new Dimension(child.shapeSize);
			childSize.width += deltaX*childDeepCount.x/totalDeepCount.x;
			childSize.height += deltaY*childDeepCount.y/totalDeepCount.y;
			child.resize(g,childSize);
		}
		layout();
	}


	/**
	 * This method was created by a SmartGuide.
	 */
	public void select() {
		backgroundColor = defaultBGselect;
		forgroundColor = defaultFGselect;
		bSelected = true;
		return;
	}


	/**
	 * This method was created in VisualAge.
	 */
	public void setDirty(boolean isDirty) {
		bDirty = isDirty;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @param label java.lang.String
	 */
	public final void setLabel(String label){
		this.label = label;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @param int x
	 * @param int y
	 */
	public void setLocation(Point screenPoint) {
		relativePos.x = screenPoint.x;
		relativePos.y = screenPoint.y;
		return;
	}


	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	@Override
	public String toString() {
		return getClass().getName()+"@"+Integer.toHexString(hashCode())+"("+getLabel()+")"; // ="+getModelObject();
	}


	/**
	 * This method was created by a SmartGuide.
	 */
	public void unselect() {
		backgroundColor = defaultBG;
		forgroundColor = defaultFG;
		bSelected = false;
	}

}
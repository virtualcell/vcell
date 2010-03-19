package org.vcell.sybil.gui.graph;

/*   SybilGraphCartoonTool  --- by Oliver Ruebenacker, UCHC --- April 2007 to February 2009
 *   Provide Panel for Graph, including buttons
 */

import org.vcell.sybil.gui.graph.nodes.NodeShape;

import java.awt.event.*;
import java.awt.*;
import java.util.Iterator;
import java.util.Set;

import javax.swing.*;

import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompContainer;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompSimpleContainer;
import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTagTool;
import org.vcell.sybil.util.exception.CatchUtil;
import org.vcell.sybil.util.iterators.BufferedIterator;

public class SybilGraphTool implements MouseListener, MouseMotionListener {
	
	protected boolean bMoving;
	protected Shape movingShape;
	protected Point movingPointWorld;
	protected Point movingOffsetWorld;

	protected boolean bRectStretch;
	protected RDFGraphCompContainer rubberBandContainer;
	protected RubberBandRectShape rectShape;

	protected Point endPointWorld;

	protected GraphPane graphPane;
	
	public SybilGraphTool() { rubberBandContainer = new RDFGraphCompSimpleContainer(new RDFGraphCompTagTool(this)); }
	
	private Graph graph() { return graphPane.graph(); }

	public void mouseClicked(MouseEvent event) {

		Point screenPoint = new Point(event.getX(), event.getY());
		screenToWorld(screenPoint);
		try {
			if ((event.getModifiers() & (MouseEvent.BUTTON2_MASK | MouseEvent.BUTTON3_MASK)) != 0) { 
				return; 
			}
			if (event.getClickCount() == 2){ graph().setChoice(event.getPoint()); }
		} catch (Exception e) {
			CatchUtil.handle(e);
			Point canvasLoc = graphPane().getLocationOnScreen();
			canvasLoc.x += screenPoint.x;
			canvasLoc.y += screenPoint.y;
		}				
	}


	public void mouseDragged(MouseEvent event) {
		if ((event.getModifiers() & (MouseEvent.BUTTON2_MASK | MouseEvent.BUTTON3_MASK)) != 0){ return; }
		boolean bShift = (event.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK;
		boolean bCntrl = (event.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK;
		try {
			Point worldPoint = screenToWorld(event.getX(),event.getY());
			if (bMoving) {
				Iterator<RDFGraphComponent> selectedComps 
				= new BufferedIterator<RDFGraphComponent>(graph().model().selectedComps().iterator());
				Shape confineShape = graph().topShape();
				Point confinePos = confineShape.p();
				Dimension confineSize = confineShape.size();
				worldPoint.x = Math.max(movingOffsetWorld.x + confinePos.x - confineSize.width/2,
						Math.min(movingOffsetWorld.x + confinePos.x + confineSize.width/2 
								- movingShape.size().width, worldPoint.x));
				worldPoint.y = Math.max(movingOffsetWorld.y + confinePos.y - confineSize.height/2,
						Math.min(movingOffsetWorld.x + confinePos.y + confineSize.height/2 - 
								movingShape.size().height, worldPoint.y));
				while(selectedComps.hasNext()) {
					Shape shape = graph().shapeMap().get(selectedComps.next());
					if (shape != movingShape) {
						int selectedMovingOffsetX = 
							movingOffsetWorld.x + (movingShape.p().x - shape.p().x);
						int selectedMovingOffsetY = 
							movingOffsetWorld.y + (movingShape.p().y - shape.p().y);
						worldPoint.x = Math.max(selectedMovingOffsetX + confinePos.x 
								- confineSize.width/2 + shape.size().width/2,
								Math.min(selectedMovingOffsetX + confinePos.x 
										+ confineSize.width/2 - shape.size().width/2, worldPoint.x));
						worldPoint.y = Math.max(selectedMovingOffsetY + confinePos.y 
								- confineSize.height/2 + shape.size().height/2,
								Math.min(selectedMovingOffsetY + confinePos.y 
										+ confineSize.height/2 - shape.size().height/2, worldPoint.y));
					}
				}

				graphPane().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				Point newMovingPoint = new Point(worldPoint.x - movingOffsetWorld.x, 
						worldPoint.y - movingOffsetWorld.y);
				int deltaX = newMovingPoint.x - movingPointWorld.x;
				int deltaY = newMovingPoint.y - movingPointWorld.y;
				movingPointWorld = newMovingPoint;
				movingShape.location().setP(movingShape.p().x + deltaX, movingShape.p().y + deltaY);
				selectedComps = new BufferedIterator<RDFGraphComponent>(graph().model().selectedComps().iterator());
				while(selectedComps.hasNext()) {
					Shape shape = graph().shapeMap().get(selectedComps.next());
					if (shape != movingShape) {
						shape.location().setP(shape.p().x + deltaX, shape.p().y + deltaY);
					}
				}
				graphPane().invalidate();
				((JViewport)graphPane().getParent()).revalidate();
				graphPane().repaint();
			} else if (bRectStretch) {
				Point parentLoc = rectShape.parent().p();
				Dimension parentSize = rectShape.parent().size();
				worldPoint.x = Math.max(1 - parentSize.width/2, Math.min(parentSize.width/2 - 1,
						worldPoint.x-parentLoc.x)) + parentLoc.x;
				worldPoint.y = Math.max(1 - parentSize.height/2, Math.min(parentSize.height/2 - 1,
						worldPoint.y-parentLoc.y)) + parentLoc.y;
				graphPane().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				//getGraphPane().repaint();
				Graphics2D g = (Graphics2D)graphPane().getGraphics();
				java.awt.geom.AffineTransform oldTransform = g.getTransform();
				g.scale(0.01*graph().zoomPercent(),0.01*graph().zoomPercent());
				g.setXORMode(Color.white);
				rectShape.drag(endPointWorld, g);
				rectShape.paint(g);
				endPointWorld = worldPoint;
				rectShape.drag(endPointWorld, g);
				rectShape.paint(g);
				g.setTransform(oldTransform);
			} else {
				graph().setChoice(worldPoint);
				Shape shape = 
					((graph().model().chosenComp() != null) ? 
							graph().chosenShape() : graph().getSelectedShape());
				if(!bCntrl && !bShift && (shape instanceof NodeShape)){
					bMoving=true;
					movingShape = shape;
					movingPointWorld = shape.p();
					movingOffsetWorld = new Point(worldPoint.x - movingPointWorld.x,
							worldPoint.y - movingPointWorld.y);
				} else {
					shape = graph().topShape();
					bRectStretch = true;
					endPointWorld = new Point(worldPoint.x+1,worldPoint.y+1);
					rectShape = new RubberBandRectShape(worldPoint, endPointWorld,graph(), 
							rubberBandContainer);
					shape.addChildShape(rectShape);
					Graphics2D g = (Graphics2D)graphPane().getGraphics();
					java.awt.geom.AffineTransform oldTransform = g.getTransform();
					g.scale(0.01*graph().zoomPercent(),0.01*graph().zoomPercent());
					g.setXORMode(Color.white);
					rectShape.drag(endPointWorld, g);
					rectShape.paint(g);
					g.setTransform(oldTransform);
				}		
			}		
		} catch (Exception e) {
			CatchUtil.handle(e);
		}			
	}

	public void mousePressed(MouseEvent event) {
		if(graph() == null) { return; }
		try {
			int eventX = event.getX();
			int eventY = event.getY();
			Point worldPoint = new Point((int)(eventX*100.0/graph().zoomPercent()),
					(int)(eventY*100.0/graph().zoomPercent()));
			boolean bShift = (event.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK;
			boolean bCntrl = (event.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK;
			selectEventFromWorld(worldPoint,bShift,bCntrl);
			if (event.isPopupTrigger()){
				return;
			}
		} catch (Exception e) {
			CatchUtil.handle(e);
		}				
	}

	public void mouseReleased(MouseEvent event) {
		if(graph() == null) { return; }
		try {
			if (event.isPopupTrigger()){ return; }
			if ((event.getModifiers() & (MouseEvent.BUTTON2_MASK | MouseEvent.BUTTON3_MASK)) != 0) { 
				return; 
			}
			graphPane().setCursor(Cursor.getDefaultCursor());
			if(bMoving) {
				graphPane().invalidate();
				((JViewport)graphPane().getParent()).revalidate();
				graphPane().repaint();
			} else if(bRectStretch) {
				Point absLoc = rectShape.p();
				Dimension size = rectShape.size();
				graph().removeShape(rectShape);
				Rectangle rect = new Rectangle(absLoc.x - size.width/2, absLoc.y - size.height/2, 
						size.width, size.height);
				boolean bShift = (event.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK;
				boolean bCntrl = (event.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK;
				selectEventFromWorld(rect, bShift, bCntrl);
				graphPane().repaint();
			} else {
				graph().removeShape(rectShape);
			}
			bMoving = false;
			movingShape = null;
			bRectStretch = false;
		} catch (Exception e) {
			CatchUtil.handle(e);
		}			

	}

	private void selectEventFromWorld(Point worldPoint, boolean bShift, boolean bCntrl) {
		if(graph() == null) { return; }
		Shape pickedShape = graph().pickWorld(worldPoint);
		if(bShift) {
			if (pickedShape == null){ return; }
			if (pickedShape instanceof ContainerShape){ return; }
			if(graph().getSelectedShape() instanceof ContainerShape) { 
				UIGraph<Shape, Graph> r = graph();
				r.model().selectedComps().clear();
				r.model().listenersUpdate(); 
			}
			graph().select(pickedShape);
		} else if(bCntrl) {
			if(pickedShape == null) { return; }
			if(pickedShape instanceof ContainerShape){ return; }
			if(pickedShape.isSelected()){ 
				UIGraph<Shape, Graph> r = graph();
				r.model().selectedComps().remove(pickedShape.graphComp()); r.model().listenersUpdate(); 
			} else { 
				graph().select(pickedShape); 
			}
		} else {
			if(pickedShape == null || !pickedShape.isSelected()) { 
				UIGraph<Shape, Graph> r = graph();
				r.model().selectedComps().clear();
				r.model().listenersUpdate(); 
			}
			if(pickedShape != null && pickedShape.isSelected()) { return; }
			if(pickedShape instanceof ContainerShape) { return; }
			if(pickedShape != null) { graph().select(pickedShape); }			
		}
	}

	private void selectEventFromWorld(Rectangle rect, boolean bShift, boolean bCntrl) {
		if (bShift){
			if(graph().getSelectedShape() instanceof ContainerShape) { 
				UIGraph<Shape, Graph> r = graph();
				r.model().selectedComps().clear();
				r.model().listenersUpdate(); 
			}
			Set<Shape> shapes = graph().pickWorld(rect);
			for(Shape shape : shapes) {
				if (shape instanceof ElipseShape){ graph().select(shape); }
			}
		} else if (bCntrl) {
			if(graph().getSelectedShape() instanceof ContainerShape) { 
				UIGraph<Shape, Graph> r = graph();
				r.model().selectedComps().clear();
				r.model().listenersUpdate(); 
			}
			Set<Shape> shapes = graph().pickWorld(rect);
			for(Shape shape : shapes) {
				if (shape instanceof ElipseShape){
					if (shape.isSelected()) { 
						UIGraph<Shape, Graph> r = graph();
						r.model().selectedComps().remove(shape.graphComp()); r.model().listenersUpdate(); 
					} else { 
						graph().select(shape); 
					}
				}
			}
		} else {
			UIGraph<Shape, Graph> r = graph();
			r.model().selectedComps().clear();
			r.model().listenersUpdate();
			Set<Shape> shapes = graph().pickWorld(rect);
			for(Shape shape : shapes) {
				if (shape instanceof ElipseShape) { graph().select(shape); }
			}			
		}
	}

	public void setGraphPane(GraphPane newGraphPane) {	
		if(graphPane != null) {
			graphPane.removeMouseListener(this);
			graphPane.removeMouseMotionListener(this);
		}
		graphPane = newGraphPane;
		if(graphPane != null) {
			graphPane.addMouseListener(this);
			graphPane.addMouseMotionListener(this);
		}
	}

	protected Point screenToWorld(int x, int y) {
		if(graph() == null) { return null; }
		double zoomUnscaling = 100.0/graph().zoomPercent();
		return new Point((int)(x*zoomUnscaling),(int)(y*zoomUnscaling));
	}

	protected Point screenToWorld(Point screenPoint) {
		if(graph() == null) { return null; }
		double zoomUnscaling = 100.0/graph().zoomPercent();
		return new Point((int) (screenPoint.x*zoomUnscaling), (int) (screenPoint.y*zoomUnscaling));
	}

	public GraphPane graphPane() { return graphPane; }

	public void mouseEntered(MouseEvent event) { }

	public void mouseExited(MouseEvent event) { }

	public void mouseMoved(MouseEvent event) { }

}
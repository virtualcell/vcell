package org.vcell.sybil.gui.graph;

/*   Graph  --- by Oliver Ruebenacker, UCHC --- July 2007 to November 2009
 *   Generic model for graph GUI. Base class for SybilGraph.
 */

import java.awt .Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.*;

import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompEdge;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompSingleThing;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompSimpleContainer;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompSimpleEdge;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompSimpleRelation;
import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTag;
import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTagModel;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.util.exception.CatchUtil;
import com.hp.hpl.jena.rdf.model.Statement;

import org.vcell.sybil.gui.graph.Shape;
import org.vcell.sybil.gui.graph.ShapeFactory;

public class Graph extends UIGraph<Shape, Graph> {

	private int zoomPercent = 100;

	public Graph(SBBox box) {
		super(box);
		startNewGraph();
	}

	public Shape createShape(RDFGraphComponent syCo) { return ShapeFactory.createShape(this, syCo); }
	
	public Dimension getPreferedSize(Graphics2D g) {
		Dimension dim = null;
		if (topShape() == null) {
			dim = new Dimension(1,1);
		} else {
			AffineTransform oldTransform = g.getTransform();
			g.scale(zoomPercent/100.0, zoomPercent/100.0);
			Dimension oldDim = topShape().getPreferedSize(g);
			g.setTransform(oldTransform);
			double newWidth = oldDim.width*(zoomPercent/100.0);
			double newHeight = oldDim.height*(zoomPercent/100.0);
			dim = new Dimension((int) newWidth, (int) newHeight);
		}
		return dim;
	}

	public int zoomPercent() { return zoomPercent; }

	public void paint(Graphics2D g, GraphPane canvas) {
		try {
			if (g == null){ throw new NullPointerException("Graphics is null"); }
			if (canvas == null){ throw new NullPointerException("Canvas is null"); }
			Shape topShape = topShape();
			if (topShape == null && canvas != null){
				canvas.clear(g);
				return;
			} else if (topShape != null){
				AffineTransform oldTransform = g.getTransform();
				g.scale(zoomPercent/100.0, zoomPercent/100.0);
				topShape.paintFamily(g);
				g.setTransform(oldTransform);
			}	
		} catch (ConcurrentModificationException e) { CatchUtil.handle(e, CatchUtil.RecordSilently); 
		} catch (Exception e) {
			CatchUtil.handle(e);
		}	
	}

	public Shape pickWorld(Point point) {
		Shape topShape = topShape();
		if (topShape == null) { return null; }
		return topShape.pick(point);
	}

	public Set<Shape> pickWorld(Rectangle rectWorld) {
		Shape topShape = topShape();
		if (topShape == null) return null;
		Set<Shape> pickedList = new HashSet<Shape>();
		Iterator<Shape> shapeIter = topShape().descendants();
		while(shapeIter.hasNext()) {
		    Shape shape = shapeIter.next();
			if (rectWorld.contains(shape.p())){
				pickedList.add(shape);
			}
		}
		return pickedList;
	}

	public void resize(Graphics2D g, Dimension sizeNew) throws Exception {
		if (topShape() != null){
			double newWidth = (100.0/zoomPercent)*sizeNew.getWidth();
			double newHeight = (100.0/zoomPercent)*sizeNew.getHeight();
			topShape().resize(g, new Dimension((int) newWidth, (int) newHeight));
		}
	}

	public void setZoomPercent(int zoomPercentNew) {
		if (zoomPercentNew<1 || zoomPercentNew>1000){
			throw new RuntimeException("zoomPercent must be between 1 and 1000");
		}
		zoomPercent = zoomPercentNew;
		updateView();
	}

	public void setChoice(Point point) {
		Shape shape = pickWorld(point);
		if(shape != null) { model.setChosenComp(shape.graphComp()); }
	}

	public void startNewGraph() {
		containerComp = new RDFGraphCompSimpleContainer(new RDFGraphCompTagModel(model()));
		containerShape = new SybilGraphContainerShape(this, containerComp);
		model.clear();
		shapeMap().put(containerComp, containerShape);
	}
	
	public void clear() { shapeMap.clear(); updateView(); }

	public void addEdge(SBBox box, NamedThing thing1, NamedThing thing2, Statement statement, RDFGraphCompTag tag) {
		RDFGraphCompEdge compEdge = new RDFGraphCompSimpleEdge(new RDFGraphCompSingleThing(thing1, tag), new RDFGraphCompSingleThing(thing2, tag), 
				new RDFGraphCompSimpleRelation(box, statement, tag), tag);
		if (!shapeMap().containsKey(compEdge)) { 
			if(!shapeMap().containsKey(compEdge.startComp())) { addComp(compEdge.startComp()); }
			if(!shapeMap().containsKey(compEdge.endComp())) { addComp(compEdge.endComp()); } 
			addComp(compEdge);
		}
	}

}
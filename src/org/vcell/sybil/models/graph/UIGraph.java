package org.vcell.sybil.models.graph;

/*   UIGraph  --- by Oliver Ruebenacker, UCHC --- July 2007 to March 2010
 *   Generic interface for graph independent of user interface
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.vcell.sybil.models.graph.Visibility.Hider;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompContainer;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompEdge;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompEdgeChain;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompThingGroup;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompThing;
import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTag;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.util.graphlayout.LayoutType;
import org.vcell.sybil.util.iterators.BufferedIterator;

import com.hp.hpl.jena.rdf.model.Statement;


public abstract class UIGraph<S extends UIShape<S>, G extends UIGraph<S, G>> 
implements GraphModel.Listener {

	public static class InvalidParentException extends Exception {
		public InvalidParentException(String message) { super(message); }
	
		private static final long serialVersionUID = -5168792135674432368L;
	}

	protected GraphModel model;
	protected Map<RDFGraphComponent, S> shapeMap = new HashMap<RDFGraphComponent, S>(); 
	protected Set<GraphListener<S, G>> listeners = new HashSet<GraphListener<S, G>>();
	protected RDFGraphCompContainer containerComp;
	protected S containerShape;

	public UIGraph(SBBox box) { 
		this.model = new GraphModel(box);
		model.listeners().add(this);
	}

	public GraphModel model() { return model; }
	
	public abstract void startNewGraph();
	
	public Set<GraphListener<S, G>> listeners() { return listeners; }
	
	public Map<RDFGraphComponent, S> shapeMap() { return shapeMap; }
	
	public RDFGraphComponent parentComp(RDFGraphComponent comp) {
		S shape = shapeMap().get(comp);
		S parentShape = shape.parent();
		if(parentShape != null) { return parentShape.graphComp(); }
		else { return containerComp(); }
	}
	
	public RDFGraphCompContainer containerComp() { return containerComp; }

	
	public void layoutGraph(LayoutType newLayout) { 
		for(GraphListener<S, G> viewer : listeners) { viewer.layoutGraph(newLayout); } 
	}
	
	public void updateView() { 
		for(GraphListener<S, G> viewer : listeners) { viewer.updateView(); } 
	}
	
	public int getNumShapes() { return topShape().countDescendants(); }

	public S getSelectedShape() {
		Iterator<RDFGraphComponent> compIter = model().selectedComps().iterator();
		if(compIter.hasNext()) {
			return shapeMap().get(compIter.next());
		}
		return null;
	}

	public Iterator<S> shapeIter() { return new BufferedIterator<S>(topShape().descendants()); }

	public S topShape() { return containerShape(); }

	public void select(S shape) {
		if(shape == null) { return; }
		if(shape.graphComp() == null) { return; }
		model.selectedComps().add(shape.graphComp());
		model.listenersUpdate();
	}

	protected abstract S createShape(RDFGraphComponent syCo);

	protected S addShape(RDFGraphComponent sybComp) {
		S shape = shapeMap().get(sybComp);
		if(shape == null) {
			Set<RDFGraphComponent> requiredComps = sybComp.dependencies();
			for(RDFGraphComponent requiredComp : requiredComps) {
				addShape(requiredComp); 
			}
			shape = createShape(sybComp);
			shapeMap().put(sybComp, shape);
			S parentShape = sybComp == containerComp() ? null : containerShape();
			if(parentShape != null) { parentShape.addChildShape(shape); }
		}
		for(NamedThing thing : sybComp.things()) { model.thingToComponentMap().add(thing, sybComp); }
		for(S dependency : shape.dependencies()) { dependency.dependents().add(shape); }
		return shape;
	}

	protected S addShapeToParent(RDFGraphComponent sybComp, RDFGraphComponent parentComp) throws InvalidParentException {
		if(parentComp == null) { throw new InvalidParentException("Parent Component is null"); }
		if(parentComp == sybComp) { 
			throw new InvalidParentException("Attempting to make shape its own parent"); 
		}		
		S parentShape = shapeMap().get(parentComp);
		if(parentShape == null) { 
			throw new InvalidParentException("Parent component has no shape assigned"); 
		}				
		S shape = shapeMap().get(sybComp);
		if(shape == null) {
			Set<RDFGraphComponent> requiredComps = sybComp.dependencies();
			for(RDFGraphComponent requiredComp : requiredComps) {
				addShape(requiredComp); 
			}
			shape = createShape(sybComp);
			shapeMap().put(sybComp, shape);
		} 
		if(parentShape != null) { parentShape.addChildShape(shape); }
		for(NamedThing thing : sybComp.things()) { model.thingToComponentMap().add(thing, sybComp); }
		for(S dependency : shape.dependencies()) { dependency.dependents().add(shape); }
		return shape;
	}


	public void addComp(RDFGraphComponent newComp) { 
		if(!shapeMap().containsKey(newComp)) { addShape(newComp); } 
	}

	public void removeShape(S shape) { if(shape != null) { removeShapeAndComp(shape, shape.graphComp()); } }

	public void removeComp(RDFGraphComponent oldComp) { removeShapeAndComp(shapeMap().get(oldComp), oldComp); }

	private void removeShapeAndComp(S shape, RDFGraphComponent sybComp) {
		if(shape != null) { 
			S parent = shape.parent();
			if(parent != null) { parent.children().remove(shape); }
		}		
		shapeMap().remove(sybComp);
		model.selectedComps().remove(sybComp);
		for(S dependency : shape.dependencies()) { dependency.dependents().remove(shape); }
		for(NamedThing thing : sybComp.things()) { model.thingToComponentMap().remove(thing, sybComp); }
	}

	public S chosenShape() { return shapeMap().get(model.chosenComp()); }

	public S containerShape() { return containerShape; }

	public void hideAll(Hider hider) {
		for(S shape : shapeMap().values()) {
			if(shape != topShape()) { 
				// shape.visibility().setHidesItself(true);
				shape.visibility().hiders().add(hider);
			}
		}		
	}
	
	public void hideComp(RDFGraphComponent comp, Hider hider) { hide(shapeMap().get(comp), hider); }

	public void hide(S shape, Hider hider) {
		if(shape != null) {
			// shape.visibility().setHidesItself(true);
			shape.visibility().hiders().add(hider);
			for(S dependent : shape.dependents()) { hide(dependent, hider); }
		}
	
	}

	public void unhide(RDFGraphComponent comp, Hider hider) { unhide(shapeMap().get(comp), hider); }

	public void unhide(S shape, Hider hider) {
		if(shape != null) {
			// shape.visibility().setHidesItself(false);
			shape.visibility().hiders().remove(hider);
			for(S dependency : shape.dependencies()) { unhide(dependency, hider); }
		}
	}

	public void unhideWithAncestors(RDFGraphComponent comp, Hider hider) { 
		unhideWithAncestors(shapeMap().get(comp), hider); 
	}

	public void unhideWithAncestors(S shape, Hider hider) {
		if(shape != null) {
			//shape.visibility().setHidesItself(false);
			shape.visibility().hiders().remove(hider);
			for(S dependency : shape.dependencies()) { unhide(dependency, hider); }
			S parentShape = shape.parent();
			if(parentShape != null) { unhideWithAncestors(parentShape, hider); }
		}
	}

	public void unhideNeighbors(RDFGraphComponent comp, Hider hider) { unhideNeighbors(shapeMap().get(comp), hider); }

	public void unhideNeighbors(S shape, Hider hider) {
		if(shape != null) {
			unhideWithAncestors(shape, hider);
			for(S dependent : shape.dependents()) { 
				unhideWithAncestors(dependent, hider); 
				for(S neighbor : dependent.dependencies()) {
					unhideWithAncestors(neighbor, hider);
					for(S neighborDependents : neighbor.dependents()) {
						unhideWithAncestors(neighborDependents, hider);						
					}
				}
			}
		}
	}

	public void unhideIfDependenciesActive(RDFGraphComponent comp, Hider hider) { 
		unhideIfDependenciesActive(shapeMap().get(comp), hider); 
	}

	public void unhideIfDependenciesActive(S shape, Hider hider) {
		if(shape != null) {
			boolean dependenciesActive = true;
			for(S dependency : shape.dependencies()) {
				dependenciesActive = dependenciesActive && (!dependency.visibility().isHidden());
			}
			if(dependenciesActive) { unhide(shape, hider); }
		}
	}

	public void unhideEdges(RDFGraphComponent comp, Hider hider) { unhideEdges(shapeMap().get(comp), hider); }

	public void unhideEdges(S shape, Hider hider) {
		if(shape != null) {
			for(S dependent : shape.dependents()) { unhideIfDependenciesActive(dependent, hider); }
		}
	}

	public void unhideAllComps(Hider hider) {
		for(S shape : shapeMap().values()) {
			//shape.visibility().setHidesItself(false);
			shape.visibility().hiders().remove(hider);
			shape.visibility().setHidesFamily(false);
		}
	}

	public S createEdgeChain(RDFGraphCompEdge egde, RDFGraphCompEdge minorEdge, RDFGraphCompTag tag) {
		RDFGraphComponent parent = parentComp(egde);
		try { return createEdgeChain(egde, minorEdge, parent, tag); } 
		catch (InvalidParentException e) { return null; }		
	}
		
	public S createEdgeChain(RDFGraphCompEdge edge, RDFGraphCompEdge edgeMinor, RDFGraphComponent parent, RDFGraphCompTag tag) 
	throws InvalidParentException {
		S edgeChainShape = null;
		if(edge != null && edge != parent) { 
			RDFGraphCompEdgeChain edgeChain = new RDFGraphCompEdgeChain(edge, edgeMinor, tag);
			edgeChainShape = addShapeToParent(edgeChain, parent);
			addShapeToParent(edge, edgeChain);
			addShapeToParent(edgeMinor, edgeChain);
			S subEdgeShape = addShapeToParent(edge.endComp(), edgeChain);
			subEdgeShape.setLocationIndependent(false);
			subEdgeShape.centerOn(edgeChainShape);
		}
		return edgeChainShape;
	}

	public S createNodeGroup(RDFGraphCompThing node, Set<RDFGraphComponent> children, RDFGraphCompTag tag) {
		RDFGraphComponent parent = parentComp(node);
		try { return createNodeGroup(node, children, parent, tag); } 
		catch (InvalidParentException e) { return null; }		
	}
		
	public S createNodeGroup(RDFGraphCompThing node, Set<RDFGraphComponent> children, RDFGraphComponent parent, RDFGraphCompTag tag) 
	throws InvalidParentException {
		S nodeGroupShape = null;
		if(node != null && node != parent) { 
			RDFGraphCompThingGroup nodeGroup = new RDFGraphCompThingGroup(node, tag);
			nodeGroup.children().addAll(children);
			nodeGroup.children().remove(nodeGroup);
			nodeGroupShape = addShapeToParent(nodeGroup, parent);
			S shapeHeir = addShapeToParent(node, nodeGroup);
			nodeGroupShape.centerOn(shapeHeir);
			shapeHeir.setLocationIndependent(false);
			for(RDFGraphComponent child : children) {
				if(child instanceof RDFGraphCompThing && child != node) {
					addShapeToParent(child, nodeGroup);
				}
			}
		}
		return nodeGroupShape;
	}

	public void collapse(RDFGraphComponent comp, Hider hider) { collapse(shapeMap().get(comp), hider); }

	public void collapse(S shape, Hider hider) {
		if(shape != null && shape != containerShape()) {
			// shape.visibility().setHidesItself(false);
			shape.visibility().hiders().remove(hider);
			shape.visibility().setAbsorbesFamily(true);
		}
	}
	
	public void explode(RDFGraphComponent comp, Hider hider) { explode(shapeMap().get(comp), hider); }

	public void explode(S shape, Hider hider) {
		if(shape != null && shape != containerShape()) {
			// shape.visibility().setHidesItself(true);
			shape.visibility().hiders().add(hider);
			shape.visibility().setAbsorbesFamily(false);
		}
	}

	public abstract void addEdge(SBBox box, NamedThing thing1, NamedThing thing2, Statement statement,
			RDFGraphCompTag tag);

	public int numOfElements() { return shapeMap().size() - 1; }
	
}

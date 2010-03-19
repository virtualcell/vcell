package org.vcell.sybil.gui.graph;

/*   ShapeFactory  --- by Oliver Ruebenacker, UCHC --- December 2007 to January 2009
 *   Creates Graph shapes out of SybComps
 */

import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompEdge;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompThing;
import org.vcell.sybil.rdf.schemas.BioPAX2;

import org.vcell.sybil.gui.graph.nodes.NodeShape;
import org.vcell.sybil.gui.graph.nodes.NodeShapeComplex;
import org.vcell.sybil.gui.graph.nodes.NodeShapeDefault;
import org.vcell.sybil.gui.graph.nodes.NodeShapeParticipant;
import org.vcell.sybil.gui.graph.nodes.NodeShapeProtein;
import org.vcell.sybil.gui.graph.nodes.NodeShapeReaction;
import org.vcell.sybil.gui.graph.nodes.NodeShapeReactionTransport;
import org.vcell.sybil.gui.graph.nodes.NodeShapeSmallMolecule;
import org.vcell.sybil.gui.graph.edges.EdgeShape;
import org.vcell.sybil.gui.graph.edges.EdgeShapeComponents;
import org.vcell.sybil.gui.graph.edges.EdgeShapeControlled;
import org.vcell.sybil.gui.graph.edges.EdgeShapeController;
import org.vcell.sybil.gui.graph.edges.EdgeShapeDefault;
import org.vcell.sybil.gui.graph.edges.EdgeShapeLeft;
import org.vcell.sybil.gui.graph.edges.EdgeShapeRight;
import org.vcell.sybil.gui.graph.nodes.NodeShapeTransport;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class ShapeFactory {

	public static Shape createShape(Graph graph, RDFGraphComponent sybComp) {
		if(sybComp instanceof RDFGraphCompEdge) { return createEdge(graph, ((RDFGraphCompEdge)sybComp)); }
		else { return createNode(graph, sybComp); }
	}
	
	public static NodeShape createNode(Graph graph, RDFGraphComponent comp) {
		NodeShape nodeShape = null;
		if(comp instanceof RDFGraphCompThing) { 
			RDFGraphCompThing compResource = (RDFGraphCompThing) comp;
			Resource type = compResource.type();
			if(type != null) {		
				if(BioPAX2.biochemicalReaction.equals(type))
					nodeShape = new NodeShapeReaction(graph, comp);
				else if(BioPAX2.conversion.equals(type))
					nodeShape = new NodeShapeReaction(graph, comp);
				else if(BioPAX2.complexAssembly.equals(type))
					nodeShape = new NodeShapeReaction(graph, comp);
				else if(BioPAX2.transport.equals(type))
					nodeShape = new NodeShapeTransport(graph, comp);
				else if(BioPAX2.transportWithBiochemicalReaction.equals(type))
					nodeShape = new NodeShapeReactionTransport(graph, comp);
				else if(BioPAX2.smallMolecule.equals(type))
					nodeShape = new NodeShapeSmallMolecule(graph, comp);
				else if(BioPAX2.protein.equals(type))
					nodeShape = new NodeShapeProtein(graph, comp);
				else if(BioPAX2.complex.equals(type))
					nodeShape = new NodeShapeComplex(graph, comp);
				else if(BioPAX2.sequenceParticipant.equals(type))
					nodeShape = new NodeShapeParticipant(graph, comp);
				else if(BioPAX2.physicalEntityParticipant.equals(type))
					nodeShape = new NodeShapeParticipant(graph, comp);
				else
					nodeShape = new NodeShapeDefault(graph, comp);
			}
		}
		if(nodeShape == null) { nodeShape = new NodeShapeDefault(graph, comp); }
		return nodeShape;
	}

	public static EdgeShape createEdge(Graph graph, RDFGraphCompEdge comp) {
		EdgeShape edgeShape = null;
		Property predicate = null;
		try { predicate = comp.statement().getPredicate(); }
		catch(NullPointerException e) {}
		if(BioPAX2.LEFT.equals(predicate)) {
			edgeShape = new EdgeShapeLeft(graph, comp);
		} else if(BioPAX2.RIGHT.equals(predicate)) {
			edgeShape = new EdgeShapeRight(graph, comp);			
		} else if(BioPAX2.CONTROLLER.equals(predicate)) {
			edgeShape = new EdgeShapeController(graph, comp);			
		} else if(BioPAX2.CONTROLLED.equals(predicate)) {
			edgeShape = new EdgeShapeControlled(graph, comp);			
		} else if(BioPAX2.COMPONENTS.equals(predicate)) {
			edgeShape = new EdgeShapeComponents(graph, comp);			
		}
		if(edgeShape == null) { edgeShape = new EdgeShapeDefault(graph, comp); }
		return edgeShape; 
	}
	
}

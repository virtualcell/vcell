package cbit.gui.graph;

import java.awt.Rectangle;

import org.vcell.util.graphlayout.ContainedGraph;
import org.vcell.util.graphlayout.ContainedGraph.Container;
import org.vcell.util.graphlayout.ContainedGraph.Node;

public class VCellGraphToContainedGraphMapper {

	protected final GraphModel vcellGraph;
	protected final ContainedGraph containedGraph;
	
	public VCellGraphToContainedGraphMapper(GraphModel vcellGraph, ContainedGraph containedGraph) {
		this.vcellGraph = vcellGraph;
		this.containedGraph = containedGraph;
	}

	public VCellGraphToContainedGraphMapper(GraphModel vcellGraph) {
		this(vcellGraph, new ContainedGraph.Default());
	}
	
	public GraphModel getVCellGraph() { return vcellGraph; }
	public ContainedGraph getContainedGraph() { return containedGraph; }
	
	public void updateContainedGraphFromVCellGraph() {
		containedGraph.clear();
		for(Shape shape : vcellGraph.getShapes()) {
			if(shape instanceof ContainerShape) {
				Rectangle boundary = vcellGraph.getContainerLayout().getBoundaryForAutomaticLayout(shape);
				Container container = containedGraph.addContainer(shape, boundary);
				boolean noChildIsContainer = true;
				for(Shape child : shape.getChildren()) {
					if(child instanceof ContainerShape) {
						noChildIsContainer = false;
					}
				}				
				if(noChildIsContainer) {
					for(Shape child : shape.getChildren()) {
						containedGraph.addNode(child, container, child.getSpaceManager().getAbsLoc(), 
								child.getSpaceManager().getSize());
					}					
				}
			}
		}
	}
	
	public void updateVCellGraphFromContainedGraph() {
		for(Node node : containedGraph.getNodes()) {
			Object object = node.getObject();
			if(object instanceof Shape) {
				Shape shape = (Shape) object;
				shape.getSpaceManager().setAbsLoc(node.getPos());
			}
		}
	}
	
}

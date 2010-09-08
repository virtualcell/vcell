package cbit.gui.graph.groups;

import java.util.Set;

import cbit.gui.graph.EdgeShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
import cbit.gui.graph.ShapeUtil;

public class EdgeBundleShape extends EdgeShape {

	protected final VCEdgeBundle edgeBundle;

	public EdgeBundleShape(GraphModel graphModel, Shape startShape,
			Shape endShape, VCEdgeBundle edgeBundle) {
		super(startShape, endShape, graphModel);
		this.edgeBundle = edgeBundle;
	}

	public EdgeBundleShape(GraphModel graphModel, Shape startShape,
			Shape endShape, String name, Set<EdgeShape> edgeShapes) {
		this(graphModel, startShape, endShape, ShapeUtil.createEdgeBundle(name, edgeShapes));
	}

	@Override
	public VCEdgeBundle getModelObject() {
		return edgeBundle;
	}

	@Override
	public void refreshLabel() {
		setLabel(edgeBundle.getName());
	}

}

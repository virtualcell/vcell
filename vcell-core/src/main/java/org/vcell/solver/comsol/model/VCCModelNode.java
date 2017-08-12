package org.vcell.solver.comsol.model;

public class VCCModelNode extends VCCBase {
	public enum SpatialOrder {
		linear
	}
	public final VCCModelNode.SpatialOrder spatialOrder;
	public final Boolean defineLocalCoord;
	public VCCModelNode(String name, VCCModelNode.SpatialOrder spatialOrder, boolean defineLocalCoord) {
		super(name);
		this.spatialOrder = spatialOrder;
		this.defineLocalCoord = defineLocalCoord;
	}
	public VCCModelNode(String name) {
		super(name);
		this.spatialOrder = null;
		this.defineLocalCoord = null;
	}
}
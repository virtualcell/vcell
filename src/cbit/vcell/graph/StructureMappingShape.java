package cbit.vcell.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import cbit.gui.graph.*;
import cbit.vcell.mapping.*;
/**
 * This type was created in VisualAge.
 */
public class StructureMappingShape extends EdgeShape {
	protected StructureMapping structureMapping = null;

	/**
	 * ReactionParticipantShape constructor comment.
	 * @param label java.lang.String
	 * @param graphModel cbit.vcell.graph.GraphModel
	 */
	public StructureMappingShape(StructureMapping structureMapping, StructureShape structureShape,
			GeometryClassLegendShape geometryClassShape, GraphModel graphModel) {
		super(structureShape, geometryClassShape, graphModel);
		this.structureMapping = structureMapping;
	}


	/**
	 * This method was created in VisualAge.
	 * @return cbit.vcell.graph.ReactionStepShape
	 */
	public StructureShape getStructureShape() {
		return (StructureShape)startShape;
	}


	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Object
	 */
	@Override
	public Object getModelObject() {
		return structureMapping;
	}

	/**
	 * This method was created in VisualAge.
	 */
	@Override
	public void refreshLabel() {
		setLabel("");
	}
}
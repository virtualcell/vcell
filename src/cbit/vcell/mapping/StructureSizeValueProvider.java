package cbit.vcell.mapping;

import cbit.vcell.mapping.MathMapping.StructureSizeValues;

public interface StructureSizeValueProvider {

	public abstract StructureSizeValues computeAbsoluteStructureSizeValues(
			SimulationContext simContext, String structureName2)
			throws MappingException;

}
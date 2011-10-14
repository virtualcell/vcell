package org.vcell.uome;

public class UOMEObjectPools {

	protected UOMEUnitPool unitPool = new UOMEUnitPool();
	protected UOMEExpressionPool expressionPool = new UOMEExpressionPool();
	
	public UOMEUnitPool getUnitPool() { return unitPool; }
	public UOMEExpressionPool getExpressionPool() { return expressionPool; }
	
}

package cbit.vcell.mapping;

import cbit.vcell.parser.Expression;
import cbit.vcell.units.VCUnitDefinition;

public interface UnitFactorProvider {
	
	Expression getUnitFactor(VCUnitDefinition unit);

}

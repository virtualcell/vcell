package cbit.vcell.units.parser;

import org.junit.Assert;
import org.junit.Test;

public class UnitSymbolTest {

	@Test
	public void testUnitSymbol( ) {
		Assert.assertEquals("uM*s^-1", new UnitSymbol("uM.s-1").getUnitSymbolAsInfix());
		Assert.assertEquals("dimensionless", new UnitSymbol("dimensionless").getUnitSymbolAsInfix());
		Assert.assertEquals("tbd", new UnitSymbol("tbd").getUnitSymbolAsInfix());
		Assert.assertEquals("m^2*s*item^-1", new UnitSymbol("m2.s.item-1").getUnitSymbolAsInfix());
	}

}

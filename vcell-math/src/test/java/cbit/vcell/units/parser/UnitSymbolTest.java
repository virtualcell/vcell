package cbit.vcell.units.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("Fast")
public class UnitSymbolTest {

	@Test
	public void testUnitSymbol( ) {
		assertEquals("uM*s^-1", new UnitSymbol("uM.s-1").getUnitSymbolAsInfix());
		assertEquals("dimensionless", new UnitSymbol("dimensionless").getUnitSymbolAsInfix());
		assertEquals("tbd", new UnitSymbol("tbd").getUnitSymbolAsInfix());
		assertEquals("m^2*s*item^-1", new UnitSymbol("m2.s.item-1").getUnitSymbolAsInfix());
	}

}

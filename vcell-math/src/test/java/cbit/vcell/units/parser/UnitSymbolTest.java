package cbit.vcell.units.parser;

import org.junit.Assert;
import org.junit.Test;

public class UnitSymbolTest {

	public UnitSymbolTest() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void example( ) {
		Assert.assertEquals("uM*s^-1", new UnitSymbol("uM.s-1").getUnitSymbolAsInfix());
		//us.dump("D");
	}

}

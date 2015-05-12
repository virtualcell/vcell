package cbit.vcell.units.parser;

import org.junit.Test;

public class UnitSymbolTest {

	public UnitSymbolTest() {
		// TODO Auto-generated constructor stub
	}
	
	private void dump(UnitSymbol us) {
		us.dump("D");
	}
	
	@Test
	public void example( ) {
		UnitSymbol us = new UnitSymbol("uM.s-1");
		dump(us);
	}

}

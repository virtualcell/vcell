package cbit.vcell.units;

import java.util.Iterator;

import org.junit.Test;

import ucar.units.Unit;

public class VCUnitTest {

	public VCUnitTest() {
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("serial")
	@Test
	public void loop( ) {
		VCUnitSystem tSystem = new VCUnitSystem() {};
		//tSystem.getInstance("mol");
		tSystem.getInstance("mole");
		Iterator<VCUnitDefinition> iter = tSystem.getKnownUnits();
		while (iter.hasNext()) {
			VCUnitDefinition ud = iter.next();
			String sym = ud.getSymbol( );
			Unit u = ud.getUcarUnit();
			System.out.println(sym + " = " + u);
		}
	}
}

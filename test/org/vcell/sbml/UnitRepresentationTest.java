package org.vcell.sbml;

import org.junit.Test;
import org.vcell.sbml.vcell.UnitRepresentation;
import org.vcell.sbml.vcell.UnitRepresentation.Fundamental;

import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitSystem;

/**
 * test suite for {@link UnitRepresentation}
 */
public class UnitRepresentationTest extends ConversionTest {
	private VCUnitSystem unitSystem = ModelUnitSystem.createDefaultVCModelUnitSystem();
	public UnitRepresentationTest() {
		super();
	}


	@Test(expected = NullPointerException.class)
	public void nTest( ) {
		process(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void constantTest( ) {
		process("7");
	}
	@Test
	public void itemTest( ) {
		process("item");
	}
	@Test
	public void molarTest( ) {
		process("M");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void emptyTest( ) {
		process("");
		
	}
	
	@Override
	protected void process(String us, UnitRepresentation unitRep) {
		for (Fundamental f : unitRep.getFundamentals()) {
			VCUnitDefinition vcUd = unitSystem.getInstance(f.unit);
			System.out.println("VC: " + vcUd.getSymbol());
		}
		
	}
}
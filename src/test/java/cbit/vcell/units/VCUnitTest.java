package cbit.vcell.units;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import ucar.units_vcell.Unit;

public class VCUnitTest {

	public VCUnitTest() {
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("serial")
	@Test
	public void loop( ) {
		VCUnitSystem tSystem = new VCUnitSystem() {};
		//tSystem.getInstance("mol");
		VCUnitDefinition unit1 = tSystem.getInstance("mole");
		VCUnitDefinition unit2 = tSystem.getInstance("uM.s-1");
		Iterator<VCUnitDefinition> iter = tSystem.getKnownUnits();
		VCUnitDefinition ud1 = iter.next();
		VCUnitDefinition ud2 = iter.next();
		Assert.assertFalse(iter.hasNext());
		Assert.assertTrue(	(ud1.compareEqual(unit1) && ud2.compareEqual(unit2)) ||
				  			(ud2.compareEqual(unit1) && ud1.compareEqual(unit2)));
		Assert.assertTrue(	(ud1 == unit1 && ud2 == unit2) ||
				  			(ud2 == unit1 && ud1 == unit2));
	}
}

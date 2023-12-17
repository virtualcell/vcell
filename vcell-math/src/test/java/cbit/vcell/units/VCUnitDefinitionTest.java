package cbit.vcell.units;

import org.junit.jupiter.api.Tag;
import java.util.Iterator;

import cbit.vcell.units.parser.UnitSymbol;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;

@Tag("Fast")
public class VCUnitDefinitionTest {

	@SuppressWarnings("serial")
	@Test
	public void testUnitSystem( ) {
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

	@Test
	public void testUnitSymbolParsing( ) {
		VCUnitSystem tSystem = new VCUnitSystem() {};
		Assert.assertEquals("uM.s-1", tSystem.getInstance("uM.s-1").getSymbol());
		Assert.assertEquals("dimensionless",  tSystem.getInstance("dimensionless").getSymbol());
		Assert.assertEquals("tbd",  tSystem.getInstance("").getSymbol());
		Assert.assertEquals("tbd",  tSystem.getInstance("tbd").getSymbol());
		Assert.assertEquals("m2.s.item-1",  tSystem.getInstance("m2.s.item-1").getSymbol());
		Assert.assertEquals("tbd",  tSystem.getInstance("0 m2.s.item-1").getSymbol());
		Assert.assertEquals("tbd",  tSystem.getInstance("0").getSymbol());
		Assert.assertEquals("tbd",  tSystem.getInstance("0.0 uM").getSymbol());
		Assert.assertEquals("tbd",  tSystem.getInstance("0.0").getSymbol());
	}

}

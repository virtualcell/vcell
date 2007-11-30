package org.vcell.sbml.test;

import cbit.vcell.units.SBMLUnitTranslator;
import cbit.vcell.units.VCUnitDefinition;


public class SBMLUnitTranslatorTest {

	static
	{
		System.loadLibrary("sbmlj");
	}


	public static void main(String[] args) {
		SBVCUnitConversionTest();

	}

	public static void SBVCUnitConversionTest() {
		java.util.Iterator<cbit.vcell.units.VCUnitDefinition> iter = cbit.vcell.units.VCUnitDefinition.getKnownUnits();
		while (iter.hasNext()) {
			cbit.vcell.units.VCUnitDefinition vcUnit = iter.next();
			org.sbml.libsbml.UnitDefinition sbmlUnit = cbit.vcell.units.SBMLUnitTranslator.getSBMLUnitDefinition(vcUnit);
			cbit.vcell.units.VCUnitDefinition newVCUnit = cbit.vcell.units.SBMLUnitTranslator.getVCUnitDefinition(sbmlUnit);
			if (vcUnit.convertTo(1.0, newVCUnit) != 1.0) {
				System.err.println("Failed Unit : " + vcUnit.getSymbol());
			}
			if (newVCUnit.convertTo(1.0, vcUnit) != 1.0) {
				System.err.println("Failed Unit : " + newVCUnit.getSymbol());
			}
			if (!vcUnit.compareEqual(newVCUnit)) {
				System.err.println("Failed to roundtrip : " + vcUnit.getSymbol());
			} else {
				System.out.println("PASSED roundtrip : " + newVCUnit.getSymbol());
			}
		}
	}

}

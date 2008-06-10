package org.vcell.physics.component;

import org.vcell.units.VCUnitDefinition;

import jscl.plugin.Expression;
import jscl.plugin.ParseException;

public class LeakChannel extends TwoPortElectricalComponent {

	public LeakChannel(String argName) {
		super(argName);
		Parameter gL = new Parameter("gL",VCUnitDefinition.UNIT_nS_per_um2);
		Parameter VL = new Parameter("VL",VCUnitDefinition.UNIT_mV);
		addSymbol(gL);
		addSymbol(VL);
		try {
			addEquation(Expression.valueOf("Ip(t) - gL * (V(t) - VL)"));
			addEquation(Expression.valueOf("gL - (-0.0030)"));
			addEquation(Expression.valueOf("VL - (-51.4)"));
		} catch (ParseException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}

}

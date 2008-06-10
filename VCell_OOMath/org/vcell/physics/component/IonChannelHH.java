package org.vcell.physics.component;

import org.vcell.units.VCUnitDefinition;

import jscl.plugin.Expression;
import jscl.plugin.ParseException;

public abstract class IonChannelHH extends TwoPortElectricalComponent {

	public IonChannelHH(String argName, int valenceValue) {
		super(argName);
		Variable Iin = new Variable("Iin(t)",VCUnitDefinition.UNIT_uM);
		Variable Iin_flux = new Variable("Iin_flux(t)",VCUnitDefinition.UNIT_uM_per_s);
		Variable Iout = new Variable("Iout(t)",VCUnitDefinition.UNIT_uM);
		Variable Iout_flux = new Variable("Iout_flux(t)",VCUnitDefinition.UNIT_uM_per_s);
		Parameter z = new Parameter("z",VCUnitDefinition.UNIT_nS_per_um2);
		Parameter R = new Parameter("R",VCUnitDefinition.UNIT_mV_C_per_K_per_mol);
		Parameter T = new Parameter("T",VCUnitDefinition.UNIT_K);
		Parameter F = new Parameter("F",VCUnitDefinition.UNIT_C_per_mol);
		Parameter F_nmol = new Parameter("F_nmol",VCUnitDefinition.UNIT_C_per_nmol);
		Variable conductivity = new Variable("conductivity(t)",VCUnitDefinition.UNIT_nS_per_um2);
		addSymbol(Iin);
		addSymbol(Iin_flux);
		addSymbol(Iout);
		addSymbol(Iout_flux);
		addSymbol(z);
		addSymbol(R);
		addSymbol(T);
		addSymbol(F);
		addSymbol(F_nmol);
		addSymbol(conductivity);
		Connector conIin = new Connector(this,"conn_Iin",Iin,Iin_flux);
		Connector conIout = new Connector(this,"conn_Iout",Iout,Iout_flux);
		addConnector(conIin);
		addConnector(conIout);
		try {
			addEquation(Expression.valueOf("z - "+valenceValue));
			addEquation(Expression.valueOf("R - 8314.0"));
			addEquation(Expression.valueOf("T - 273.0"));
			addEquation(Expression.valueOf("F - 9.648e4"));
			addEquation(Expression.valueOf("F_nmol - 9.648e-5"));
			addEquation(Expression.valueOf("Ip(t) - conductivity(t) * ( R*T/(z*F) * log(Iout(t)/Iin(t)) - V(t) )"));
			addEquation(Expression.valueOf("Iin_flux(t) - Ip(t)/(z * F_nmol)"));
			addEquation(Expression.valueOf("Iout_flux(t) + Iin_flux(t)"));
		} catch (ParseException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}

}

package org.vcell.physics.component;

import org.vcell.units.VCUnitDefinition;

import jscl.plugin.Expression;
import jscl.plugin.ParseException;

public abstract class IonChannelHH extends TwoPortElectricalComponent {
	public static final String CONNECTOR_ION_OUTSIDE_CONC = "conn_IonOutside_conc";
	public static final String CONNECTOR_ION_INSIDE_CONC = "conn_IonInside_conc";

	public IonChannelHH(String argName, int valenceValue) {
		super(argName);
		Variable Iin_conc = new Variable("Iin_conc(t)",VCUnitDefinition.UNIT_uM);
		Variable Iin_rate = new Variable("Iin_rate(t)",VCUnitDefinition.UNIT_molecules_per_s);
		Variable Iout_conc = new Variable("Iout_conc(t)",VCUnitDefinition.UNIT_uM);
		Variable Iout_rate = new Variable("Iout_rate(t)",VCUnitDefinition.UNIT_molecules_per_s);
		Parameter z = new Parameter("z",VCUnitDefinition.UNIT_DIMENSIONLESS);
		Parameter R = new Parameter("R",VCUnitDefinition.UNIT_mV_C_per_K_per_mol);
		Parameter T = new Parameter("T",VCUnitDefinition.UNIT_K);
		Parameter F = new Parameter("F",VCUnitDefinition.UNIT_C_per_mol);
		Parameter F_nmol = new Parameter("F_nmol",VCUnitDefinition.UNIT_C_per_nmol);
		Variable conductivity = new Variable("conductivity(t)",VCUnitDefinition.UNIT_nS);
		addSymbol(Iin_conc);
		addSymbol(Iin_rate);
		addSymbol(Iout_conc);
		addSymbol(Iout_rate);
		addSymbol(z);
		addSymbol(R);
		addSymbol(T);
		addSymbol(F);
		addSymbol(F_nmol);
		addSymbol(conductivity);
		Connector conIinConc = new Connector(this,CONNECTOR_ION_INSIDE_CONC,Iin_conc,Iin_rate);
		Connector conIoutConc = new Connector(this,CONNECTOR_ION_OUTSIDE_CONC,Iout_conc,Iout_rate);
		addConnector(conIinConc);
		addConnector(conIoutConc);
		try {
			addEquation(Expression.valueOf("z - "+valenceValue));
			addEquation(Expression.valueOf("R - 8314.0"));
			addEquation(Expression.valueOf("T - 300.0"));
			addEquation(Expression.valueOf("F - 9.648e4"));
			addEquation(Expression.valueOf("F_nmol - 9.648e-5"));
			addEquation(Expression.valueOf("Ip(t) - conductivity(t) * ( R*T/(z*F) * log(Iout_conc(t)/Iin_conc(t)) - V(t) )"));
			addEquation(Expression.valueOf("Iin_rate(t) - Ip(t)/(z * F_nmol)"));
			addEquation(Expression.valueOf("Iout_rate(t) + Iin_rate(t)"));
		} catch (ParseException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}

}

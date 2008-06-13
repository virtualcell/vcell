package org.vcell.physics.component;

import org.vcell.units.VCUnitDefinition;

import jscl.plugin.Expression;
import jscl.plugin.ParseException;

public class KChannel extends IonChannelHH {

	public KChannel(String argName) {
		super(argName, 1);
		
		double n_o_init_value = 0.304015731;
		
		Variable n_o = new Variable("n_o(t)",VCUnitDefinition.UNIT_DIMENSIONLESS);
		Variable n_o_init = new Variable("n_o(0)",VCUnitDefinition.UNIT_DIMENSIONLESS);
		Variable alpha_n = new Variable("alpha_n(t)",VCUnitDefinition.UNIT_per_s);
		Variable beta_n = new Variable("beta_n(t)",VCUnitDefinition.UNIT_per_s);
		Variable alpha_n_exp_arg = new Variable("alpha_n_exp_arg(t)",VCUnitDefinition.UNIT_DIMENSIONLESS);
		Variable V_n = new Variable("V_n(t)",VCUnitDefinition.UNIT_mV);
		Parameter Vrest_n = new Parameter("Vrest_n",VCUnitDefinition.UNIT_mV);
		
		Parameter gK_max = new Parameter("gK_max",VCUnitDefinition.UNIT_nS);
		
		addSymbol(n_o);
		addSymbol(n_o_init);
		addSymbol(alpha_n);
		addSymbol(beta_n);
		addSymbol(V_n);
		addSymbol(alpha_n_exp_arg);
		addSymbol(Vrest_n);

		addSymbol(gK_max);
		try {
			addEquation(Expression.valueOf("conductivity(t) - gK_max*(n_o(t))^4"));
			addEquation(Expression.valueOf("gK_max - (0.36)"));
			addEquation(Expression.valueOf("d(n_o(t),t) - (alpha_n(t)*(1-n_o(t)) - beta_n(t)*n_o(t))"));
			addEquation(Expression.valueOf("n_o(0) - ("+n_o_init_value+")"));
			addEquation(Expression.valueOf("alpha_n(t) - (((gt(1000*abs(alpha_n_exp_arg(t)),1))*(0.1*alpha_n_exp_arg(t) / (-1.0 + exp(alpha_n_exp_arg(t))))) + (le(1000*abs(alpha_n_exp_arg(t)),1))*0.1)"));
			addEquation(Expression.valueOf("alpha_n_exp_arg(t) - (0.1 * (10.0 - V_n(t)))"));
			addEquation(Expression.valueOf("beta_n(t) - (0.125 * exp(-0.0125 * V_n(t)))"));
			addEquation(Expression.valueOf("V_n(t) - (V(t) - Vrest_n)"));
			addEquation(Expression.valueOf("Vrest_n - (-62)"));
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

}

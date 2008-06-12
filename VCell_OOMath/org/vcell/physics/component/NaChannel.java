package org.vcell.physics.component;

import org.vcell.units.VCUnitDefinition;

import jscl.plugin.Expression;
import jscl.plugin.ParseException;

public class NaChannel extends IonChannelHH {

	public NaChannel(String argName) {
		super(argName, 1);

		double m_o_init_value = 0.04759071;
		double h_o_init_value = 0.627122591;
		
		Variable m_o = new Variable("m_o(t)",VCUnitDefinition.UNIT_DIMENSIONLESS);
		Variable m_o_init = new Variable("m_o(0)",VCUnitDefinition.UNIT_DIMENSIONLESS);
		Variable alpha_m = new Variable("alpha_m(t)",VCUnitDefinition.UNIT_per_s);
		Variable beta_m = new Variable("beta_m(t)",VCUnitDefinition.UNIT_per_s);
		Variable alpha_m_exp_arg = new Variable("alpha_m_exp_arg(t)",VCUnitDefinition.UNIT_DIMENSIONLESS);
		Variable beta_m_exp_arg = new Variable("beta_m_exp_arg(t)",VCUnitDefinition.UNIT_DIMENSIONLESS);
		Variable V_m = new Variable("V_m(t)",VCUnitDefinition.UNIT_mV);
		Parameter Vrest_m = new Parameter("Vrest_m",VCUnitDefinition.UNIT_mV);

		Variable h_o = new Variable("h_o(t)",VCUnitDefinition.UNIT_DIMENSIONLESS);
		Variable h_o_init = new Variable("h_o(0)",VCUnitDefinition.UNIT_DIMENSIONLESS);
		Variable alpha_h = new Variable("alpha_h(t)",VCUnitDefinition.UNIT_per_s);
		Variable beta_h = new Variable("beta_h(t)",VCUnitDefinition.UNIT_per_s);
		Variable alpha_h_exp_arg = new Variable("alpha_h_exp_arg(t)",VCUnitDefinition.UNIT_DIMENSIONLESS);
		Variable beta_h_exp_arg = new Variable("beta_h_exp_arg(t)",VCUnitDefinition.UNIT_DIMENSIONLESS);
		Variable V_h = new Variable("V_h(t)",VCUnitDefinition.UNIT_mV);
		Parameter Vrest_h = new Parameter("Vrest_h",VCUnitDefinition.UNIT_mV);
		
		Parameter gNa_max = new Parameter("gNa_max",VCUnitDefinition.UNIT_nS);
		
		addSymbol(m_o);
		addSymbol(m_o_init);
		addSymbol(alpha_m);
		addSymbol(beta_m);
		addSymbol(alpha_m_exp_arg);
		addSymbol(beta_m_exp_arg);
		addSymbol(V_m);
		addSymbol(Vrest_m);
		
		addSymbol(h_o);
		addSymbol(h_o_init);
		addSymbol(alpha_h);
		addSymbol(beta_h);
		addSymbol(alpha_h_exp_arg);
		addSymbol(beta_h_exp_arg);
		addSymbol(V_h);
		addSymbol(Vrest_h);
		
		addSymbol(gNa_max);
		try {
			addEquation(Expression.valueOf("conductivity(t) - gNa_max*(m_o(t))^3*h_o(t)"));
			addEquation(Expression.valueOf("gNa_max - (1.2)"));
			
			addEquation(Expression.valueOf("d(m_o(t),t) - (alpha_m(t)*(1-m_o(t)) - beta_m(t)*m_o(t))"));
			addEquation(Expression.valueOf("m_o(0) - ("+m_o_init_value+")"));
			addEquation(Expression.valueOf("alpha_m(t) - ((0.1 * (25.0 - V_m(t)) / (-1.0 + exp(alpha_m_exp_arg(t)))))"));
			addEquation(Expression.valueOf("alpha_m_exp_arg(t) - ((0.1 * (25.0 - V_m(t))))"));
			addEquation(Expression.valueOf("beta_m(t) - ((4.0 * exp(beta_m_exp_arg(t))))"));
			addEquation(Expression.valueOf("beta_m_exp_arg(t) - ( - (0.05555555555555555 * V_m(t)))"));
			addEquation(Expression.valueOf("V_m(t) - (V(t) - Vrest_m)"));
			addEquation(Expression.valueOf("Vrest_m - (-62)"));
			
			addEquation(Expression.valueOf("d(h_o(t),t) - (alpha_h(t)*(1-h_o(t)) - beta_h(t)*h_o(t))"));
			addEquation(Expression.valueOf("h_o(0) - ("+h_o_init_value+")"));
			addEquation(Expression.valueOf("alpha_h(t) - ((0.07 * exp(alpha_h_exp_arg(t))))"));
			addEquation(Expression.valueOf("alpha_h_exp_arg(t) - (- (0.05 * V_h(t)))"));
			addEquation(Expression.valueOf("beta_h(t) - (gt(1000*abs(beta_h_exp_arg(t)),1)*((1.0 / (1.0 + exp(beta_h_exp_arg(t))))))"));
			addEquation(Expression.valueOf("beta_h_exp_arg(t) - ((0.1 * (30.0 - V_h(t))))"));
			addEquation(Expression.valueOf("V_h(t) - (V(t) - Vrest_h)"));
			addEquation(Expression.valueOf("Vrest_h - (-62)"));
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

}

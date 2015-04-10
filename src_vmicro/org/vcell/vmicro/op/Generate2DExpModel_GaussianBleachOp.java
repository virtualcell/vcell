package org.vcell.vmicro.op;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

public class Generate2DExpModel_GaussianBleachOp extends Generate2DExpModelOpAbstract {
	
	protected Expression createBleachExpression(
			double bleachRadius,
			double bleachRate, 
			double bleachMonitorRate, 
			double bleachStart,
			double bleachEnd) throws ExpressionException {
		
		Expression bleachRateExp = new Expression(bleachMonitorRate+"*((t<"+bleachStart+")||(t>"+bleachEnd+")) + "+bleachRate+"*exp(-2*(x*x + y*y)/pow("+bleachRadius+"))*((t>="+bleachStart+")&&(t<="+bleachEnd+"))");
		return bleachRateExp;
	}

}

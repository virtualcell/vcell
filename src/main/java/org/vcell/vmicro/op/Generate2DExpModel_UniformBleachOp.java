package org.vcell.vmicro.op;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

public class Generate2DExpModel_UniformBleachOp extends Generate2DExpModelOpAbstract {
	

	protected Expression createBleachExpression(
			double bleachRadius,
			double bleachRate, 
			double bleachMonitorRate, 
			double bleachStart,
			double bleachEnd) throws ExpressionException {
		
		Expression bleachRateExp = new Expression(bleachMonitorRate+" + ((pow(x,2)+pow(y,2)<pow("+bleachRadius+",2))&&(t>="+bleachStart+")&&(t<="+bleachEnd+"))*("+(bleachRate-bleachMonitorRate)+")");
		return bleachRateExp;
	}

}

package org.vcell.physics.component;

import org.vcell.units.VCUnitDefinition;

import jscl.plugin.Expression;
import jscl.plugin.ParseException;

/**
 * Insert the type's description here.
 * Creation date: (1/14/2004 8:02:56 PM)
 * @author: Jim Schaff
 */
public class LumpedLocation extends Location {

/**
 * UnresolvedMembrane constructor comment.
 * @param argName java.lang.String
 */
public LumpedLocation(String argName, int dimension, double sizeValue) {
	super(argName,dimension);
	Variable size = null;
	switch(dimension){
	case 1: {
		size = new Variable("size",VCUnitDefinition.UNIT_um);
		break;
	}
	case 2: {
		size = new Variable("size",VCUnitDefinition.UNIT_um2);
		break;
	}
	case 3: {
		size = new Variable("size",VCUnitDefinition.UNIT_um3);
		break;
	}
	}
	addSymbol(size);
	Connector conn = new Connector(this,"conn_size",size,null);
	addConnector(conn);
	try {
		addEquation(Expression.valueOf("size - ("+sizeValue+")"));
	} catch (ParseException e) {
		e.printStackTrace();
		throw new RuntimeException(e.getMessage());
	}
}

}
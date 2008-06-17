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
	public final static String CONNECTOR_ELECTRICAL = "conn_elect";
	public final static String CONNECTOR_SIZE = "conn_size";

/**
 * UnresolvedMembrane constructor comment.
 * @param argName java.lang.String
 */
public LumpedLocation(String argName, int dimension, double sizeValue) {
	super(argName,dimension);
	Variable size = new Variable("size",getSizeUnit(dimension));
	addSymbol(size);
	addConnector(new Connector(this,CONNECTOR_SIZE,size,null));
	if (dimension==3){
		Variable V = new Variable("V(t)",VCUnitDefinition.UNIT_mV);
		addSymbol(V);
		addConnector(new Connector(this,CONNECTOR_ELECTRICAL,V,null));
	}
	try {
		addEquation(Expression.valueOf("size - ("+sizeValue+")"));
	} catch (ParseException e) {
		e.printStackTrace();
		throw new RuntimeException(e.getMessage());
	}
}


}
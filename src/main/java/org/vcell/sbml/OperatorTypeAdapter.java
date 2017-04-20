package org.vcell.sbml;

import org.sbml.libsbml.libsbmlConstants;

import cbit.vcell.geometry.CSGSetOperator.OperatorType;

/**
 * convert sbml code to {@link OperatorType}
 * @author gweatherby
 *
 */
public class OperatorTypeAdapter {
	public static OperatorType fromSbml(int code) {
		switch (code) {
		case libsbmlConstants.SPATIAL_SETOPERATION_UNION:
			return OperatorType.UNION;
		case libsbmlConstants.SPATIAL_SETOPERATION_RELATIVECOMPLEMENT:
			return OperatorType.DIFFERENCE;
		case libsbmlConstants.SPATIAL_SETOPERATION_INTERSECTION:
			return OperatorType.INTERSECTION;
		}
		throw new IllegalArgumentException("unsupported type code " + code);
	}

}

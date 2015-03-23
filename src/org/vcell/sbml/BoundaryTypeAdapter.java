package org.vcell.sbml;

import javax.help.UnsupportedOperationException;

import org.sbml.libsbml.libsbmlConstants;

import cbit.vcell.math.BoundaryConditionType;

/**
 * adapter between {@link BoundaryConditionType} and LibSbml values 
 * @author gweatherby
 *
 */
public class BoundaryTypeAdapter {

	/**
	 * @param sbmlCode
	 * @return equivalent type 
	 * @throws IllegalArgumentException if code not recognized
	 * @throws UnsupportedOperationException if virtual cell doesn't support 
	 */
	public static BoundaryConditionType fromSbml(int sbmlCode) {
		for (Equivalents e: Equivalents.values()) {
			if (e.sbmlCode == sbmlCode) {
				return convertOrThrow(e);
			}
		}
		throw new IllegalArgumentException("Unknown SBML boundary code " + sbmlCode);
	}

	/**
	 * @param e
	 * @return {@link Equivalents#boundaryConditionType} or exception if null
	 * @throws UnsupportedOperationException
	 */
	private static BoundaryConditionType convertOrThrow(Equivalents e) {
		if (e.boundaryConditionType != null) {
			return e.boundaryConditionType;
		}
		throw new UnsupportedOperationException("Virtual Cell does not support boundary condition " + e);
	}

	/**
	 * @param bct
	 * @return {@link Equivalents} type or {@link libsbmlConstants#BOUNDARYCONDITIONKIND_UNKNOWN}
	 */
	public static int fromBoundaryConditionType(BoundaryConditionType bct) {
		for (Equivalents e: Equivalents.values()) {
			if (bct.equals(e.boundaryConditionType)) {
				return e.sbmlCode;
			}
		}
		return Equivalents.UNKNOWN.sbmlCode; 
	}

	private static enum Equivalents {

		UNKNOWN(null,libsbmlConstants.BOUNDARYCONDITIONKIND_UNKNOWN),
		DIRICHLET(BoundaryConditionType.DIRICHLET, libsbmlConstants.SPATIAL_BOUNDARYKIND_DIRICHLET),
		NEUMANN(BoundaryConditionType.NEUMANN,libsbmlConstants.SPATIAL_BOUNDARYKIND_NEUMANN),
		ROBIN_VALUE(null,libsbmlConstants.SPATIAL_BOUNDARYKIND_ROBIN_VALUE_COEFFICIENT),
		ROBIN_SUM(null,libsbmlConstants.SPATIAL_BOUNDARYKIND_ROBIN_SUM),
		ROBIN_INWARD(null,libsbmlConstants.SPATIAL_BOUNDARYKIND_ROBIN_INWARD_NORMAL_GRADIENT_COEFFICIENT) 
		;

		BoundaryConditionType boundaryConditionType;
		int sbmlCode;

		private Equivalents(BoundaryConditionType boundaryConditionType, int sbmlCode) {
			this.boundaryConditionType = boundaryConditionType;
			this.sbmlCode = sbmlCode;
		}
	}
}

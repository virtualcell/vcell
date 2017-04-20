package org.vcell.sbml;

import org.sbml.libsbml.libsbmlConstants;
import org.vcell.util.VCAssert;

import cbit.vcell.mapping.SpeciesContextSpec;

/**
 * adapt SMBL codes to VCell types
 * @author gweatherby
 *
 */
public enum SpatialAdapter {
	SPATIAL_X(libsbmlConstants.SPATIAL_COORDINATEKIND_CARTESIAN_X,"x", SpeciesContextSpec.ROLE_VelocityX),
	SPATIAL_Y(libsbmlConstants.SPATIAL_COORDINATEKIND_CARTESIAN_Y,"y", SpeciesContextSpec.ROLE_VelocityY),
	SPATIAL_Z(libsbmlConstants.SPATIAL_COORDINATEKIND_CARTESIAN_Z,"z", SpeciesContextSpec.ROLE_VelocityZ),
	;
	
	/**
	 * number of enum values
	 */
	public final static int LENGTH = values( ).length;
	static {
		VCAssert.assertTrue(LENGTH >= 3, "must be at least three cartesian coordinates");
	}
	
	private final int sbmlCode;
	private final String name;
	private final int speciesContextVelocityRole; 

	
	private SpatialAdapter(int sbmlCode, String name, int speciesContextRole) {
		this.sbmlCode = sbmlCode;
		this.name = name;
		this.speciesContextVelocityRole = speciesContextRole;
	}
	
	public int sbmlType( ) {
		return sbmlCode;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * @return {@link SpeciesContextSpec} velocity role
	 */
	public int velocityRole( ) {
		return speciesContextVelocityRole;
		
	}

	/**
	 * return adapter corresponding to type or null
	 * @param sbmlType
	 * @return matching type or null
	 */
	public static SpatialAdapter fromSBMLType(int sbmlType) {
		for (SpatialAdapter sa: values( )) {
			if (sa.sbmlCode == sbmlType) {
				return sa;
			}
		}
		return null;
	}
	
	/**
	 * return adapter corresponding to type or throw exception 
	 * @param sbmlType
	 * @return matching type 
	 * @throws IllegalArgumentException
	 */
	public static SpatialAdapter requiredSBMLType(int sbmlType) {
		SpatialAdapter sa = fromSBMLType(sbmlType);
		if (sa != null) {
			return sa;
		}
		throw new IllegalArgumentException("Invalid SBML type code for Cartesian coordinate " + sbmlType);
	}

}

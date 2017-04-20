package org.vcell.util.document;

import org.vcell.util.BeanUtils;

/**
 * create extended identification information 
 * @author gweatherby
 */
public class VerboseDataIdentifier {
	/**
	 * return implementing class, id, and user info
	 * @param dataIdentifier
	 * @throws NullPointerException if param null
	 */
	public static String parse(VCDataIdentifier dataIdentifier) {
		return BeanUtils.leafName(dataIdentifier.getClass()) 
				+ " " + dataIdentifier.getID() 
				+ " " + dataIdentifier.getOwner().toString();
	}
	
	/**
	 * no need to create object
	 */
	private VerboseDataIdentifier( ) {}

}

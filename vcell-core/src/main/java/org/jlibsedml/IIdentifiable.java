package org.jlibsedml;

import org.jlibsedml.components.SId;

/**
 * Interface for typing any object in SED-ML that can be identified.
 * @author radams
 *
 */
public interface IIdentifiable {

	/**
	 * Gets a non-null, non-empty identifier for this object.
	 * @return a <code>String</code> of a unique identifier for this object.
	 */
    SId getId() ;
    String getIdAsString();
}

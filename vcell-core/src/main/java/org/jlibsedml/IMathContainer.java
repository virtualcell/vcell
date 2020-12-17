package org.jlibsedml;

import java.util.List;

import org.jmathml.ASTNode;

/**
 * Convenience interface for representing elements in SED-ML that contain math expressions.
 * @author radams
 *
 */
public interface IMathContainer {
	
	
	/**
	 * Gets list of identifiable variables. Implementing classes can define 
	 *  their own sub-type of {@link IIdentifiable}.
	 * @return A List<? extends IIdentifiable>.
	 */
	List <? extends IIdentifiable> getListOfVariables();
	
	/**
	 * Gets list of identifiable parameters. Implementing classes can define 
	 *  their sub-type.
	 * @return A List<? extends IIdentifiable>.
	 */
	List<? extends IIdentifiable> getListOfParameters();
	
	/**
	 * Gets an ASTNode for this object.
	 * @return An {@link ASTNode}.
	 */
	ASTNode getMath();
	
	/**
	 * Gets an identifier for this object.
	 * @return A non-null string.
	 */
	String getId();

}

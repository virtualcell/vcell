package org.jlibsedml.execution;

import java.net.URI;

/**
 * Interface for obtaining a model from a given URI. Clients can implement this interface
 *  to resolve models from specific locations or repositories.
 *   Implementations of {@link AbstractSedmlExecutor} can add model resolvers by
 *    calling <br/>
 *    <pre>
 *    exe.addModelResolver(IModelResolver resolver);
 *    </pre>
 *    
 *    This package provides a default implementation for resolving models from
 *     locations on the file-system.
 * @author radams
 *
 */
public interface IModelResolver {
	
	/**
	 * Attempts to find a model's contents based upon the argument URI.
	 * @param modelURI A non-null {@link URI} element.
	 * @return a <code>String</code> representation of the model XML or <code>null</code> if not found.
	 */
	String getModelXMLFor(URI modelURI);
	
}

package org.jmathml;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for registering domain specific symbols. Clients implement an
 * ASTSymbolFactory capable of instantiating particular symbols and add the
 * factory to the registry before manipulating MathML.
 * 
 * @author radams
 *
 */
public class SymbolRegistry {

	private static SymbolRegistry instance;
	private List<ASTSymbolFactory> listOfFactories = new ArrayList<ASTSymbolFactory>();

	private SymbolRegistry() {

	}

	/**
	 * Public accessor for the singleton instance of this class.
	 * 
	 * @return The {@link SymbolRegistry}
	 */
	public static SymbolRegistry getInstance() {
		if (instance == null) {
			instance = new SymbolRegistry();
		}
		return instance;
	}

	/**
	 * Adds a factory to the registry if it is not already in the registry.
	 * 
	 * @param factory
	 *            An {@link ASTSymbolFactory}
	 * @return <code>true</code> if factory was added successfully,
	 *         <code>false</code> otherwise
	 */
	public final boolean addSymbolFactory(ASTSymbolFactory factory) {
		if (!listOfFactories.contains(factory))
			return listOfFactories.add(factory);
		return false;
	}

	/**
	 * REmoves the argument from the registry
	 * 
	 * @param factory
	 *            An {@link ASTSymbolFactory}
	 * @return <code>true</code> if the argument {@link ASTSymbolFactory} was in
	 *         the registry, false otherwise.
	 */
	public final boolean removeSymbolFactory(ASTSymbolFactory factory) {
		return listOfFactories.remove(factory);
	}

	void clearFactories() {
		listOfFactories.clear();
	}

	/**
	 * Attempts to create an {@link ASTSymbol} function using the urlEncoding
	 * attribute of a MathML symbol.
	 * 
	 * @param urlEncoding
	 *            A non-null string encoding of the symbol to be created.
	 * @param symbolName
	 *            The text value of the csymbol element. For example, in the
	 *            csymbol:
	 * 
	 *            <pre>
	 * 	&lt;csymbol definitionURL="http://sed-ml.org/#min" encoding="text"&gt;
	 *     	  min
	 *     	 &lt;/csymbol&gt;
	 * </pre>
	 * 
	 *            , the symbolName is 'min'
	 * @return An {@link ASTSymbol} or <code>null</code> if the symbol could not
	 *         be created.
	 * @throws IllegalArgumentException
	 *             if <code>urlEncoding</code> or <code>symbolName</code> is
	 *             <code>null</code>.
	 * @since 2.1
	 */
	ASTSymbol createSymbolFor(String urlEncoding, String symbolName) {
		for (ASTSymbolFactory fac : listOfFactories) {
			if (fac.canCreateSymbol(urlEncoding)) {
				return fac.createSymbol(urlEncoding);
			}
		}
		return null;
	}

	/**
	 * Attempts to create an {@link ASTSymbol} function using the urlEncoding
	 * attribute of a MathML symbol.
	 * 
	 * @param urlEncoding
	 *            A non-null string encoding of the symbol to be created.
	 * @return An {@link ASTSymbol} or <code>null</code> if the symbol could not
	 *         be created.
	 * @throws IllegalArgumentException
	 *             if <code>urlEncoding</code> is <code>null</code>.
	 * @deprecated Use createSymbolFor(String urlEncoding,String symbolName)
	 *             instead
	 */
	ASTSymbol createSymbolFor(String urlEncoding) {
		for (ASTSymbolFactory fac : listOfFactories) {
			if (fac.canCreateSymbol(urlEncoding)) {
				return fac.createSymbol(urlEncoding);
			}
		}
		return null;
	}

}

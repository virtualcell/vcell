package org.jlibsedml.mathsymbols;

import org.jmathml.ASTSymbol;
import org.jmathml.ASTSymbolFactory;

/**
 * A SED-ML specific symbol factory for creating SED-ML specific MathML symbols.<br/>
 * To use this factory, clients should  add it to a jmathML SymbolRegistry.
 * @author radams
 *
 */
public class SedMLSymbolFactory extends ASTSymbolFactory {

    /**
     * Default constructor.
     */
	public SedMLSymbolFactory() {
		super("org.jlibsedml.symbol");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ASTSymbol createSymbol(String urlEncoding) {
		if (urlEncoding.contains("min")) {
			return new MinSymbol("min");
		} else if (urlEncoding.contains("max")) {
			return new MaxSymbol("max");
		}
		if (urlEncoding.contains("sum")) {
			return new SumSymbol("sum");
		}
		if (urlEncoding.contains("product")) {
			return new ProductSymbol("product");
		}
		return null;
	}

	protected boolean canCreateSymbol(String urlEncoding) {
		return createSymbol(urlEncoding) != null;

	}

}

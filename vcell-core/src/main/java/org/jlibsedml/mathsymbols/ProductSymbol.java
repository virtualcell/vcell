package org.jlibsedml.mathsymbols;

import org.jmathml.ASTNumber;
import org.jmathml.IEvaluationContext;


/**
 * Custom Math-ML symbol specifying the 'product' function on a vector.
 * @author radams
 *
 */
public class ProductSymbol extends SedMLSymbol  {
	
		 ProductSymbol(String id) {
		super(id);
		setEncoding("text");
		setDefinitionURL("http://sed-ml.org/#product");
		// TODO Auto-generated constructor stub
	}

		@Override
		 protected ASTNumber doEvaluate(IEvaluationContext context) {
			double rc = 1;
			for (Double val: context.getValueFor(firstChild().getName())){
				rc *= val;
			}
			return ASTNumber.createNumber(rc);
		}
		
	
		
	
		
		

	
}

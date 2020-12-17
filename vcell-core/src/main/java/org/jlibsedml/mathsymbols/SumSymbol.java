package org.jlibsedml.mathsymbols;

import org.jmathml.ASTNumber;
import org.jmathml.IEvaluationContext;

/**
 * Custom Math-ML symbol specifying the 'sum' function on a vector.
 * @author radams
 *
 */
public class SumSymbol extends SedMLSymbol  {
	
		 SumSymbol(String id) {
		super(id);
		setEncoding("text");
		setDefinitionURL("http://sed-ml.org/#sum");
	}

		@Override
		 protected ASTNumber doEvaluate(IEvaluationContext context) {
			double rc = 0;
			for (Double val: context.getValueFor(firstChild().getName())){
				rc += val;
			}
			return ASTNumber.createNumber(rc);
		}
		
	
		
	
		
		

	
}

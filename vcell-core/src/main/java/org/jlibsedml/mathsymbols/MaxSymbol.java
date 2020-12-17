package org.jlibsedml.mathsymbols;

import org.jmathml.ASTNumber;
import org.jmathml.IEvaluationContext;

/**
 * Custom Math-ML symbol specifying the 'max' function on a vector.
 * @author radams
 *
 */
public class MaxSymbol extends SedMLSymbol  {
	
        /**
         * 
         * @param id 
         */
		 MaxSymbol(String id) {
		super(id);
		setEncoding("text");
		setDefinitionURL("http://sed-ml.org/#max");
		// TODO Auto-generated constructor stub
	}

		@Override
		 protected ASTNumber doEvaluate(IEvaluationContext context) {
			double rc = Double.MIN_VALUE;
			for (Double val: context.getValueFor(firstChild().getName())){
				rc = val > rc ?val:rc;
			}
			return ASTNumber.createNumber(rc);
		}
		
	
		
	
		
		

	
}

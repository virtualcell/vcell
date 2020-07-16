package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.NotIntegrableException;
import jscl.math.Variable;

public class IndefiniteIntegral extends Operator {
    public IndefiniteIntegral(Generic expression, Generic variable) {
        super("integral",new Generic[] {expression,variable});
    }

    public Generic compute() {
        Variable variable=parameter[1].variableValue();
        try {
            return parameter[0].antiderivative(variable);
        } catch (NotIntegrableException e) {}
        return expressionValue();
    }

    public String toMathML() {
        Variable v=parameter[1].variableValue();
	StringBuffer b = new StringBuffer();
	b.append("<apply><int/><bvar>");
        b.append(v.toMathML());
        b.append("</bvar>");
        b.append(parameter[0].toMathML());
	b.append("</apply>");
	return b.toString();
    }

    protected Variable newinstance() {
        return new IndefiniteIntegral(null,null);
    }
}

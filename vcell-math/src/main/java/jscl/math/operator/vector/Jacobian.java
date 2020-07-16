package jscl.math.operator.vector;

import jscl.math.Generic;
import jscl.math.JSCLVector;
import jscl.math.NotVectorException;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;

public class Jacobian extends VectorOperator {
    public Jacobian(Generic vector, Generic variable) {
        super("jacobian",new Generic[] {vector,variable});
    }

    public Generic compute() {
        Variable variable[]=variables(parameter[1].vectorValue());
        try {
            JSCLVector vector=parameter[0].vectorValue();
            return vector.jacobian(variable);
        } catch (final NotVectorException e) {}
        return expressionValue();
    }

    @Override
    public String toMathML() {
        StringBuffer b = new StringBuffer();
        b.append("<apply>");
	b.append("<apply><transpose/>");
        b.append(operator("nabla").toMathML());
        b.append("</apply>");
	b.append("<apply><transpose/>");
        b.append(parameter[0].toMathML());
        b.append("</apply>");
        b.append("</apply>");
        return b.toString();
    }

    protected Variable newinstance() {
        return new Jacobian(null,null);
    }
}

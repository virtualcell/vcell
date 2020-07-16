package jscl.math.operator.vector;

import jscl.math.Generic;
import jscl.math.JSCLVector;
import jscl.math.NotVectorException;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;

public class Curl extends VectorOperator {
    public Curl(Generic vector, Generic variable) {
        super("curl",new Generic[] {vector,variable});
    }

    public Generic compute() {
        Variable variable[]=variables(parameter[1].vectorValue());
        try {
            JSCLVector vector=parameter[0].vectorValue();
            return vector.curl(variable);
        } catch (final NotVectorException e) {}
        return expressionValue();
    }

    @Override
    public String toMathML() {
        StringBuffer b = new StringBuffer();
        b.append("<apply><curl/>");
        b.append(parameter[0].toMathML());
        b.append(parameter[1].toMathML());
        b.append("</apply>");
        return b.toString();
    }

    protected Variable newinstance() {
        return new Curl(null,null);
    }
}

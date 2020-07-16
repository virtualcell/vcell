package jscl.math.operator.vector;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;

public class Grad extends VectorOperator {
    public Grad(Generic expression, Generic variable) {
        super("grad",new Generic[] {expression,variable});
    }

    public Generic compute() {
        Variable variable[]=variables(parameter[1].vectorValue());
        Expression expression=parameter[0].expressionValue();
        return expression.grad(variable);
    }

    @Override
    public String toMathML() {
        StringBuffer b = new StringBuffer();
        b.append("<apply><grad/>");
        b.append(parameter[0].toMathML());
        b.append(parameter[1].toMathML());
        b.append("</apply>");
        return b.toString();
    }

    protected Variable newinstance() {
        return new Grad(null,null);
    }
}

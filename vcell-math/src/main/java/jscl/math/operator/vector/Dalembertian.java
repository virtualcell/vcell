package jscl.math.operator.vector;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;

public class Dalembertian extends VectorOperator {
    public Dalembertian(Generic expression, Generic variable) {
        super("dalembertian",new Generic[] {expression,variable});
    }

    public Generic compute() {
        Variable variable[]=variables(parameter[1].vectorValue());
        Expression expression=parameter[0].expressionValue();
        return expression.dalembertian(variable);
    }

    @Override
    public String toMathML() {
        StringBuffer b = new StringBuffer();
        b.append("<apply>");
        b.append(operator("square").toMathML());
        b.append(parameter[0].toMathML());
        b.append("</apply>");
        return b.toString();
    }

    protected Variable newinstance() {
        return new Dalembertian(null,null);
    }
}

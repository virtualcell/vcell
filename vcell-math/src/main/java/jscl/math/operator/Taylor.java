package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.TechnicalVariable;
import jscl.math.function.Pow;
import jscl.math.Variable;

public class Taylor extends Operator {
    public Taylor(Generic expression, Generic variable, Generic value, Generic order) {
        super("taylor",new Generic[] {expression,variable,value,order});
    }

    public Generic compute() {
        Generic i=new TechnicalVariable("i").expressionValue();
        return new Sum(new Derivative(parameter[0], parameter[1], parameter[2], i).expressionValue().divide(new Factorial(i).expressionValue()).multiply(new Pow(parameter[1].subtract(parameter[2]), i).expressionValue()), i, JSCLInteger.valueOf(0), parameter[3]).expressionValue().expand();
    }

    protected Variable newinstance() {
        return new Taylor(null,null,null,null);
    }
}

package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JSCLBoolean;
import jscl.math.Variable;

public class FactorOf extends Operator {
    public FactorOf(Generic expression1, Generic expression2) {
        super("factorof",new Generic[] {expression1,expression2});
    }

    public Generic compute() {
        try {
            return JSCLBoolean.valueOf(parameter[1].multiple(parameter[0]));
        } catch (ArithmeticException e) {}
        return expressionValue();
    }

    protected Variable newinstance() {
        return new FactorOf(null,null);
    }
}

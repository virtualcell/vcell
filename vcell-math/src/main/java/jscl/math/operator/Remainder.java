package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.Variable;

public class Remainder extends Operator {
    public Remainder(Generic expression1, Generic expression2) {
        super("rem",new Generic[] {expression1,expression2});
    }

    public Generic compute() {
        return parameter[0].remainder(parameter[1]);
    }

    protected Variable newinstance() {
        return new Remainder(null,null);
    }
}

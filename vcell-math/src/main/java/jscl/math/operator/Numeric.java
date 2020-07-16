package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.Variable;

public class Numeric extends Operator {
    public Numeric(Generic expression) {
        super("numeric",new Generic[] {expression});
    }

    public Generic compute() {
        return parameter[0].numeric();
    }

    protected Variable newinstance() {
        return new Numeric(null);
    }
}

package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.Variable;

public class Simplify extends Operator {
    public Simplify(Generic expression) {
        super("simplify",new Generic[] {expression});
    }

    public Generic compute() {
        return parameter[0].simplify();
    }

    protected Variable newinstance() {
        return new Simplify(null);
    }
}

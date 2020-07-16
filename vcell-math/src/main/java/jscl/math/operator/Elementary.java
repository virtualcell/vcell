package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.Variable;

public class Elementary extends Operator {
    public Elementary(Generic expression) {
        super("elementary",new Generic[] {expression});
    }

    public Generic compute() {
        return parameter[0].elementary();
    }

    protected Variable newinstance() {
        return new Elementary(null);
    }
}

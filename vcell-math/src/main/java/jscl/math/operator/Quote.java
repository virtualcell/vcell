package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.Variable;

public class Quote extends Operator {
    public Quote(Generic expression) {
        super("quote",new Generic[] {expression});
    }

    public Generic compute() {
        return parameter[0];
    }

    public Generic expand() {
        return compute();
    }

    protected Variable newinstance() {
        return new Quote(null);
    }
}

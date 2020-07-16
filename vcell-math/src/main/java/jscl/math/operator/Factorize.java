package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.Variable;

public class Factorize extends Operator {
    public Factorize(Generic expression) {
        super("factorize",new Generic[] {expression});
    }

    public Generic compute() {
        return parameter[0].factorize();
    }

    protected Variable newinstance() {
        return new Factorize(null);
    }
}

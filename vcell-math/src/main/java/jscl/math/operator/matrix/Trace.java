package jscl.math.operator.matrix;

import jscl.math.Generic;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.Operator;

public class Trace extends Operator {
    public Trace(Generic matrix) {
        super("trace",new Generic[] {matrix});
    }

    public Generic compute() {
        if(parameter[0] instanceof Matrix) {
            Matrix matrix=(Matrix)parameter[0];
            return matrix.trace();
        }
        return expressionValue();
    }

    protected Variable newinstance() {
        return new Trace(null);
    }
}

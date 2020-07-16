package jscl.math.function.bool;

import jscl.math.Generic;
import jscl.math.JSCLBoolean;
import jscl.math.NotBooleanException;
import jscl.math.NumericWrapper;
import jscl.math.function.Bool;
import jscl.math.Variable;

public class Not extends Bool {
    public Not(Generic expression) {
        super("not",new Generic[] {expression});
    }

    public Generic evaluate() {
        try {
            return parameter[0].booleanValue().not();
        } catch (NotBooleanException e) {}
        return expressionValue();
    }

    public Generic evalfunc() {
        return new jscl.math.Function() {
            public double apply(double value) {
                return JSCLBoolean.valueOf(((jscl.math.Function)parameter[0]).apply(value) != 0).not().content().doubleValue();
            }
        };
    }

    public Generic evalnum() {
        return new NumericWrapper(JSCLBoolean.valueOf(((NumericWrapper)parameter[0]).signum() != 0).not());
    }

    protected Variable newinstance() {
        return new Not(null);
    }
}

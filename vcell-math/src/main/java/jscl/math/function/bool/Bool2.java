package jscl.math.function.bool;

import jscl.math.Generic;
import jscl.math.JSCLBoolean;
import jscl.math.NotBooleanException;
import jscl.math.NumericWrapper;
import jscl.math.function.Bool;

public abstract class Bool2 extends Bool {
    protected Bool2(String name, Generic expression1, Generic expression2) {
        super(name,new Generic[] {expression1,expression2});
    }

    protected abstract JSCLBoolean apply(JSCLBoolean a, JSCLBoolean b);

    public Generic evaluate() {
        try {
            return apply(parameter[0].booleanValue(),parameter[1].booleanValue());
        } catch (NotBooleanException e) {}
        return expressionValue();
    }

    public Generic evalfunc() {
        return new jscl.math.Function() {
            public double apply(double value) {
                return Bool2.this.apply(JSCLBoolean.valueOf(((jscl.math.Function)parameter[0]).apply(value) != 0),JSCLBoolean.valueOf(((jscl.math.Function)parameter[1]).apply(value) != 0)).content().doubleValue();
            }
        };
    }

    public Generic evalnum() {
        return new NumericWrapper(apply(JSCLBoolean.valueOf(((NumericWrapper)parameter[0]).signum() != 0),JSCLBoolean.valueOf(((NumericWrapper)parameter[1]).signum() != 0)));
    }
}

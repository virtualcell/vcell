package jscl.math.function;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotDivisibleException;
import jscl.math.Variable;

public class Inv extends Frac {
    public Inv(Generic generic) {
        super(JSCLInteger.valueOf(1),generic);
    }

    public Generic evaluate() {
        if(JSCLInteger.valueOf(1).multiple(parameter())) {
            return JSCLInteger.valueOf(1).divide(parameter());
        }
        return expressionValue();
    }

    public Generic parameter() {
        return parameter[1];
    }

    protected Variable newinstance() {
        return new Inv(null);
    }
}

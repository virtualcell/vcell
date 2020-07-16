package jscl.math.function;

import jscl.math.Generic;
import jscl.math.JSCLBoolean;
import jscl.math.NotIntegrableException;

public abstract class Bool extends Function {
    protected Bool(String name, Generic parameter[]) {
        super(name,parameter);
    }

    public Generic antiderivative(int n) throws NotIntegrableException {
        throw new NotIntegrableException();
    }

    public Generic derivative(int n) {
        return JSCLBoolean.valueOf(0);
    }

    public Generic evalelem() {
        return expressionValue();
    }

    public Generic evalsimp() {
        return expressionValue();
    }
}

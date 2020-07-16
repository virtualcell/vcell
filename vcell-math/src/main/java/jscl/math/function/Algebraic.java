package jscl.math.function;

import jscl.math.Generic;
import jscl.math.NotIntegrableException;

public abstract class Algebraic extends Function {
    public Algebraic(String name, Generic parameter[]) {
        super(name,parameter);
    }

    public abstract Root rootValue() throws NotRootException;

    public Generic antiderivative(int n) throws NotIntegrableException {
        return null;
    }
}

package jscl.math.function.bool;

import jscl.math.Generic;
import jscl.math.JSCLBoolean;
import jscl.math.Variable;

public class Implies extends Bool2 {
    public Implies(Generic expression1, Generic expression2) {
        super("implies",expression1,expression2);
    }

    protected JSCLBoolean apply(JSCLBoolean a, JSCLBoolean b) {
        return a.implies(b);
    }

    protected Variable newinstance() {
        return new Implies(null,null);
    }
}

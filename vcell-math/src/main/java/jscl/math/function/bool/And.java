package jscl.math.function.bool;

import jscl.math.Generic;
import jscl.math.JSCLBoolean;
import jscl.math.Variable;

public class And extends Bool2 {
    public And(Generic expression1, Generic expression2) {
        super("and",expression1,expression2);
    }

    protected JSCLBoolean apply(JSCLBoolean a, JSCLBoolean b) {
        return a.and(b);
    }

    protected Variable newinstance() {
        return new And(null,null);
    }
}

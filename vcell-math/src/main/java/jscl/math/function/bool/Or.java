package jscl.math.function.bool;

import jscl.math.Generic;
import jscl.math.JSCLBoolean;
import jscl.math.Variable;

public class Or extends Bool2 {
    public Or(Generic expression1, Generic expression2) {
        super("or",expression1,expression2);
    }

    protected JSCLBoolean apply(JSCLBoolean a, JSCLBoolean b) {
        return a.or(b);
    }

    protected Variable newinstance() {
        return new Or(null,null);
    }
}

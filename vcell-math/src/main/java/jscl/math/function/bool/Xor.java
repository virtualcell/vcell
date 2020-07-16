package jscl.math.function.bool;

import jscl.math.Generic;
import jscl.math.JSCLBoolean;
import jscl.math.Variable;

public class Xor extends Bool2 {
    public Xor(Generic expression1, Generic expression2) {
        super("xor",expression1,expression2);
    }

    protected JSCLBoolean apply(JSCLBoolean a, JSCLBoolean b) {
        return a.xor(b);
    }

    protected Variable newinstance() {
        return new Xor(null,null);
    }
}

package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.Variable;

public class Graph extends Operator {
    public Graph(Generic expression, Generic variable) {
        super("graph",new Generic[] {expression,variable});
    }

    public Generic compute() {
        Variable variable=parameter[1].variableValue();
        return parameter[0].function(variable);
    }

    public Generic expand() {
        return compute();
    }

    protected Variable newinstance() {
        return new Graph(null,null);
    }
}

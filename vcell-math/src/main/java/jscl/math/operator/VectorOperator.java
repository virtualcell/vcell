package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.Variable;
import jscl.math.function.Constant;

public abstract class VectorOperator extends Operator {
    public VectorOperator(String name, Generic parameter[]) {
        super(name,parameter);
    }

    protected Variable operator(String name) {
        return new Constant(name,0,subscript());
    }

    private Generic[] subscript() {
        Variable variable[]=variables(parameter[1].vectorValue());
        Generic subscript[]=new Generic[variable.length];
        for(int i=0;i<variable.length;i++) subscript[i]=variable[i].expressionValue();
        return subscript;
    }
}

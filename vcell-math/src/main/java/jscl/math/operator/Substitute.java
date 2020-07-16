package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JSCLVector;
import jscl.math.NotVectorException;
import jscl.math.Variable;

public class Substitute extends Operator {
    public Substitute(Generic expression, Generic variable, Generic value) {
        super("subst",new Generic[] {expression,variable,value});
    }

    public Generic compute() {
        try {
            Generic a=parameter[0];
            Variable variable[]=variables(parameter[1].vectorValue());
            Generic s[]=parameter[2].vectorValue().elements();
            for(int i=0;i<variable.length;i++) a=a.substitute(variable[i],s[i]);
            return a;
        } catch (final NotVectorException e) {
            Variable variable=parameter[1].variableValue();
            return parameter[0].substitute(variable,parameter[2]);
        }
    }

    public Generic expand() {
        return compute();
    }

    protected Variable newinstance() {
        return new Substitute(null,null,null);
    }
}

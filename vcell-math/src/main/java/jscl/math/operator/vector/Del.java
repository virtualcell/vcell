package jscl.math.operator.vector;

import jscl.math.Generic;
import jscl.math.JSCLVector;
import jscl.math.NotVectorException;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;
import jscl.math.operator.product.GeometricProduct;

public class Del extends VectorOperator {
    public Del(Generic vector, Generic variable, Generic algebra) {
        super("del",new Generic[] {vector,variable,algebra});
    }

    public Generic compute() {
        Variable variable[]=variables(parameter[1].vectorValue());
        int algebra[]=GeometricProduct.algebra(parameter[2]);
        try {
            JSCLVector vector=parameter[0].vectorValue();
            return vector.del(variable,algebra);
        } catch (final NotVectorException e) {}
        return expressionValue();
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        int n=3;
        if(parameter[2].signum()==0) n=2;
        buffer.append(name);
        buffer.append("(");
        for(int i=0;i<n;i++) {
            buffer.append(parameter[i]).append(i<n-1?", ":"");
        }
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public String toMathML() {
        StringBuffer b = new StringBuffer();
        b.append("<apply>");
        b.append(operator("nabla").toMathML());
        b.append(parameter[0].toMathML());
        if(parameter[2].signum()==0);
        else b.append(parameter[2].toMathML());
        b.append("</apply>");
        return b.toString();
    }

    protected Variable newinstance() {
        return new Del(null,null,null);
    }
}

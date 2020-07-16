package jscl.math.operator.product;

import jscl.math.Generic;
import jscl.math.JSCLVector;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;

public class VectorProduct extends VectorOperator {
    public VectorProduct(Generic vector1, Generic vector2) {
        super("vector",new Generic[] {vector1,vector2});
    }

    public Generic compute() {
        if(parameter[0] instanceof JSCLVector && parameter[1] instanceof JSCLVector) {
            JSCLVector v1=(JSCLVector)parameter[0];
            JSCLVector v2=(JSCLVector)parameter[1];
            return v1.vectorProduct(v2);
        }
        return expressionValue();
    }

    @Override
    public String toMathML() {
        StringBuffer b = new StringBuffer();
        b.append("<apply>");
        b.append("<mo>" + "\u2227" + "</mo>");
        for(int i=0;i<parameter.length;i++) {
            b.append(parameter[i].toMathML());
        }
        b.append("</apply>");
        return b.toString();
    }

    protected Variable newinstance() {
        return new VectorProduct(null,null);
    }
}

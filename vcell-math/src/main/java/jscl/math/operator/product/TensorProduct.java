package jscl.math.operator.product;

import jscl.math.Generic;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;

public class TensorProduct extends VectorOperator {
    public TensorProduct(Generic matrix1, Generic matrix2) {
        super("tensor",new Generic[] {matrix1,matrix2});
    }

    public Generic compute() {
        if(parameter[0] instanceof Matrix && parameter[1] instanceof Matrix) {
            Matrix m1=(Matrix)parameter[0];
            Matrix m2=(Matrix)parameter[1];
            return m1.tensorProduct(m2);
        }
        return expressionValue();
    }

    @Override
    public String toMathML() {
        StringBuffer b = new StringBuffer();
        b.append("<apply>");
        b.append("<mo>" + "\u2A2F" + "</mo>");
        for(int i=0;i<parameter.length;i++) {
            b.append(parameter[i].toMathML());
        }
        b.append("</apply>");
        return b.toString();
    }

    protected Variable newinstance() {
        return new TensorProduct(null,null);
    }
}

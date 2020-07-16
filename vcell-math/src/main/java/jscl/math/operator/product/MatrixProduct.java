package jscl.math.operator.product;

import jscl.math.Generic;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;

public class MatrixProduct extends VectorOperator {
    public MatrixProduct(Generic matrix1, Generic matrix2) {
        super("matrix",new Generic[] {matrix1,matrix2});
    }

    public Generic compute() {
        if(Matrix.product(parameter[0],parameter[1])) {
            return parameter[0].multiply(parameter[1]);
        }
        return expressionValue();
    }

    protected Variable newinstance() {
        return new MatrixProduct(null,null);
    }
}

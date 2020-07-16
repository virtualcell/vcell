package jscl.math.function;

import jscl.math.Generic;
import jscl.math.JSCLBoolean;
import jscl.math.NotIntegerException;
import jscl.math.NotIntegrableException;
import jscl.math.NumericWrapper;
import jscl.math.Variable;

public class Comparison extends Function {
    int operator;

    public Comparison(String name, Generic expression1, Generic expression2) {
        super(name,new Generic[] {expression1,expression2});
        for(int i=0;i<easo.length;i++) if(name.compareTo(easo[i])==0) operator=i;
    }

    public Generic antiderivative(int n) throws NotIntegrableException {
        throw new NotIntegrableException();
    }

    public Generic derivative(int n) {
        return JSCLBoolean.valueOf(false);
    }

    public Generic evaluate() {
        try {
            return JSCLBoolean.valueOf(compare(parameter[0].integerValue(),parameter[1].integerValue()));
        } catch (NotIntegerException e) {}
        return expressionValue();
    }

    public Generic evalelem() {
        return expressionValue();
    }

    public Generic evalsimp() {
        if (operator < 2) {
            return JSCLBoolean.valueOf(compare(parameter[0],parameter[1]));
	}
        return expressionValue();
    }

    public Generic evalfunc() {
        return new jscl.math.Function() {
            public double apply(double value) {
                return JSCLBoolean.valueOf(compare(Double.compare(((jscl.math.Function)parameter[0]).apply(value),((jscl.math.Function)parameter[1]).apply(value)))).content().doubleValue();
            }
        };
    }

    public Generic evalnum() {
        return new NumericWrapper(JSCLBoolean.valueOf(compare((NumericWrapper)parameter[0],(NumericWrapper)parameter[1])));
    }

    private boolean compare(Generic a1, Generic a2) {
        return compare(a1.compareTo(a2));
    }

    private boolean compare(int n) {
        switch(operator) {
            case 0:
                return n==0;
            case 1:
                return n!=0;
            case 2:
                return n<=0;
            case 3:
                return n<0;
            case 4:
                return n>=0;
            case 5:
                return n>0;
            case 6:
                return n==0;
            default:
                return false;
        }
    }

    protected Variable newinstance() {
        return new Comparison(name,null,null);
    }

    private static final String easo[]={"eq","neq","leq","lt","geq","gt","approx"};
}

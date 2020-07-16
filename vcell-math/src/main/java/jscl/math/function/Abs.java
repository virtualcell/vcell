package jscl.math.function;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.NotIntegrableException;
import jscl.math.NotVariableException;
import jscl.math.NumericWrapper;
import jscl.math.Variable;

public class Abs extends Function {
    public Abs(Generic generic) {
        super("abs",new Generic[] {generic});
    }

    public Generic antiderivative(int n) throws NotIntegrableException {
        return Constant.half.multiply(parameter[0]).multiply(new Abs(parameter[0]).evaluate());
    }

    public Generic derivative(int n) {
        return new Frac(
            parameter[0],
            expressionValue()
        ).evaluate();
    }

    public Generic evaluate() {
        if(parameter[0].signum()<0) {
            return new Abs(parameter[0].negate()).evaluate();
        }
        try {
            return parameter[0].integerValue().abs();
        } catch (NotIntegerException e) {}
        return expressionValue();
    }

    public Generic evalelem() {
        return new Sqrt(
            parameter[0].pow(2)
        ).evalelem();
    }

    public Generic evalsimp() {
        if(parameter[0].signum()<0) {
            return new Abs(parameter[0].negate()).evalsimp();
        }
        try {
            return parameter[0].integerValue().abs();
        } catch (NotIntegerException e) {}
        try {
            Variable v=parameter[0].variableValue();
            if(v instanceof Abs) {
                Function f=(Function)v;
                return f.evalsimp();
            }
        } catch (NotVariableException e) {}
        return expressionValue();
    }

    public Generic evalfunc() {
        return ((jscl.math.Function)parameter[0]).abs();
    }

    public Generic evalnum() {
        return ((NumericWrapper)parameter[0]).abs();
    }

    protected Variable newinstance() {
        return new Abs(null);
    }
}

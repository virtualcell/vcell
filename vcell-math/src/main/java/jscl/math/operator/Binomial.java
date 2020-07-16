package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.Variable;

public class Binomial extends Operator {
    public Binomial(Generic n, Generic p) {
        super("C",new Generic[] {n,p});
    }

    public Generic compute() {
        try {
            JSCLInteger n=parameter[0].integerValue();
            JSCLInteger p=parameter[1].integerValue();
            return n.factorial().divide(p.factorial().multiply(n.subtract(p).factorial()));
        } catch (NotIntegerException e) {}
        return expressionValue();
    }

    @Override
    public String toMathML() {
        StringBuffer b = new StringBuffer();
        b.append("<msubsup>");
        b.append("<mi>" + nameToMathML() + "</mi>");
        b.append(parameter[0].toMathML());
        b.append(parameter[1].toMathML());
        b.append("</msubsup>");
        return b.toString();
    }

    protected Variable newinstance() {
        return new Binomial(null,null);
    }
}

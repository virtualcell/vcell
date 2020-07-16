package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.NotVariableException;
import jscl.math.Variable;
import jscl.math.function.Frac;
import jscl.math.function.Pow;
import jscl.util.ArrayComparator;

public class Factorial extends Operator {
    public Factorial(Generic expression, Generic order) {
        super("",new Generic[] {expression, order});
    }

    public Factorial(Generic expression) {
        this(expression, JSCLInteger.valueOf(1));
    }

    public Generic compute() {
        int k=parameter[1].integerValue().intValue();
        try {
            return parameter[0].integerValue().factorial(k);
        } catch (NotIntegerException e) {}
        return expressionValue();
    }

    public int compareTo(Variable variable) {
        if(this==variable) return 0;
        int c=comparator.compare(this,variable);
        if(c<0) return -1;
        else if(c>0) return 1;
        else {
            Factorial v=(Factorial)variable;
            return ArrayComparator.comparator.compare(parameter,v.parameter);
        }
    }

    public String toString() {
        int k=parameter[1].integerValue().intValue();
        StringBuffer buffer=new StringBuffer();
        try {
            JSCLInteger en=parameter[0].integerValue();
            buffer.append(en);
        } catch (NotIntegerException e) {
            try {
                Variable v=parameter[0].variableValue();
                if(v instanceof Frac || v instanceof Pow) {
                    buffer.append(GenericVariable.valueOf(parameter[0]));
                } else buffer.append(v);
            } catch (NotVariableException e2) {
                buffer.append(GenericVariable.valueOf(parameter[0]));
            }
        }
        for(int i=0;i<k;i++) buffer.append("!");
        return buffer.toString();
    }

    public String toMathML() {
        int k=parameter[1].integerValue().intValue();
        StringBuffer b = new StringBuffer();
        b.append("<apply><factorial/>");
        b.append(parameter[0].toMathML());
        if(k>1) {
            b.append(JSCLInteger.valueOf(k).toMathML());
        }
        b.append("</apply>");
        return b.toString();
    }

    protected Variable newinstance() {
        return new Factorial(null,null);
    }
}

package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.Variable;

public class Derivative extends Operator {
    public Derivative(Generic expression, Generic variable, Generic value, Generic order) {
        super("d",new Generic[] {expression,variable,value,order});
    }

    public Generic compute() {
        Variable variable=parameter[1].variableValue();
        try {
            int n=parameter[3].integerValue().intValue();
            Generic a=parameter[0];
            for(int i=0;i<n;i++) {
                a=a.derivative(variable);
            }
            return a.substitute(variable,parameter[2]);
        } catch (NotIntegerException e) {}
        return expressionValue();
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        int n=4;
        if(parameter[3].compareTo(JSCLInteger.valueOf(1))==0) {
            n=3;
            if(parameter[2].compareTo(parameter[1])==0) n=2;
        }
        buffer.append(name);
        buffer.append("(");
        for(int i=0;i<n;i++) {
            buffer.append(parameter[i]).append(i<n-1?", ":"");
        }
        buffer.append(")");
        return buffer.toString();
    }

    public String toMathML() {
	StringBuffer b = new StringBuffer();
        if(parameter[2].compareTo(parameter[1])!=0) {
	    b.append("<apply>");
            b.append(derivationToMathML());
	    b.append(parameter[2].toMathML());
	    b.append("</apply>");
        } else {
            b.append(derivationToMathML());
        }
	return b.toString();
     }

    String derivationToMathML() {
        Variable v=parameter[1].variableValue();
	StringBuffer b = new StringBuffer();
	b.append("<apply><diff/><bvar><degree>");
        b.append(parameter[3].toMathML());
        b.append("</degree>");
        b.append(v.toMathML());
        b.append("</bvar>");
        b.append(parameter[0].toMathML());
	b.append("</apply>");
	return b.toString();
    }

    protected Variable newinstance() {
        return new Derivative(null,null,null,null);
    }
}

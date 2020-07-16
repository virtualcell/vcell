package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.Variable;

public class Limit extends Operator {
    public Limit(Generic expression, Generic variable, Generic limit, Generic direction) {
        super("limit",new Generic[] {expression,variable,limit,direction});
    }

    public Generic compute() {
        return expressionValue();
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        int n=4;
        if(parameter[3].signum()==0) n=3;
        buffer.append(name);
        buffer.append("(");
        for(int i=0;i<n;i++) {
            buffer.append(parameter[i]).append(i<n-1?", ":"");
        }
        buffer.append(")");
        return buffer.toString();
    }

    public String toMathML() {
        int c=parameter[3].signum();
        Variable v=parameter[1].variableValue();
        StringBuffer b = new StringBuffer();
        b.append("<apply><limit/><lowlimit>");
        if(c==0) b.append(parameter[2].toMathML());
        else {
            b.append("<msup>");
            b.append(parameter[2].toMathML());
            b.append("<mo>");
            b.append(c<0?"-":"+");
            b.append("</mo>");
            b.append("</msup>");
        }
        b.append("</lowlimit><bvar>");
        b.append(v.toMathML());
        b.append("</bvar>");
        b.append(parameter[0].toMathML());
        b.append("</apply>");
        return b.toString();
    }

    protected Variable newinstance() {
        return new Limit(null,null,null,null);
    }
}
